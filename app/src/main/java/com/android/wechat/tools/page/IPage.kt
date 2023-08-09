package com.android.wechat.tools.page

import kotlinx.coroutines.delay

interface IPage {

    fun delayTime() = 100L

    fun pageClassName(): String

    fun pageTitleName(): String

    fun isMe(): Boolean

    suspend fun <T>delayAction(delayMillis: Long = delayTime(), block: suspend () -> T): T {
        delay(delayMillis)
        return block()
    }
}