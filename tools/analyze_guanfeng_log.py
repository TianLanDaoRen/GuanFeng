#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
观风 · CSV 离线复盘工具（只用标准库，不依赖 pandas）

用法:
    python3 analyze_guanfeng_log.py <csv 文件或目录> ...

它回答四个问题：
  1) 这次会话记录了多少、有没有断档（进程被杀或熄屏压制的痕迹）
  2) 抓到了几段垂直运动（电梯 / 楼梯），各自位移多少米
  3) 解耦到底有没有生效——把「引擎给出的天气速率」与「对原始气压直接回归的朴素速率」
     逐点对照：朴素速率在高度事件期间会飙高，引擎速率应当纹丝不动
  4) 置信度闸门拦下了多少（可信 vs 窗口太短 / 趋势不稳）

输出是给人看的报告，可直接贴进复盘记录。
"""

import csv
import glob
import os
import sys

TICK_MS = 2000
GAP_FACTOR = 3
HPA_PER_METER = 0.12
NAIVE_WINDOW = 45  # 朴素速率用最近 45 个样本（2 秒采样即 90 秒）做回归


def load_rows(path):
    with open(path, newline="", encoding="utf-8") as handle:
        return [row for row in csv.DictReader(handle)]


def fnum(value, default=0.0):
    try:
        return float(value)
    except (TypeError, ValueError):
        return default


def inum(value, default=0):
    try:
        return int(float(value))
    except (TypeError, ValueError):
        return default


def naive_rate_hpa_per_hour(rows, index, window=NAIVE_WINDOW):
    """对原始气压直接做最小二乘回归——也就是「不做高度解耦」的朴素做法。"""
    start = max(0, index - window + 1)
    segment = rows[start:index + 1]
    if len(segment) < 3:
        return 0.0
    base = inum(segment[0]["timestamp_ms"])
    xs = [(inum(r["timestamp_ms"]) - base) / 1000.0 for r in segment]
    ys = [fnum(r["pressure_hpa"]) for r in segment]
    n = len(xs)
    mean_x = sum(xs) / n
    mean_y = sum(ys) / n
    numerator = sum((x - mean_x) * (y - mean_y) for x, y in zip(xs, ys))
    denominator = sum((x - mean_x) ** 2 for x in xs)
    if denominator == 0:
        return 0.0
    return numerator / denominator * 3600.0


def find_gaps(rows):
    gaps = []
    for prev, cur in zip(rows, rows[1:]):
        delta = inum(cur["timestamp_ms"]) - inum(prev["timestamp_ms"])
        if delta > TICK_MS * GAP_FACTOR:
            gaps.append((prev["clock"], cur["clock"], delta / 1000.0))
    return gaps


def find_elevation_episodes(rows):
    """elevation_events 是累计值，所以它递增的连续行就是一次垂直运动事件。"""
    episodes = []
    current = None
    last_count = inum(rows[0]["elevation_events"]) if rows else 0
    for index, row in enumerate(rows):
        count = inum(row["elevation_events"])
        if count > last_count:
            if current is None:
                # 位移起点要取事件发生「之前」那一行的高度，
                # 否则首行已经计入本次位移，会导致总位移少算一格。
                previous_m = (
                    fnum(rows[index - 1]["elevation_meters"]) if index > 0
                    else fnum(row["elevation_meters"])
                )
                current = {
                    "start": row["clock"],
                    "end": row["clock"],
                    "samples": 0,
                    "steps": 0,
                    "start_m": previous_m,
                    "end_m": fnum(row["elevation_meters"]),
                    "peak_naive": 0.0,
                    "peak_engine": 0.0,
                    "first_index": index,
                }
            current["end"] = row["clock"]
            current["samples"] += count - last_count
            current["end_m"] = fnum(row["elevation_meters"])
            current["steps"] += inum(row["steps"])
            current["last_index"] = index
        elif current is not None:
            episodes.append(current)
            current = None
        last_count = count
    if current is not None:
        episodes.append(current)

    for episode in episodes:
        for index in range(episode["first_index"], episode.get("last_index", episode["first_index"]) + 1):
            naive = abs(naive_rate_hpa_per_hour(rows, index))
            engine = abs(fnum(rows[index]["rate_hpa_per_hour"]))
            episode["peak_naive"] = max(episode["peak_naive"], naive)
            episode["peak_engine"] = max(episode["peak_engine"], engine)
    return episodes


def report(path):
    rows = load_rows(path)
    if not rows:
        print(f"\n=== {os.path.basename(path)} ===\n空文件，跳过")
        return

    span_seconds = (inum(rows[-1]["timestamp_ms"]) - inum(rows[0]["timestamp_ms"])) / 1000.0
    print(f"\n=== {os.path.basename(path)} ===")
    print(f"样本 {len(rows)} 行 · 覆盖 {span_seconds / 60:.1f} 分钟 · "
          f"{rows[0]['clock']} → {rows[-1]['clock']}")

    pressures = [fnum(r["pressure_hpa"]) for r in rows]
    print(f"气压 {min(pressures):.2f} ~ {max(pressures):.2f} hPa（跨度 {max(pressures) - min(pressures):.2f}）")

    gaps = find_gaps(rows)
    if gaps:
        print(f"断档 {len(gaps)} 处（进程被压制或被杀的证据）：")
        for start, end, seconds in gaps[:10]:
            print(f"  {start} → {end}  中断 {seconds:.0f} 秒")
    else:
        print("断档 0 处 —— 全程连续")

    confidence_counts = {}
    for row in rows:
        key = row.get("confidence") or "未知"
        confidence_counts[key] = confidence_counts.get(key, 0) + 1
    total = len(rows)
    summary = " · ".join(
        f"{key} {value}（{value / total * 100:.0f}%）"
        for key, value in sorted(confidence_counts.items(), key=lambda kv: -kv[1])
    )
    print(f"置信度分布：{summary}")

    episodes = find_elevation_episodes(rows)
    print(f"垂直运动事件：{len(episodes)} 段")
    for index, episode in enumerate(episodes, start=1):
        delta_m = episode["end_m"] - episode["start_m"]
        direction = "上升" if delta_m >= 0 else "下降"
        print(f"  第 {index} 段 {episode['start']} → {episode['end']} · "
              f"{episode['samples']} 个样本 · 位移 {delta_m:+.1f} 米（{direction}）· 步数 {episode['steps']}")
        print(f"      朴素估算峰值 {episode['peak_naive']:.1f} hPa/h"
              f"  ↔  引擎输出峰值 {episode['peak_engine']:.2f} hPa/h")
        if episode["peak_naive"] > 5 and episode["peak_engine"] < 1.5:
            print("      ✓ 解耦生效：朴素做法会误报剧变，引擎把它按住了")
        elif episode["peak_naive"] > 5:
            print("      ⚠ 解耦可能不完整：引擎速率仍然偏高，值得复查")
        else:
            print("      · 朴素速率本身不高，这段事件偏温和（例如缓慢步行）")

    grade_counts = {}
    for row in rows:
        key = row["grade"]
        grade_counts[key] = grade_counts.get(key, 0) + 1
    print("引擎判定分布：" + " · ".join(f"{k} {v}" for k, v in sorted(grade_counts.items(), key=lambda kv: -kv[1])))


def main(argv):
    targets = argv[1:] or ["."]
    files = []
    for target in targets:
        if os.path.isdir(target):
            # 递归扫描：adb pull 一个目录时会多套一层 files/，不能只扫一层
            for root, _dirs, names in os.walk(target):
                files.extend(
                    os.path.join(root, name)
                    for name in sorted(names)
                    if name.endswith(".csv")
                )
        elif os.path.isfile(target):
            files.append(target)
    if not files:
        print("没找到 CSV 文件")
        return 1
    for path in files:
        report(path)
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
