package com.android.accessibility.ext.acc

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.android.accessibility.ext.data.NodeWrapper
import com.android.accessibility.ext.default

fun AccessibilityNodeInfo?.printNodeInfo(
    prefix: String = "",
    isLast: Boolean = false,
    printContent: StringBuilder = StringBuilder(),
    simplePrint: Boolean = false
): String {
    val node = this ?: return printContent.toString()
    val nodeWrapper = NodeWrapper(
        className = node.className.default(),
        text = node.text.default(),
        id = node.viewIdResourceName.default(),
        description = node.contentDescription.default(),
        isClickable = node.isClickable,
        isScrollable = node.isScrollable,
        isEditable = node.isEditable,
        isSelected = node.isSelected,
        isChecked = node.isChecked,
        nodeInfo = node
    )
    val marker = if (isLast) """\--- """ else "+--- "
    val currentPrefix = "$prefix$marker"
    val print =
        currentPrefix + if (simplePrint) nodeWrapper.toSimpleString() else nodeWrapper.toString()
    printContent.append("${print}\n")
    Log.d("printNodeInfo", print)

    val size = node.childCount
    if (size > 0) {
        val childPrefix = prefix + if (isLast) "  " else "|  "
        val lastChildIndex = size - 1
        for (index in 0 until size) {
            val isLastChild = index == lastChildIndex
            node.getChild(index).printNodeInfo(childPrefix, isLastChild, printContent, simplePrint)
        }
    }
    return printContent.toString()
}