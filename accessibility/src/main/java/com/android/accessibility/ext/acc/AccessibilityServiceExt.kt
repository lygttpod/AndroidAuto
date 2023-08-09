package com.android.accessibility.ext.acc

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.android.accessibility.ext.default
import kotlinx.coroutines.delay
import kotlin.math.abs


fun AccessibilityService.findById(id: String): AccessibilityNodeInfo? {
    return rootInActiveWindow.findNodesById(id).firstOrNull()
}

fun AccessibilityService.findByText(text: String): AccessibilityNodeInfo? {
    return rootInActiveWindow.findNodeByText(text)
}

fun AccessibilityService.findByContainsText(
    isPrint: Boolean = true,
    textList: List<String>
): AccessibilityNodeInfo? {
    return rootInActiveWindow.findNodeWrapperByContainsText(isPrint, textList)?.nodeInfo
}

fun AccessibilityService.findByIdAndText(id: String, text: String): AccessibilityNodeInfo? {
    return rootInActiveWindow.findNodesById(id).firstOrNull { it.text == text }
}

fun AccessibilityService?.clickById(id: String): Boolean {
    this ?: return false
    val find = rootInActiveWindow.findNodesById(id).firstOrNull() ?: return false
    gestureClick(find)
    return true
}

fun AccessibilityService?.clickByText(text: String): Boolean {
    this ?: return false
    val find = rootInActiveWindow.findNodesByText(text).firstOrNull() ?: return false
    gestureClick(find)
    return true
}

suspend fun AccessibilityService?.scrollToClickByText(
    scrollViewId: String,
    text: String
): Boolean {
    this ?: return false
    val find = rootInActiveWindow.findNodeByText(text)
    return if (find == null) {
        rootInActiveWindow.findNodesById(scrollViewId).firstOrNull()?.scrollForward()
        delay(200)
        scrollToClickByText(scrollViewId, text)
    } else {
        find.click()
    }
}

suspend fun AccessibilityService?.scrollToFindNextNodeByCurrentText(
    scrollViewId: String,
    childViewId: String,
    lastText: String?,
    filterTexts: List<String> = listOf()
): AccessibilityNodeInfo? {
    this ?: return null
    var find = getNextNodeByCurrentText(scrollViewId, childViewId, lastText, filterTexts)
    var isEnd = false
    var isRealEnd = false
    while (find == null && !isRealEnd) {
        val parent: AccessibilityNodeInfo =
            rootInActiveWindow.findNodeById(scrollViewId) ?: return null
        parent.scrollForward()
        delay(200)
        val tryFind = getNextNodeByCurrentText(scrollViewId, childViewId, lastText, filterTexts)
        find = tryFind
        if (isEnd && tryFind == null) {
            isRealEnd = true
        }
        isEnd = tryFind == null
    }
    return find
}

private suspend fun AccessibilityService?.getNextNodeByCurrentText(
    scrollViewId: String,
    childViewId: String,
    lastText: String?,
    filterTexts: List<String> = listOf()
): AccessibilityNodeInfo? {
    this ?: return null
    val parent: AccessibilityNodeInfo = rootInActiveWindow.findNodeById(scrollViewId) ?: return null
    val find =
        parent.findNodesById(childViewId).filterNot { filterTexts.contains(it.text.default()) }
    return if (lastText.isNullOrBlank()) {
        find.firstOrNull()
    } else {
        val lastIndex = find.indexOfFirst { it.text.default() == lastText }
        if (lastIndex > -1) {
            find.getOrNull(lastIndex + 1)
        } else {
            find.firstOrNull()
        }
    }
}

suspend fun AccessibilityService?.scrollToFindByText(
    scrollViewId: String,
    text: String
): AccessibilityNodeInfo? {
    this ?: return null
    val find = rootInActiveWindow.findNodeByText(text)
    return if (find == null) {
        rootInActiveWindow.findNodeById(scrollViewId)?.scrollForward()
        delay(200)
        scrollToFindByText(scrollViewId, text)
    } else {
        find
    }
}

fun AccessibilityService.printNodeInfo() {
    rootInActiveWindow.printNodeInfo()
}

fun AccessibilityService.clickByIdAndText(
    id: String,
    text: String
): Boolean {
    rootInActiveWindow.findNodesById(id).firstOrNull { it.text == text }?.let {
        gestureClick(it)
        return true
    }
    return false
}

/**
 * 模拟back按键
 */
fun AccessibilityService.pressBackButton(): Boolean {
    return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
}

/**
 * 利用手势模拟点击
 * @param node: 需要点击的节点
 * */
