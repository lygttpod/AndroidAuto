package com.lygttpod.android.auto.tools.service

import com.android.accessibility.ext.acc.printNodeInfo
import com.android.accessibility.ext.toast
import com.android.local.service.annotation.Get
import com.android.local.service.annotation.Page
import com.android.local.service.annotation.Service
import com.android.local.service.core.ALSHelper
import com.lygttpod.android.auto.tools.accessibility.AutoToolsAccessibility
import com.lygttpod.android.auto.tools.manager.ContentManger

@Service(port = 9527)
abstract class AutoToolsService {

    @Page("index")
    fun getIndexPage() = "auto_tools_index.html"

    @Get("getPageNodeInfo")
    fun getPageNodeInfo(): String {
        ContentManger.printContent = AutoToolsAccessibility.autoToolsAccessibility?.printNodeInfo(false)
        ALSHelper.context?.applicationContext?.toast("成功获取到页面元素，请查看")
        return ContentManger.printContent
            ?: "暂未获取到页面节点信息，请先确保APP已开启【布局节点速查器】无障碍功能"
    }

    @Get("getPageSimpleNodeInfo")
    fun getPageSimpleNodeInfo(): String {
        ContentManger.printContent =
            AutoToolsAccessibility.autoToolsAccessibility?.printNodeInfo(true)
        ALSHelper.context?.applicationContext?.toast("成功获取到页面元素，请查看")
        return ContentManger.printContent
            ?: "暂未获取到页面节点信息，请先确保APP已开启【布局节点速查器】无障碍功能"
    }

}