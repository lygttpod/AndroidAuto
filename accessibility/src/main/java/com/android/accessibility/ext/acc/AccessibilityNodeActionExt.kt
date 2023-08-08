package com.android.accessibility.ext.acc

import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 点击事件
 */
fun AccessibilityNodeInfo?.click(): Boolean {
    this ?: return false
    return if (isClickable) {
        performAction(AccessibilityNodeInfo.ACTION_CLICK)
    } else {
        parent?.click() == true
    }
}

/**
 * 长按事件
 */
fun AccessibilityNodeInfo.longClick(): Boolean {
    return if (isClickable) {
        performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
    } else {
        parent.longClick()
    }
}

/**
 * 输入内容
 */
fun AccessibilityNodeInfo.inputText(input: String): Boolean {
    val arguments = Bundle().apply {
        putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, input)
    }
    return performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
}

/**
 * 向下滚动
 */
fun AccessibilityNodeInfo.scrollBackward() =
    performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)

/**
 * 向上滚动
 */
fun AccessibilityNodeInfo.scrollForward() =
    performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