fun AccessibilityService.gestureClick(node: AccessibilityNodeInfo) {
    val nodeBounds = Rect().apply(node::getBoundsInScreen)
    val x = nodeBounds.centerX().toFloat()
    val y = nodeBounds.centerY().toFloat()
    dispatchGesture(
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
 * 利用手势模拟滑动
 * @param distance: 需要滑动的距离，像素值 负值：向下滚动，正直：向上滚动
 * @param scrollDuration: 需要滑动时长
 * @param node: 需要滑动的节点
 * */
private fun AccessibilityService.gestureScroll(
    node: AccessibilityNodeInfo,
    distance: Int,
    scrollDuration: Long = 300
) {
    val nodeBounds = Rect().apply(node::getBoundsInScreen)
    val x = nodeBounds.centerX().toFloat()
    val y = nodeBounds.centerY().toFloat()
    dispatchGesture(
        GestureDescription.Builder().apply {
            addStroke(
                GestureDescription.StrokeDescription(
                    Path().apply {
                        moveTo(x, y)
                        lineTo(x, y + distance)
                    },
                    0L,
                    scrollDuration
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
fun AccessibilityService.scrollForward(
    distance: Int = 500,
    scrollDuration: Long = 300,
    node: AccessibilityNodeInfo
) {
    gestureScroll(node, abs(distance), scrollDuration)
}

/**
 * 向下滚动
 */
fun AccessibilityService.scrollBackward(
    distance: Int = 500,
    scrollDuration: Long = 300,
    node: AccessibilityNodeInfo
) {
    gestureScroll(node, -abs(distance), scrollDuration)
}


suspend fun AccessibilityService?.findAllChildByScroll(
    parentViewId: String,
    childViewId: String,
    stopFindBlock: (MutableList<AccessibilityNodeInfo>) -> Boolean
): List<AccessibilityNodeInfo> {
    this ?: return listOf()
    val rootNode = rootInActiveWindow
    val list = mutableListOf<AccessibilityNodeInfo>()
    val find = findChildNodes(parentViewId, childViewId)
    find.forEach { node ->
        val newNode = list.find { it.text.default() == node.text.default() } == null
        if (newNode) {
            list.add(node)
        }
    }

    val parentNode = rootNode.findNodeById(parentViewId) ?: return list
    var isStop = false
    while (parentNode.isScrollable && !isStop) {
        parentNode.scrollForward()
        delay(800)//时间太短的话有时候会获取不到节点信息
        val findNextNodes = findChildNodes(parentViewId, childViewId)
        findNextNodes.forEach { node ->
            val newNode = list.find { it.text.default() == node.text.default() } == null
            if (newNode) {
                list.add(node)
            }
        }
        isStop = stopFindBlock(list)
    }
    return list
}

fun AccessibilityService?.findChildNodes(
    parentViewId: String,
    childViewId: String
): List<AccessibilityNodeInfo> {
    this ?: return listOf()
    val rootNode = rootInActiveWindow
    val parentNode: AccessibilityNodeInfo =
        rootNode.findNodesById(parentViewId).firstOrNull() ?: return listOf()
    val findList = mutableListOf<AccessibilityNodeInfo>()
    val size = parentNode.childCount
    if (size <= 0) return emptyList()
    for (index in 0 until size) {
        parentNode.getChild(index).findNodesById(childViewId).firstOrNull()?.let {
            Log.d("printNodeInfo", "当前页parentNode可见的元素=======${it.text}")
            findList.add(it)
        }
    }
    return findList
}

suspend fun AccessibilityService?.selectChildByScroll(
    parentViewId: String,
    childViewId: String,
    maxSelectCount: Int = Int.MAX_VALUE,
    startText: String? = null,
    onScrollEnd: (() -> Unit)? = null
): List<String> {
    this ?: return listOf()
    val rootNode = rootInActiveWindow
    val findTexts = mutableListOf<String>()
    val findNodes = findChildNodes(parentViewId, childViewId)
    val startIndex = findNodes.indexOfFirst { it.text.default() == startText }
    findNodes.filterIndexed { index, info -> index > startIndex }.forEach {
        val text = it.text.default()
        if (!findTexts.contains(text)) {
            if (findTexts.size < maxSelectCount) {
                val clicked = it.click()
                if (clicked) {
                    findTexts.add(text)
                    Log.d("selectChildByScroll", "click: 点击 $text")
                }
                delay(50)
            } else {
                return@forEach
            }
        }
    }

    val parentNode = rootNode.findNodeById(parentViewId) ?: return findTexts
    var lastTemp = findNodes
    //第一次滚动到底的判断，测试中发现，有一定概率出现滚动到上次选择的最后一个好友正好是当前一屏中的第一个，就会误判为滚动完成
    var isEnd = false
    //当第一次判断为滚动结束后，在家滚动一次后二次判断，可以确保真正的滚动到底
    var isRealEnd = false
    while (parentNode.isScrollable && findTexts.size < maxSelectCount && !isRealEnd) {
        parentNode.scrollForward()
        Log.d("selectChildByScroll", "滚动了一屏")
        delay(1000)
        val findNextNodes = findChildNodes(parentViewId, childViewId)
        val tempEnd = lastTemp.lastOrNull()?.text == findNextNodes.lastOrNull()?.text
        if (isEnd && tempEnd) {
            isRealEnd = true
        }
        isEnd = tempEnd

        Log.d(
            "selectChildByScroll",
            "=============是否搜索到底了  isEnd = $isEnd  isRealEnd = $isRealEnd  lastTemp = ${lastTemp.lastOrNull()?.text}   currentLast = ${findNextNodes.lastOrNull()?.text}"
        )
        if (isRealEnd) {
            onScrollEnd?.invoke()
            break
        }
        lastTemp = findNextNodes
        if (!isEnd) {
            findNextNodes.forEach {
                val text = it.text.default()
                if (!findTexts.contains(text)) {
                    if (findTexts.size < maxSelectCount) {
                        val clicked = it.click()
                        if (clicked) {
                            findTexts.add(text)
                            Log.d("selectChildByScroll", "click: 点击 $text")
                        }
                    } else {
                        return@forEach
                    }
                }
            }
        }
    }
    return findTexts
}
