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
    fun getIndexPage(): String {
        AutoToolsAccessibility.floatWindowPackageName = null
        return "auto_tools_index.html"
    }

    @Get("getPageSimpleNodeInfo")
    fun getPageSimpleNodeInfo(): String {
        ContentManger.printContent =
            AutoToolsAccessibility.getRootWindowNodeInfo().printNodeInfo(simplePrint = true)
        return ContentManger.printContent
            ?: "暂未获取到页面节点信息，请先确保APP已开启【布局节点速查器】无障碍功能"
    }

    @Get("click")
    fun click(nodeId: String? = null, nodeText: String? = null): Boolean {
        val accessibility = AutoToolsAccessibility.autoToolsAccessibility
        return if (nodeId.isNullOrBlank().not() && nodeText.isNullOrBlank().not()) {
            AutoToolsAccessibility.getRootWindowNodeInfo().clickByIdAndText(nodeId!!, nodeText!!, accessibilityService = accessibility)
        } else if (nodeId.isNullOrBlank().not()) {
            AutoToolsAccessibility.getRootWindowNodeInfo().clickById(nodeId!!, accessibilityService = accessibility)
        } else if (nodeText.isNullOrBlank().not()) {
            AutoToolsAccessibility.getRootWindowNodeInfo().clickByText(nodeText!!, accessibilityService = accessibility)
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

    @Get("setConfig")
    fun setConfig(type: String, value: String? = null): Boolean {
        when(type) {
            "idColor" -> setIdColor(value)
            "textColor" -> setTextColor(value)
            "floatWindowPackageName" -> setFloatWindowPackageName(value)
        }
        return true
    }

    private fun setIdColor(value: String?): Boolean {
        val illegalityId = value.isNullOrBlank().not() && value!!.length != 6
        if (illegalityId) {
            return false
        }
        AppContext.setSpValue("idColor", value.default())
        return true
    }

    private fun setTextColor(value: String?): Boolean {
        val illegalityText = value.isNullOrBlank().not() && value!!.length != 6
        if (illegalityText) {
            return false
        }
        AppContext.setSpValue("textColor", value.default())
        return true
    }

    private fun setFloatWindowPackageName(value: String?) {
        AutoToolsAccessibility.floatWindowPackageName = value
    }

    @Get("getHighlight")
    fun getHighlight(): List<String> {
        val idColor = AppContext.getSpValue("idColor")
        val textColor = AppContext.getSpValue("textColor")
        return listOf(idColor, textColor)
    }
}