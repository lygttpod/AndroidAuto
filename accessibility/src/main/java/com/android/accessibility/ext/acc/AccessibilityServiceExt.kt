package com.android.accessibility.ext.acc

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.android.accessibility.ext.default
import kotlinx.coroutines.delay


fun AccessibilityService.findById(id: String): AccessibilityNodeInfo? {
    return rootInActiveWindow.findNodesById(id).firstOrNull()
}

fun AccessibilityService?.findNodesById(id: String): List<AccessibilityNodeInfo> {
    return this?.rootInActiveWindow?.findNodesById(id) ?: listOf()
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

fun AccessibilityService?.clickById(id: String, gestureClick: Boolean = true): Boolean {
    this ?: return false
    val find = rootInActiveWindow.findNodesById(id).firstOrNull() ?: return false
    return if (gestureClick) {
        gestureClick(find).takeIf { it } ?: find.click()
    } else {
        find.click().takeIf { it } ?: gestureClick(find)
    }
}

fun AccessibilityService?.clickByText(text: String, gestureClick: Boolean = true): Boolean {
    this ?: return false
    val find = rootInActiveWindow.findNodesByText(text).firstOrNull() ?: return false
    return if (gestureClick) {
        gestureClick(find).takeIf { it } ?: find.click()
    } else {
        find.click().takeIf { it } ?: gestureClick(find)
    }
}


fun AccessibilityService?.clickByIdAndText(
    id: String,
    text: String,
    gestureClick: Boolean = true
): Boolean {
    this ?: return false
    rootInActiveWindow.findNodesById(id).firstOrNull { it.text.default() == text }?.let { find ->
        return if (gestureClick) {
            gestureClick(find).takeIf { it } ?: find.click()
        } else {
            find.click().takeIf { it } ?: gestureClick(find)
        }
    }
    return false
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
    while (find == null && !isEnd) {
        val parent: AccessibilityNodeInfo =
            rootInActiveWindow.findNodeById(scrollViewId) ?: return null
        parent.scrollForward()
        delay(200)
        val tryFind = getNextNodeByCurrentText(scrollViewId, childViewId, lastText, filterTexts)
        find = tryFind
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

fun AccessibilityService?.printNodeInfo(simplePrint: Boolean = true): String {
    this ?: return ""
    return rootInActiveWindow.printNodeInfo(simplePrint = simplePrint)
}

/**
 * 模拟back按键
 */
fun AccessibilityService.pressBackButton(): Boolean {
    return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
}

suspend fun AccessibilityService?.findAllChildByScroll(
    parentViewId: String,
    childViewId: String,
): List<AccessibilityNodeInfo> {
    this ?: return listOf()
    val rootNode = rootInActiveWindow
    val list = mutableListOf<AccessibilityNodeInfo>()
    val finds = findAllChildByFilter(parentViewId, childViewId) { filter ->
        //倒叙查找可以提示查找效率，因为新增的数据是在列表后边的
        list.findLast { it.text.default() == filter.text.default() } != null
    }
    list.addAll(finds)
    val parentNode = rootNode.findNodeById(parentViewId) ?: return list
    var isStop = false
    while (parentNode.isScrollable && !isStop) {
        parentNode.scrollForward()
        delay(500)//时间太短的话有时候会获取不到节点信息
        val findNextNodes = findAllChildByFilter(parentViewId, childViewId) { filter ->
            list.findLast { it.text.default() == filter.text.default() } != null
        }
        isStop = findNextNodes.isEmpty()
        if (isStop) break
        list.addAll(findNextNodes)
    }
    return list
}

private fun AccessibilityService?.findAllChildByFilter(
    parentViewId: String,
    childViewId: String,
    filterPredicate: (AccessibilityNodeInfo) -> Boolean
): List<AccessibilityNodeInfo> {
    val find = findChildNodes(parentViewId, childViewId)
    return find.filterNot { filterPredicate(it) }
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

suspend fun AccessibilityService?.selectChild(
    parentViewId: String,
    childViewId: String,
    maxSelectCount: Int = Int.MAX_VALUE,
    lastText: String? = null,
): List<String> {
    this ?: return listOf()
    val findTexts = mutableListOf<String>()
    val findNodes = findChildNodes(parentViewId, childViewId)
    val findIndex = findNodes.indexOfFirst { it.text.default() == lastText }
    findNodes.filterIndexed { index, _ -> index > findIndex }.forEach {
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
    return findTexts
}

suspend fun AccessibilityService?.selectChildByScroll(
    parentViewId: String,
    childViewId: String,
    maxSelectCount: Int = Int.MAX_VALUE,
    lastText: String? = null,
): List<String> {
    this ?: return listOf()
    val rootNode = rootInActiveWindow
    val findTexts = mutableListOf<String>()
    val select = if (lastText.isNullOrBlank()) {
        selectChild(parentViewId, childViewId, maxSelectCount, lastText)
    } else {
        scrollToFindByText(parentViewId, lastText)
        selectChild(parentViewId, childViewId, maxSelectCount, lastText)
    }
    findTexts.addAll(select)
    if (findTexts.size == maxSelectCount) return findTexts
    val parentNode = rootNode.findNodeById(parentViewId) ?: return findTexts
    var isEnd = false
    while (parentNode.isScrollable && findTexts.size < maxSelectCount && !isEnd) {
        parentNode.scrollForward()
        Log.d("selectChildByScroll", "滚动了一屏")
        delay(1000)
        val findNextNodes = selectChild(
            parentViewId,
            childViewId,
            maxSelectCount - findTexts.size,
            findTexts.lastOrNull() ?: lastText
        )
        isEnd = findNextNodes.isEmpty()
        Log.d("selectChildByScroll", "=============是否搜索到底了  isEnd = $isEnd")
        findTexts.addAll(findNextNodes)
    }
    return findTexts
}
