package com.android.accessibility.ext.task

import com.android.accessibility.ext.task.i.ITaskTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeout


suspend fun retryCheckTask(
    timeOutMillis: Long = 5_000,
    periodMillis: Long = 500,
    tracker: ITaskTracker<Unit> = ITaskTracker.empty(),
    predicate: suspend CoroutineScope.() -> Boolean
): Boolean {
    return try {
        val block: suspend CoroutineScope.() -> Unit? = { if (predicate()) Unit else null }
        retryTask(timeOutMillis, periodMillis, tracker, block)
        true
    } catch (e: Exception) {
        false
    }
}

suspend fun <T> retryTask(
    timeOutMillis: Long = 5_000,
    periodMillis: Long = 500,
    tracker: ITaskTracker<T> = ITaskTracker.empty(),
    block: suspend CoroutineScope.() -> T?
): T {
    val start = System.currentTimeMillis()
    var count = 0
    try {
        return withTimeout(timeOutMillis) {
            tracker.onStart()
            var result: T? = null
            while (isActive) {
                count++
                result = block()
                if (null != result) {
                    val executeDuration = System.currentTimeMillis() - start
                    tracker.onSuccess(result, executeDuration, count)
                    break
                } else {
                    tracker.onEach(count)
                    delay(periodMillis)
                }
            }
            result!!
        }
    } catch (e: Exception) {
        val executeDuration = System.currentTimeMillis() - start
        tracker.onError(e, executeDuration, count)
        throw e
    }
}