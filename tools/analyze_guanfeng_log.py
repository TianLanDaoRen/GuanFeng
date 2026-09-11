#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
观风 · CSV 离线复盘工具（只用标准库，不依赖 pandas）

用法:
    python3 analyze_guanfeng_log.py <csv 文件或目录> ...

它回答四个问题：
  1) 这次会话记录了多少、有没有断档（进程被杀或熄屏压制的痕迹）
  2) 抓到几段垂直行程（电梯 / 楼梯），各自位移多少米、步态如何
  3) 解耦到底有没有生效——把「引擎给出的天气速率」与「对原始气压直接回归的朴素速率」
     逐点对照：朴素速率在行程期间会飙高，引擎速率应当纹丝不动
  4) 置信度闸门拦下了多少

关于切段的教训（别再踩）：
第一版按 `elevation_events` 计数增长来切段——错的。平地段噪声也会让计数器缓慢增长，
于是多段行程被粘成一段、起止位移互相抵消，报出「位移 -0.0 米」这种荒谬结论。
正确做法是对「解耦位移曲线」做 ZigZag 拐点检测（阈值 2 米），只认显著行程。
"""

import csv
import os
import sys

TICK_MS = 2000
GAP_FACTOR = 3
HPA_PER_METER = 0.12
NAIVE_WINDOW = 45      # 朴素速率用最近 45 个样本（2 秒采样即 90 秒）做回归
MIN_TRAVEL_M = 2.0     # ZigZag 拐点阈值：小于 2 米的往复视为噪声
ELEVATOR_STEP_RATE = 0.15  # 步/秒 低于此值判为无步态（电梯）


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


def sparkline(values, width=58):
    """把一串数值压成一行字符，用于在终端里直接看时序形状。"""
    if not values:
        return ""
    lo, hi = min(values), max(values)
    chars = "▁▂▃▄▅▆▇█"
    if hi - lo < 1e-9:
        return chars[0] * min(len(values), width)
    step = max(1, len(values) // width)
    sampled = values[::step][:width]
    return "".join(chars[int((v - lo) / (hi - lo) * 7.999)] for v in sampled)


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


def find_travel_legs(rows, min_travel_m=MIN_TRAVEL_M):
    """
    对「解耦位移曲线」做 ZigZag 拐点检测，把会话切成一段段行程。
    不要改用 elevation_events 增长来切——那会被平地噪声粘成一段（见文件头教训）。
    """
    meters = [fnum(r["elevation_meters"]) for r in rows]
    if len(meters) < 3:
        return []

    direction = 1 if meters[-1] >= meters[0] else -1
    extreme = meters[0]
    extreme_index = 0
    points = []
    for index, value in enumerate(meters):
        if direction > 0:
            if value > extreme:
                extreme, extreme_index = value, index
            elif extreme - value >= min_travel_m:
                points.append((extreme_index, extreme))
                direction = -1
                extreme, extreme_index = value, index
        else:
            if value < extreme:
                extreme, extreme_index = value, index
            elif value - extreme >= min_travel_m:
                points.append((extreme_index, extreme))
                direction = 1
                extreme, extreme_index = value, index
    points.append((extreme_index, extreme))

    legs = []
    for order in range(1, len(points)):
        start_index, start_value = points[order - 1]
        end_index, end_value = points[order]
        segment = rows[start_index:end_index + 1]
        if not segment:
            continue
        duration_s = max(
            1.0,
            (inum(rows[end_index]["timestamp_ms"]) - inum(rows[start_index]["timestamp_ms"])) / 1000.0,
        )
        steps = sum(inum(r["steps"]) for r in segment)
        # 用「步/秒」而非步数绝对值判别：电梯里也会被系统计到零星假步（实测 206 秒 12 步）。
        step_rate = steps / duration_s
        leg = {
            "start": rows[start_index]["clock"],
            "end": rows[end_index]["clock"],
            "start_index": start_index,
            "end_index": end_index,
            "delta_m": end_value - start_value,
            "steps": steps,
            "duration_s": duration_s,
            "step_rate": step_rate,
            "peak_accel": max(fnum(r["vertical_accel"]) for r in segment),
            "kind": "电梯/无步态" if step_rate < ELEVATOR_STEP_RATE else "楼梯/有步态",
            "peak_naive": 0.0,
            "peak_engine": 0.0,
            "grades": {},
        }
        for index in range(start_index, end_index + 1):
            leg["peak_naive"] = max(leg["peak_naive"], abs(naive_rate_hpa_per_hour(rows, index)))
            leg["peak_engine"] = max(leg["peak_engine"], abs(fnum(rows[index]["rate_hpa_per_hour"])))
            grade = rows[index]["grade"]
            leg["grades"][grade] = leg["grades"].get(grade, 0) + 1
        legs.append(leg)
    return legs


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
    print(f"气压 {min(pressures):.2f} ~ {max(pressures):.2f} hPa"
          f"（总落差 {max(pressures) - min(pressures):.2f} hPa"
          f" ≈ {(max(pressures) - min(pressures)) / HPA_PER_METER:.0f} 米垂直量程）")
    print(f"气压时序  {sparkline(pressures)}")

    meters = [fnum(r["elevation_meters"]) for r in rows]
    if max(meters) - min(meters) > 0.5:
        print(f"解耦位移  {sparkline(meters)}  （{min(meters):+.1f} ~ {max(meters):+.1f} 米）")

    steps_series = [inum(r["steps"]) for r in rows]
    if max(steps_series) > 0:
        print(f"每窗步数  {sparkline(steps_series)}")

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
    print("置信度分布：" + " · ".join(
        f"{key} {value}（{value / total * 100:.0f}%）"
        for key, value in sorted(confidence_counts.items(), key=lambda kv: -kv[1])))

    legs = find_travel_legs(rows)
    print(f"垂直行程：{len(legs)} 段")
    for index, leg in enumerate(legs, start=1):
        direction = "上升" if leg["delta_m"] >= 0 else "下降"
        print(f"  第 {index} 段 {leg['start']} → {leg['end']} · {leg['kind']} · "
              f"{leg['duration_s']:.0f} 秒 · 位移 {leg['delta_m']:+.1f} 米（{direction}）· "
              f"步数 {leg['steps']}（{leg['step_rate']:.2f} 步/秒）· 峰值加速度 {leg['peak_accel']:.2f}")
        print(f"      朴素估算峰值 {leg['peak_naive']:.1f} hPa/h"
              f"  ↔  引擎输出峰值 {leg['peak_engine']:.2f} hPa/h")
        grade_summary = " · ".join(f"{k} {v}" for k, v in sorted(
            leg["grades"].items(), key=lambda kv: -kv[1]))
        print(f"      行程期间引擎判定：{grade_summary}")
        if "急降" in leg["grades"] or "缓降" in leg["grades"] or "急升" in leg["grades"] or "缓升" in leg["grades"]:
            print("      ⚠ 行程期间出现「升降」类判定——解耦可能不完整或被噪声干扰")
        else:
            print("      ✓ 行程期间判定未被高度变化带偏")
        if leg["peak_naive"] > 5 and leg["peak_engine"] < 1.5:
            print("      ✓ 解耦生效：朴素做法会误报剧变，引擎把它按住了")

    grade_counts = {}
    for row in rows:
        grade_counts[row["grade"]] = grade_counts.get(row["grade"], 0) + 1
    print("引擎判定分布：" + " · ".join(
        f"{k} {v}" for k, v in sorted(grade_counts.items(), key=lambda kv: -kv[1])))

    closed = abs(meters[-1] - meters[0])
    print(f"往返闭合误差 {closed:.1f} 米（回到起点则接近 0）")


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
