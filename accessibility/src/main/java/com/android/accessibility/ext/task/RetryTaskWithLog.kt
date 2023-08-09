package com.android.accessibility.ext.task

import android.util.Log
import com.android.accessibility.ext.task.i.ITaskTracker
import kotlinx.coroutines.CoroutineScope

private const val TAG = "LogTracker"


suspend fun <T>retryTaskWithLog(
    taskName: String,
    timeOutMillis: Long = 10_000L,
    periodMillis: Long = 500L,
    predicate: suspend CoroutineScope.() -> T?
): T? {
    return try {
        retryTask(timeOutMillis, periodMillis, LogTracker(taskName), predicate)
    } catch (e: Exception) {
        null
    }
}


suspend fun retryCheckTaskWithLog(
    taskName: String,
    timeOutMillis: Long = 10_000,
    periodMillis: Long = 500,
    predicate: suspend CoroutineScope.() -> Boolean
): Boolean {
    return try {
        retryCheckTask(timeOutMillis, periodMillis, LogTracker(taskName), predicate)
    } catch (e: Exception) {
        false
    }
}


class LogTracker<T>(private val taskName: String) : ITaskTracker<T> {

    override fun onStart() {
        Log.d(TAG, "【$taskName】开始执行")
    }

    override fun onEach(currentCount: Int) {
        Log.d(TAG, "【$taskName】第 $currentCount 次执行")
    }

    override fun onSuccess(result: T, executeDuration: Long, executeCount: Int) {
        Log.d(TAG, "【$taskName】任务执行成功，轮训总次数：${executeCount}, 耗时：$executeDuration ms")
    }

    override fun onError(error: Throwable, executeDuration: Long, executeCount: Int) {
        Log.d(TAG, "【$taskName】任务执行异常，轮训总次数：${executeCount}, 耗时：$executeDuration ms")
    }

}