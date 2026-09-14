package com.yisiyun.guanfeng.core

/**
 * 观风 · 风雨判定规则。
 *
 * 诚实声明（重要）：
 * 下面的映射是**基于气压趋势与降水之间定性关系的启发式规则**，
 * 没有用任何真实降水观测数据校准过，因此 calibrated 恒为 false。
 * 它给的是「高低倾向 + 依据」，不是一个可信的概率数字。
 * 若将来要用真实数据校准，只需替换本文件，内核不必改动。
 */
enum class RainLikelihood(val label: String) {
    HIGH("高"),
    MEDIUM("中"),
    LOW("低"),
    UNKNOWN("未知")
}

data class WeatherAssessment(
    val likelihood: RainLikelihood,
    /** 主屏用的一句话，必须短到 189dp 宽不会折行（≤6 字）。 */
    val shortReason: String,
    /** 完整依据——放诊断页（记录页），主屏放不下。 */
    val rationale: String,
    val calibrated: Boolean = false,
) {
    /**
     * 行动建议：**由倾向推导，不是自己存一份**。
     *
     * 2026-09-14 连续两次踩同一个坑：修了「高 + 备把伞」，紧接着又出现「低 + 风起云涌」——
     * 因为建议虽然走了单一映射 `adviceFor`，但**每个分支都手写一遍倾向字面量**，
     * 改了 likelihood 忘了改 advice 就又脱钩。
     * 现在让它成为**计算属性**：类型系统保证"倾向与建议永远一致"，不可能再忘。
     */
    val advice: String get() = WeatherRule.adviceFor(likelihood)
}

object WeatherRule {

    /**
     * **建议文案由倾向唯一决定** —— 这是硬规矩。
     *
     * 2026-09-14 真机截图出现过「高 + 备把伞」：倾向被佐证升档抬到 HIGH，
     * 而建议还留在"过程锁存时那条分支"给的 MEDIUM 文案上 ✗。
     * 根因是**建议散落在各个分支里各写各的**。现在只留这一处映射，
     * 任何分支（含佐证升档）都必须经过它 —— 单测钉死"一一对应"。
     *
     * 措辞也按主人的判断定：**红色（急降）才可能下雨**，黄段（缓降）只是"天在变"，
     * 所以 MEDIUM 不再说"备把伞"，改说"风起云涌"。
     */
    fun adviceFor(likelihood: RainLikelihood): String = when (likelihood) {
        RainLikelihood.HIGH -> "带伞"
        RainLikelihood.MEDIUM -> "风起云涌"
        RainLikelihood.LOW -> "暂无需带伞"
        RainLikelihood.UNKNOWN -> "再等等"
    }

    /** 净降幅达到此值即按「暴风定律」的门槛给高倾向。 */
    private const val BIG_FALL_HPA = 6.0f

    /** 净降幅达到此值按中等倾向处理。 */
    private const val MODERATE_FALL_HPA = 3.0f

    /** 解除所需回升幅度，与 WeatherEpisodeTracker 的默认值保持一致（迟滞门限同量级）。 */
    private const val CLEAR_RISE_HPA = 1.5f

    /** 路径效率低于此值即认为"气压在来回振荡"，净变幅不代表真实趋势。 */
    private const val MIN_PATH_EFFICIENCY = 0.5f


