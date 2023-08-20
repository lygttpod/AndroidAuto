package com.android.wechat.tools.page

import android.view.accessibility.AccessibilityNodeInfo
import com.android.accessibility.ext.acc.*
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.accessibility.ext.task.retryTaskWithLog
import com.android.wechat.tools.data.NodeInfo
import com.android.wechat.tools.service.wxHbAccessibilityService
import com.android.wechat.tools.version.nodeProxy

object WXHBPage : IPage {

    interface Nodes {
        val chatPageTitleNode: NodeInfo
        val hbPatentNode: NodeInfo
        val hbReceiveNode: NodeInfo
        val hbOpenNode: NodeInfo
        val hbBackNode: NodeInfo
        val hbSenderNode: NodeInfo
        val hbNumNode: NodeInfo

        companion object : Nodes by nodeProxy()
    }

    override fun pageClassName() = ""

    override fun pageTitleName() = "微信抢红包"

    override fun isMe(): Boolean {
        return wxHbAccessibilityService?.findById(Nodes.chatPageTitleNode.nodeId) != null
    }

    private suspend fun inPage() = retryCheckTaskWithLog("判断是否在聊天页面") { isMe() }

    suspend fun fuckMoney() {
        val inPage = inPage()
        if (!inPage) return
        findHBNodes().forEach { openHB(it) }
    }

    private fun findHBNodes(): List<AccessibilityNodeInfo> {
        return wxHbAccessibilityService
            .findNodesById(Nodes.hbPatentNode.nodeId)
            .filter {
                it.findNodeById(Nodes.hbReceiveNode.nodeId) == null
            }
    }

    private suspend fun openHB(nodeInfo: AccessibilityNodeInfo) {
        val inPage = inPage()
        if (!inPage) return
        val click = clickHb(nodeInfo)
        if (!click) return
        val open = clickOpenHb()
        if (!open) return
        val inHbDetails = inHbDetails()
        if (!inHbDetails) return
        val hbInfo = getHbInfo()
        backFromHBDetail(hbInfo)
    }

    private suspend fun clickHb(nodeInfo: AccessibilityNodeInfo): Boolean {
        return retryCheckTaskWithLog("点击红包") { nodeInfo.click() }
    }

    private suspend fun clickOpenHb(): Boolean {
        return retryCheckTaskWithLog("点击开红包") {
            wxHbAccessibilityService.clickById(Nodes.hbOpenNode.nodeId)
        }
    }

    private suspend fun inHbDetails(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("判断当前是否在红包详情页") {
                wxHbAccessibilityService?.findById(Nodes.hbSenderNode.nodeId) != null
            }
        }
    }

    private suspend fun getHbInfo(): String? {
        return retryTaskWithLog("获取红包信息") {
            val hbSender =
                wxHbAccessibilityService?.findById(Nodes.hbSenderNode.nodeId)?.text.default()
            val getHbNum =
                wxHbAccessibilityService?.findById(Nodes.hbNumNode.nodeId)?.text.default()

            "${hbSender}，抢到：$getHbNum 元"
        }
    }

    private suspend fun backFromHBDetail(hbInfo: String?): Boolean {
        return delayAction {
            retryCheckTaskWithLog("$hbInfo  点击返回") {
                wxHbAccessibilityService.clickById(Nodes.hbBackNode.nodeId)
            }
        }
    }

}