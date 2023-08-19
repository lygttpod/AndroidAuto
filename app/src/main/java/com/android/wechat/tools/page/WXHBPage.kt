package com.android.wechat.tools.page

import android.view.accessibility.AccessibilityNodeInfo
import com.android.accessibility.ext.acc.*
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.accessibility.ext.task.retryTaskWithLog
import com.android.wechat.tools.service.wxHbAccessibilityService

object WXHBPage : IPage {

    enum class NodeInfo(val nodeText: String, val nodeId: String, val des: String) {
        ChatPageTitleNode("", "com.tencent.mm:id/ko4", "聊天页面"),
        HBPatentNode("", "com.tencent.mm:id/ape", "红包父布局"),
        HBReceiveNode("", "com.tencent.mm:id/xs", "红包【已领取】"),
        HBOpenNode("", "com.tencent.mm:id/gir", "【开】红包"),
        HBBackNode("", "com.tencent.mm:id/k6i", "从红包领取页【返回】"),
        HBSenderNode("", "com.tencent.mm:id/ge3", "红包详情页的xxx的红包"),
        HBNumNode("", "com.tencent.mm:id/gcj", "红包详情页中抢到的红包金额"),
    }

    override fun pageClassName() = ""

    override fun pageTitleName() = "微信抢红包"

    override fun isMe(): Boolean {
        return wxHbAccessibilityService?.findById(NodeInfo.ChatPageTitleNode.nodeId) != null
    }

    private suspend fun inPage() = retryCheckTaskWithLog("判断是否在聊天页面") { isMe() }

    suspend fun fuckMoney() {
        val inPage = inPage()
        if (!inPage) return
        findHBNodes().forEach { openHB(it) }
    }

    private fun findHBNodes(): List<AccessibilityNodeInfo> {
        return wxHbAccessibilityService
            .findNodesById(NodeInfo.HBPatentNode.nodeId)
            .filter {
                it.findNodeById(NodeInfo.HBReceiveNode.nodeId) == null
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
            wxHbAccessibilityService.clickById(NodeInfo.HBOpenNode.nodeId)
        }
    }

    private suspend fun inHbDetails(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("判断当前是否在红包详情页") {
                wxHbAccessibilityService?.findById(NodeInfo.HBSenderNode.nodeId) != null
            }
        }
    }

    private suspend fun getHbInfo(): String? {
        return retryTaskWithLog("获取红包信息") {
            val hbSender =
                wxHbAccessibilityService?.findById(NodeInfo.HBSenderNode.nodeId)?.text.default()
            val getHbNum =
                wxHbAccessibilityService?.findById(NodeInfo.HBNumNode.nodeId)?.text.default()

            "${hbSender}，抢到：$getHbNum 元"
        }
    }

    private suspend fun backFromHBDetail(hbInfo: String?): Boolean {
        return delayAction {
            retryCheckTaskWithLog("$hbInfo  点击返回") {
                wxHbAccessibilityService.clickById(NodeInfo.HBBackNode.nodeId)
            }
        }
    }

}