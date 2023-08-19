package com.android.wechat.tools.service

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.MutableLiveData
import com.android.accessibility.ext.AsyncAccessibilityService
import com.android.accessibility.ext.acc.isNotificationStateChanged
import com.android.wechat.tools.helper.HBTaskHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

val wxHbAccessibilityServiceLiveData = MutableLiveData<AccessibilityService?>(null)
val wxHbAccessibilityService: AccessibilityService? get() = wxHbAccessibilityServiceLiveData.value

class WxHbAccessibility : AsyncAccessibilityService() {

    companion object {
        val eventFlow = MutableStateFlow<AccessibilityEvent?>(null)
    }

    override fun targetPackageName() = "com.tencent.mm"

    override fun onCreate() {
        super.onCreate()
        wxHbAccessibilityServiceLiveData.value = this
    }

    override fun onDestroy() {
        wxHbAccessibilityServiceLiveData.value = null
        super.onDestroy()
    }

    override fun onServiceConnected() {
        GlobalScope.launch { HBTaskHelper.start() }
    }

    override fun asyncHandleAccessibilityEvent(event: AccessibilityEvent) {
        Log.d("WxHbAccessibility", "==========$event")
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
            GlobalScope.launch { eventFlow.emit(event) }
        }
    }

    private fun AccessibilityEvent.isHBEvent() =
        text.firstOrNull()?.contains(": [微信红包]恭喜发财，大吉大利") == true
}