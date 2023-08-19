package com.android.wechat.tools.helper

import com.android.wechat.tools.page.WXHBPage
import com.android.wechat.tools.service.WxHbAccessibility
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object HBTaskHelper {
    private val mutex = Mutex()
    suspend fun start() {
        WxHbAccessibility.eventFlow.buffer().collect {
            mutex.withLock {
                WXHBPage.fuckMoney()
            }
        }
    }
}