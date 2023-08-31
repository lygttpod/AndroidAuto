package com.lygttpod.android.auto.tools.service

import com.android.accessibility.ext.acc.clickById
import com.android.accessibility.ext.acc.clickByIdAndText
import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.acc.findNodeById
import com.android.accessibility.ext.acc.inputText
import com.android.accessibility.ext.acc.pressBackButton
import com.android.accessibility.ext.acc.printNodeInfo
import com.android.accessibility.ext.acc.scrollDown
import com.android.accessibility.ext.acc.scrollLeft
import com.android.accessibility.ext.acc.scrollRight
import com.android.accessibility.ext.acc.scrollUp
import com.android.accessibility.ext.default
import com.android.local.service.annotation.Get
import com.android.local.service.annotation.Page
import com.android.local.service.annotation.Service
import com.lygttpod.android.auto.tools.AppContext
import com.lygttpod.android.auto.tools.accessibility.AutoToolsAccessibility
import com.lygttpod.android.auto.tools.ktx.getSpValue
import com.lygttpod.android.auto.tools.ktx.setSpValue
import com.lygttpod.android.auto.tools.manager.ContentManger

@Service(port = 9527)
abstract class AutoToolsService {

    @Page("")
    fun getIndexPage() = "auto_tools_index.html"

    @Get("getPageNodeInfo")
    fun getPageNodeInfo(): String {
        ContentManger.printContent =
            AutoToolsAccessibility.autoToolsAccessibility?.printNodeInfo(false)
        return ContentManger.printContent
            ?: "暂未获取到页面节点信息，请先确保APP已开启【布局节点速查器】无障碍功能"
    }

    @Get("getPageSimpleNodeInfo")
    fun getPageSimpleNodeInfo(): String {
        ContentManger.printContent =
            AutoToolsAccessibility.autoToolsAccessibility?.printNodeInfo(true)
        return ContentManger.printContent
            ?: "暂未获取到页面节点信息，请先确保APP已开启【布局节点速查器】无障碍功能"
    }

    @Get("click")
    fun click(nodeId: String? = null, nodeText: String? = null): Boolean {
        return if (nodeId.isNullOrBlank().not() && nodeText.isNullOrBlank().not()) {
            AutoToolsAccessibility.autoToolsAccessibility.clickByIdAndText(nodeId!!, nodeText!!)
        } else if (nodeId.isNullOrBlank().not()) {
            AutoToolsAccessibility.autoToolsAccessibility.clickById(nodeId!!)
        } else if (nodeText.isNullOrBlank().not()) {
            AutoToolsAccessibility.autoToolsAccessibility.clickByText(nodeText!!)
        } else {
            false
        }
    }

    @Get("back")
    fun pressBack(): Boolean {
        return AutoToolsAccessibility.autoToolsAccessibility?.pressBackButton() ?: false
    }

    @Get("scrollUp")
    fun scrollUp(distance: Int): Boolean {
        return AutoToolsAccessibility.autoToolsAccessibility.scrollUp(distance)
    }

    @Get("scrollDown")
    fun scrollDown(distance: Int): Boolean {
        return AutoToolsAccessibility.autoToolsAccessibility.scrollDown(distance)
    }

    @Get("scrollLeft")
    fun scrollLeft(distance: Int): Boolean {
        return AutoToolsAccessibility.autoToolsAccessibility.scrollLeft(distance)
    }

    @Get("scrollRight")
    fun scrollRight(distance: Int): Boolean {
        return AutoToolsAccessibility.autoToolsAccessibility.scrollRight(distance)
    }

    @Get("input")
    fun input(nodeId: String, content: String): Boolean {
        val rootNode =
            AutoToolsAccessibility.autoToolsAccessibility?.rootInActiveWindow ?: return false
        return rootNode.findNodeById(nodeId)?.inputText(content) ?: false
    }

    @Get("setHighlight")
    fun setHighlight(idColor: String? = null, textColor: String? = null): Boolean {
        val illegalityId = idColor.isNullOrBlank().not() && idColor!!.length != 6
        val illegalityText = textColor.isNullOrBlank().not() && textColor!!.length != 6
        if (illegalityId || illegalityText) {
            return false
        }
        AppContext.setSpValue("idColor", idColor.default())
        AppContext.setSpValue("textColor", textColor.default())
        return true
    }

    @Get("getHighlight")
    fun getHighlight(): List<String> {
        val idColor = AppContext.getSpValue("idColor")
        val textColor = AppContext.getSpValue("textColor")
        return listOf(idColor, textColor)
    }
}