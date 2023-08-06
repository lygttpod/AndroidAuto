package com.android.accessibility.ext.task.i


/**
 * 任务执行链路跟踪
 */
interface ITaskTracker<T> {

    /** 方法开始执行触发 */
    fun onStart() {}

    /** 方法执行成功触发 */
    fun onSuccess(result: T, executeDuration: Long, executeCount: Int) {}

    /** 方法执行异常触发 */
    fun onError(error: Throwable, executeDuration: Long, executeCount: Int) {}

    /** 每次重试触发 */
    fun onEach(currentCount: Int) {}

    private object EMPTY : ITaskTracker<Any?>

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun <T> empty(): ITaskTracker<T> = EMPTY as ITaskTracker<T>
    }
}