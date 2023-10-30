package com.lygttpod.android.auto.wx.helper

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.lygttpod.android.auto.wx.page.WXDeleteDialogPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object DeleteTaskHelper {
    private const val TAG = "DeleteTaskHelper"

    private var isEnabled = false

    private val mutex = Mutex()
    private val eventFlow = MutableStateFlow<AccessibilityEvent?>(null)

    private var scope: CoroutineScope? = null

    fun enable(enable: Boolean) {
        isEnabled = enable
        if (isEnabled) {
            Log.d(TAG, "打开任务: DeleteTaskHelper")
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            scope?.launch {
                eventFlow.buffer().collect {
                    mutex.withLock {
                        WXDeleteDialogPage.handleEvent()
                    }
                }
            }
        } else {
            Log.d(TAG, "关闭任务: DeleteTaskHelper")
            scope?.cancel("关闭任务：DeleteTaskHelper")
        }
    }

    fun handle(event: AccessibilityEvent) {
        if (!isEnabled) return
        scope?.launch {
            if (mutex.isLocked) return@launch
            eventFlow.emit(event)
        }
    }
}