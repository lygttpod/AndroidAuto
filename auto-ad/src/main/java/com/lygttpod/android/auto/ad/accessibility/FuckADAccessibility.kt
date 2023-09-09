package com.lygttpod.android.auto.ad.accessibility

import android.view.accessibility.AccessibilityEvent
import com.android.accessibility.ext.AsyncAccessibilityService
import com.lygttpod.android.auto.ad.task.FuckADTask

class FuckADAccessibility : AsyncAccessibilityService() {

    companion object {
        var fuckADAccessibility: FuckADAccessibility? = null
    }

    override fun targetPackageName() = ""

    override fun onCreate() {
        super.onCreate()
        fuckADAccessibility = this
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        FuckADTask.analysisAppConfig()
    }
    override fun onDestroy() {
        fuckADAccessibility = null
        super.onDestroy()
    }

    override fun asyncHandleAccessibilityEvent(event: AccessibilityEvent) {
//        Log.d("FuckADAccessibility", "asyncHandleAccessibilityEvent: ${event}")
        FuckADTask.fuckAD(event)
    }
}