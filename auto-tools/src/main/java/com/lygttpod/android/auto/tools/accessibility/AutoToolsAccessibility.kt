package com.lygttpod.android.auto.tools.accessibility

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo.TYPE_SYSTEM
import com.android.accessibility.ext.AsyncAccessibilityService

class AutoToolsAccessibility : AsyncAccessibilityService() {

    companion object {
        var autoToolsAccessibility: AutoToolsAccessibility? = null

        var floatWindowPackageName: String? = null
        fun getRootWindowNodeInfo(): AccessibilityNodeInfo? {
            if (floatWindowPackageName.isNullOrBlank()) return autoToolsAccessibility?.rootInActiveWindow
            return autoToolsAccessibility?.windows?.filter { it.type == TYPE_SYSTEM }
                ?.lastOrNull { it.root?.packageName == floatWindowPackageName }?.root
        }
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