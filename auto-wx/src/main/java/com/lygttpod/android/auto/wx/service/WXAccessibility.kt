package com.lygttpod.android.auto.wx.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.MutableLiveData
import com.android.accessibility.ext.AsyncAccessibilityService
import com.lygttpod.android.auto.wx.helper.DeleteTaskHelper
import com.lygttpod.android.auto.wx.helper.HBTaskHelper
import java.util.concurrent.atomic.AtomicBoolean

val wxAccessibilityServiceLiveData = MutableLiveData<AccessibilityService?>(null)
val wxAccessibilityService: AccessibilityService? get() = wxAccessibilityServiceLiveData.value

class WXAccessibility : AsyncAccessibilityService() {

    companion object {
        var isInWXApp = AtomicBoolean(false)
    }

    override fun targetPackageName() = "com.tencent.mm"

    override fun onCreate() {
        super.onCreate()
        wxAccessibilityServiceLiveData.value = this
    }

    override fun onDestroy() {
        wxAccessibilityServiceLiveData.value = null
        super.onDestroy()
    }

    override fun asyncHandleAccessibilityEvent(event: AccessibilityEvent) {
        HBTaskHelper.hbTask(event)
        DeleteTaskHelper.handle(event)
    }
}