package com.lygttpod.android.auto.wx.helper

import android.app.Notification
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.android.accessibility.ext.acc.isNotificationStateChanged
import com.lygttpod.android.auto.wx.page.WXHBPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object HBTaskHelper {

    var enableFuckMoney = false

    private val mutex = Mutex()
    private val eventFlow = MutableStateFlow<AccessibilityEvent?>(null)

    private val hbTaskScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun autoFuckMoney(enable: Boolean) {
        enableFuckMoney = enable
        if (enableFuckMoney) {
            hbTaskScope.launch {
                eventFlow.buffer().collect {
                    mutex.withLock {
                        WXHBPage.fuckMoney()
                    }
                }
            }
        } else {
            hbTaskScope.cancel("关闭自动抢红包功能")
        }
    }

    fun hbTask(event: AccessibilityEvent) {
        if (!enableFuckMoney) return
        if (event.isNotificationStateChanged()) {
            if (event.isHBEvent()) {
                (event.parcelableData as? Notification)?.let {
                    try {
                        it.contentIntent.send()
                        Log.d("WxHbAccessibility", "点击了通知")
                    } catch (e: Exception) {
                        Log.d("WxHbAccessibility", "点击通知异常：${e.message}")
                    }
                }
            }
        } else {
            hbTaskScope.launch { eventFlow.emit(event) }
        }
    }

    private fun AccessibilityEvent.isHBEvent() =
        text.firstOrNull()?.contains(": [微信红包]恭喜发财，大吉大利") == true
}