    /**
     * 置信闸门：置信度不是 OK 就直接给「未知」。
     *
     * 实测教训：曾经出现过「窗口 0.14 分钟、R² 0.029，却报出缓升 1.382 hPa/h」的假警报。
     * 与其在 UI 上标注一堆免责说明，不如在源头拒绝给出结论。
     *
     * 文案取舍：主屏只有 189×248 dp，长句会折行、把布局顶乱。
     * 因此 shortReason 控制在 6 字以内，完整依据挪到记录页。
     */
    fun assess(
        trend: TrendResult,
        /**
         * 最近若干小时内「天气分量」的净降幅（从区间最高点到现在的差，≤0）。
         *
         * 为什么要单独给这个量：**气压降完转入长时间平稳，恰恰是雨正在下的典型形态**。
         * 只看窗口内斜率的话，随着窗口推移会把"曾经降过 4 hPa"忘得一干二净，
         * 于是报出「平稳 · 无需带伞」——而外面正在下雨。主人指出的正是这个边界。
         */
        recentFallHpa: Float? = null,
        /**
         * 天气过程状态（主人提出的模型）：一旦气压降幅越过门限就进入「可能下雨」，
         * **一直挂着直到气压自最低点明显回升**——与过了多久无关。
         * 这比"看最近 N 小时的降幅"更贴近事实：下雨是有始有终的过程，不是窗口统计。
         */
        episode: WeatherEpisode? = null,
    ): WeatherAssessment {
        // ⓪ 过程之中：直接给结论，不被"此刻刚好平稳"带偏。
        //    这正是主人指出的边界——降完转平很可能正在下雨，不能报「无需带伞」。
        if (episode?.active == true) {
            val drop = episode.dropHpa
            val high = drop <= -BIG_FALL_HPA
            return WeatherAssessment(
                likelihood = if (high) RainLikelihood.HIGH else RainLikelihood.MEDIUM,
                shortReason = if (high) "已降幅较大" else "可能下雨",
                // **整段拼接完再 format**。
                // 原来写成 "a" + "b" + "c".format(...) —— Kotlin 里 `.format` 只绑到
                // 紧邻的那个字面量，前面的 `+` 拼接没被格式化，于是真机上原样显示
                // "累计下降 %.1f hPa"。这种错**编译期不会报、单测也不一定覆盖**，
                // 只有真机看一眼才发现。凡是多段拼出来的 format 模板，一律加括号。
                rationale = (
                    "本轮过程已累计下降 %.1f hPa，尚未出现足够回升" +
                        "（自最低点回升 %.1f hPa 才解除）。下雨是有始有终的过程，" +
                        "在解除之前一直维持提醒，不被此刻的瞬时平稳带偏"
                    ).format(drop, CLEAR_RISE_HPA)
            )
        }
        // ① 数据不够：这两条无论何时都不给结论
        if (trend.confidence == TrendConfidence.SHORT_WINDOW ||
            trend.confidence == TrendConfidence.INSUFFICIENT
        ) {
            return WeatherAssessment(
                likelihood = RainLikelihood.UNKNOWN,
                shortReason = if (trend.confidence == TrendConfidence.SHORT_WINDOW) {
                    "样本未铺满"
                } else {
                    "样本不足"
                },
                rationale = if (trend.confidence == TrendConfidence.SHORT_WINDOW) {
                    "窗口只覆盖 %.0f%%，样本还没铺满，斜率不可信".format(trend.coverageFraction * 100f)
                } else {
                    "有效样本 ${trend.weatherSamples} 个，还不足以判断趋势"
                },
            )
        }

        /*
         * ② 净变幅优先于斜率。
         *
         * R² 衡量的是"气压曲线有多接近一条直线"，而**先急降后转平**这条最典型的降雨曲线
         * 恰恰不直——于是旧逻辑会把最该报警的形态判成「抖动过大」，进而退回速评，
         * 甚至直接说「无需带伞」。而"降了多少"这个量根本不需要线性拟合。
         *
         * 判据来源也支持这么做：气象学「暴风定律」说的是"3 小时降 4 hPa"这个**净降量**，
         * 维基说的「气压变化超过 3.5 hPa」同样是净量。斜率只用来回答"此刻降得多快"。
         */
        val netChange = trend.observedDeltaHpa
        val recentFall = recentFallHpa ?: 0f
        val worstFall = minOf(netChange, recentFall)
        // 路径效率：净变幅 ÷ 路程。先降后平 ≈ 1，来回振荡 ≈ 0。
        // 只看净变幅会把振荡噪声也当成急降报出去——这是捷径式实现的典型坑。
        val pathEfficiency = if (trend.pathLengthHpa > 0.01f) {
            kotlin.math.abs(netChange) / trend.pathLengthHpa
        } else {
            1f
        }
        val netTrustworthy = pathEfficiency >= MIN_PATH_EFFICIENCY

        if (netTrustworthy && worstFall <= -BIG_FALL_HPA) {
            return WeatherAssessment(
                likelihood = RainLikelihood.HIGH,
                shortReason = if (recentFall < netChange) "已降幅较大" else "窗口内急降",
                // 整段加括号：`.format` 只绑紧邻字面量（本文件踩过两次，别删这对括号）
                rationale = (
                    "窗口内净降 %.1f hPa，最近数小时累计降幅 %.1f hPa，" +
                        "已达「暴风定律」的 %.0f hPa 门槛（该定律描述的是净降量，不是瞬时斜率）"
                    ).format(netChange, recentFall, BIG_FALL_HPA)
            )
        }
        if (netTrustworthy && worstFall <= -MODERATE_FALL_HPA) {
            return WeatherAssessment(
                likelihood = RainLikelihood.MEDIUM,
                shortReason = if (recentFall < netChange) "已降幅偏大" else "气压缓降",
                rationale = (
                    "窗口内净降 %.1f hPa，最近数小时累计降幅 %.1f hPa，" +
                        "天气有转坏倾向"
                    ).format(netChange, recentFall)
            )
        }

        // ③ R² 只用来决定"能不能谈斜率"——净变幅已在上一步处理完
        if (trend.confidence == TrendConfidence.NOISY && !netTrustworthy) {
            // 来回振荡：路程远大于净变幅，斜率与净量都不可信 → 诚实地说未知
            return WeatherAssessment(
                likelihood = RainLikelihood.UNKNOWN,
                shortReason = "抖动过大",
                // 【必须整段加括号】`.format` 只作用于**紧邻的那个字面量**：
                // 写成 "A" + "B".format(...) 时，A 里的 %.1f 会原样打到手表上。
                // 这条坑本项目踩过两次（上一次在净变幅那条），所以现在有守卫测试：
                // WeatherRuleRationaleTest 会遍历各分支断言文案里不许残留 %。
                rationale = (
                    "窗口内气压在来回振荡（路径 %.1f hPa 而净变 %.1f hPa，效率 %.0f%%），" +
                        "既算不出可信斜率、净量也不代表真实趋势"
                    ).format(trend.pathLengthHpa, netChange, pathEfficiency * 100)
            )
        }
        if (trend.confidence == TrendConfidence.NOISY) {
            // 路径干净但曲线不直——典型的"降完转平"：净量不大且已停止恶化，
            // 这时给"已转平稳"比退回速评更诚实。
            return WeatherAssessment(
                likelihood = RainLikelihood.LOW,
                shortReason = "已转平稳",
                rationale = "窗口内气压曲线不接近直线（R² %.2f），但净变幅只有 %.1f hPa，" +
                    "说明此前的变化已结束、当前没有继续恶化"
                        .format(trend.fitRSquared, netChange)
            )
        }

        return when (trend.grade) {
            TrendGrade.FALLING_FAST -> WeatherAssessment(
                // 【2026-09-14 修】兜底分支曾经按 grade（速率）直接给 HIGH/MEDIUM，
                // 于是它**绕过上面按文献定的 3 小时门限**：
                // 实测 3h 净变 −1.69（按门限属平稳）、近段跌幅 0、无过程，
                // 但 rate = −0.56 hPa/h 落进 FALLING → 直接判中度。
                // 现在兜底一律只给 LOW，理由按实际幅度说清楚。
                likelihood = RainLikelihood.LOW,
                shortReason = "气压急降但未达判据",
                rationale = "3 小时变压 %.1f hPa，气压在急降，通常对应低压槽或强对流逼近。" +
                    "（判据来源：气象学「暴风定律」——3 小时降 4 hPa 即风暴前兆；" +
                    "维基百科亦载气压变化超过 3.5 hPa 时天气变化可期）"
                    .format(trend.deltaHpaPer3h)
            )

            TrendGrade.FALLING -> WeatherAssessment(
                likelihood = RainLikelihood.LOW,
                shortReason = "气压缓降但未达判据",
                rationale = "3 小时变压 %.1f hPa，气压缓降，天气有转坏倾向"
                    .format(trend.deltaHpaPer3h)
            )

            TrendGrade.STEADY -> WeatherAssessment(
                likelihood = RainLikelihood.LOW,
                shortReason = "气压平稳",
                // 建议原先写「无变化」——与趋势评级「平稳」是同义重复，主人一眼看出。
                // 改成行动导向的说法，才配得上占一个 14sp 的位置。
                rationale = "3 小时变压 %.1f hPa，气压平稳"
                    .format(trend.deltaHpaPer3h)
            )

            TrendGrade.RISING -> WeatherAssessment(
                likelihood = RainLikelihood.LOW,
                shortReason = "气压回升",
                rationale = "3 小时变压 %+.1f hPa，气压回升，天气趋稳"
                    .format(trend.deltaHpaPer3h)
            )

            TrendGrade.RISING_FAST -> WeatherAssessment(
                likelihood = RainLikelihood.LOW,
                shortReason = "气压急升",
                rationale = "3 小时变压 %+.1f hPa，气压急升，多为冷空气过境后转晴"
                    .format(trend.deltaHpaPer3h)
            )

            TrendGrade.INSUFFICIENT -> WeatherAssessment(
                likelihood = RainLikelihood.UNKNOWN,
                shortReason = "样本不足",
                rationale = "有效样本 ${trend.weatherSamples} 个，还不足以判断趋势"
            )
        }
    }
}
