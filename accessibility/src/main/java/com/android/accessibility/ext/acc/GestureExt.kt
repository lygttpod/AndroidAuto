package com.android.accessibility.ext.acc

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 利用手势模拟点击
 * @param node: 需要点击的节点
 * */
fun AccessibilityService?.gestureClick(node: AccessibilityNodeInfo): Boolean {
    this ?: return false
    val nodeBounds = Rect().apply(node::getBoundsInScreen)
    val x = nodeBounds.centerX().toFloat()
    val y = nodeBounds.centerY().toFloat()
    return dispatchGesture(
        GestureDescription.Builder().apply {
            addStroke(
                GestureDescription.StrokeDescription(
                    Path().apply { moveTo(x, y) },
                    0L,
                    200L
                )
            )
        }.build(),
        object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
            }
        },
        null
    )
}

/**
 * 向上滚动
 */
fun AccessibilityService?.scrollUp(distance: Int = 500): Boolean {
    return gestureScroll(ScrollDirection.UP, distance)
}

/**
 * 向下滚动
 */
fun AccessibilityService?.scrollDown(distance: Int = 500): Boolean {
    return gestureScroll(ScrollDirection.BOTTOM, distance)
}

/**
 * 向左滑动
 */
fun AccessibilityService?.scrollLeft(distance: Int = 500): Boolean {
    return gestureScroll(ScrollDirection.LEFT, distance)
}

/**
 * 向右滑动
 */
fun AccessibilityService?.scrollRight(distance: Int = 500): Boolean {
    return gestureScroll(ScrollDirection.RIGHT, distance)
}


enum class ScrollDirection {
    LEFT,
    RIGHT,
    UP,
    BOTTOM
}

/**
 * 利用手势模拟滑动
 * @param distance: 滑动距离占屏幕宽或高的百分比
 */
private fun AccessibilityService?.gestureScroll(
    direction: ScrollDirection,
    distance: Int = 500,
): Boolean {
    val service = this ?: return false
    val node = service.rootInActiveWindow
    try {
        val nodeBounds = Rect().apply(node::getBoundsInScreen)
        val x = nodeBounds.centerX().toFloat()
        val y = nodeBounds.centerY().toFloat()
        val realYDistance = minOf(if (distance <= 0) 500 else distance, (y * 2).toInt())
        val realXDistance = minOf(if (distance <= 0) 500 else distance, (x * 2).toInt())

        val path = when (direction) {
            ScrollDirection.LEFT -> Path().apply {
                moveTo(x + realXDistance / 2, y)
                lineTo(x - realXDistance / 2, y)
            }

            ScrollDirection.RIGHT -> Path().apply {
                moveTo(x - realXDistance / 2, y)
                lineTo(x + realXDistance / 2, y)
            }

            ScrollDirection.UP -> Path().apply {
                moveTo(x, y + realYDistance / 2)
                lineTo(x, y - realYDistance / 2)
            }

            ScrollDirection.BOTTOM -> Path().apply {
                moveTo(x, y - realYDistance / 2)
                lineTo(x, y + realYDistance / 2)
            }
        }
        return dispatchGesture(
            GestureDescription.Builder().apply {
                addStroke(
                    GestureDescription.StrokeDescription(path, 0L, 300)
                )
            }
                .build(),
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                }
            },
            null
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}