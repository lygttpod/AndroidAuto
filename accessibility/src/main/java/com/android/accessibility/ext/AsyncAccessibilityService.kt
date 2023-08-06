package com.android.accessibility.ext

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import java.util.concurrent.Executors

abstract class AsyncAccessibilityService : AccessibilityService() {
    private val TAG = this::class.java.simpleName

    private val executors = Executors.newSingleThreadExecutor()

    abstract fun targetPackageName(): String

    abstract fun asyncHandleAccessibilityEvent(event: AccessibilityEvent)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val accessibilityEvent = event ?: return
        executors.run { asyncHandleAccessibilityEvent(accessibilityEvent) }
    }

    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt: ")
    }

    override fun onServiceConnected() {
        Log.d(TAG, "onServiceConnected: ")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
    }
}