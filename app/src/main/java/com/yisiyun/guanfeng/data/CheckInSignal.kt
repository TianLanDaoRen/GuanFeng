package com.yisiyun.guanfeng.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 打卡事件信号。
 *
 * 存在的原因：关联视图要读最近 7 天的历史并折叠成小时桶（约 12 万行原始样本），
 * 代价不小，所以做了 10 分钟缓存。但**打卡是用户此刻的动作**，
 * 打完之后关联图必须立刻把新的点画上去——不能等缓存过期，
 * 更不能要求用户退出应用重进（这个应用正常情况下一直活着）。
 *
 * 所以用一个版本号把「有新打卡」这件事广播出去，关联页据此立即重算。
 */
object CheckInSignal {

    private val _version = MutableStateFlow(0)
    val version: StateFlow<Int> = _version.asStateFlow()

    fun notifyRecorded() {
        _version.value += 1
    }
}
