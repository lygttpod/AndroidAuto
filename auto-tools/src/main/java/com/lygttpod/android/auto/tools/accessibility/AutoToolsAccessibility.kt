package com.lygttpod.android.auto.tools.accessibility

import android.view.accessibility.AccessibilityEvent
import com.android.accessibility.ext.AsyncAccessibilityService

class AutoToolsAccessibility : AsyncAccessibilityService() {

    companion object {
        var autoToolsAccessibility: AutoToolsAccessibility? = null
    }

    override fun targetPackageName() = ""

    override fun onCreate() {
        super.onCreate()
        autoToolsAccessibility = this
    }

    override fun onDestroy() {
        autoToolsAccessibility = null
        super.onDestroy()
    }

    override fun asyncHandleAccessibilityEvent(event: AccessibilityEvent) {
    }
}