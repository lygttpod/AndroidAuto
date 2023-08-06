package com.android.accessibility.ext.data

import android.view.accessibility.AccessibilityNodeInfo

/**
 * 结点包装，方便查看打印
 */
data class NodeWrapper(
    var className: String,
    var text: String? = null,
    var id: String? = null,
    var description: String? = null,
    var clickable: Boolean = false,
    var scrollable: Boolean = false,
    var editable: Boolean = false,
    var nodeInfo: AccessibilityNodeInfo? = null
) {
    override fun toString() =
        "className = $className → text = $text → id = $id → description = $description → clickable = $clickable → scrollable = $scrollable → editable = $editable"
}