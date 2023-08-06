package com.android.accessibility.ext.acc

import android.view.accessibility.AccessibilityEvent


/**
 * 窗口状态变化
 */
fun AccessibilityEvent.isWindowStateChanged() =
    eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED

/**
 * 窗口内容变化
 */
fun AccessibilityEvent.isWindowContentChanged() =
    eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED

/**
 * 是否是点击事件
 */
fun AccessibilityEvent.isViewClicked(): Boolean =
    this.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED

/**
 * 是否是指定viewId的点击事件
 */
fun AccessibilityEvent.onViewClickedById(viewId: String): Boolean {
    return isViewClicked() && source?.viewIdResourceName == viewId
}

/**
 * 是否是指定viewId的点击事件
 */
fun AccessibilityEvent.onViewClickedByText(text: String): Boolean {
    return isViewClicked() && text.find { it.toString() == text } != null
}
