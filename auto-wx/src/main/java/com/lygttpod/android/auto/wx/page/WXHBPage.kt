package com.lygttpod.android.auto.wx.page

import android.view.accessibility.AccessibilityNodeInfo
import com.android.accessibility.ext.acc.*
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.accessibility.ext.task.retryTaskWithLog
import com.lygttpod.android.auto.wx.data.NodeInfo
import com.lygttpod.android.auto.wx.service.wxAccessibilityService
import com.lygttpod.android.auto.wx.version.nodeProxy

object WXHBPage : IPage {

    interface Nodes {
        val chatPageTitleNode: NodeInfo
        val hbPatentNode: NodeInfo
        val hbReceiveNode: NodeInfo
        val hbOpenNode: NodeInfo
        val hbBackNode: NodeInfo
        val hbSenderNode: NodeInfo
        val hbNumNode: NodeInfo
        val hbMissNode: NodeInfo
        val hbMissCloseNode: NodeInfo

        companion object : Nodes by nodeProxy()
    }

    enum class HBStatus {
        ENABLE,//可抢
        MISS,//错失
        UNKNOW//未知
    }

    override fun pageClassName() = ""

    override fun pageTitleName() = "微信抢红包"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findById(Nodes.chatPageTitleNode.nodeId) != null
    }

    private suspend fun inPage() = retryCheckTaskWithLog("判断是否在聊天页面") { isMe() }

    suspend fun fuckMoney() {
        val inPage = inPage()
        if (!inPage) return
        findHBNodes().forEach { openHB(it) }
    }

    private fun findHBNodes(): List<AccessibilityNodeInfo> {
        return wxAccessibilityService
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
        when(checkClickStatus()) {
            HBStatus.ENABLE -> {
                val open = clickOpenHb()
                if (!open) return
                val inHbDetails = inHbDetails()
                if (!inHbDetails) return
                val hbInfo = getHbInfo()
                backFromHBDetail(hbInfo)
            }
            HBStatus.MISS -> {
                clickCloseMissHb()
            }
            else -> {}
        }
    }

    private suspend fun clickHb(nodeInfo: AccessibilityNodeInfo): Boolean {
        return retryCheckTaskWithLog("点击红包") { nodeInfo.click() }
    }

    private suspend fun checkClickStatus(): HBStatus {
        //1、可能被抢完了
        //2、可以正常抢
        return retryTaskWithLog("检查点击红包状态") {
            val miss = wxAccessibilityService?.findById(Nodes.hbMissNode.nodeId) != null
            val canOpen = wxAccessibilityService?.findById(Nodes.hbOpenNode.nodeId) != null
            when{
                miss -> HBStatus.MISS
                canOpen -> HBStatus.ENABLE
                else -> null
            }
        } ?: HBStatus.UNKNOW
    }

    private suspend fun clickCloseMissHb(): Boolean {
        return retryCheckTaskWithLog("点击关闭错失的红包") {
            wxAccessibilityService.clickById(Nodes.hbMissCloseNode.nodeId)
        }
    }

    private suspend fun clickOpenHb(): Boolean {
        return retryCheckTaskWithLog("点击开红包") {
            wxAccessibilityService.clickById(Nodes.hbOpenNode.nodeId)
        }
    }

    private suspend fun inHbDetails(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("判断当前是否在红包详情页") {
                wxAccessibilityService?.findById(Nodes.hbSenderNode.nodeId) != null
            }
        }
    }

    private suspend fun getHbInfo(): String? {
        return retryTaskWithLog("获取红包信息") {
            val hbSender =
                wxAccessibilityService?.findById(Nodes.hbSenderNode.nodeId)?.text.default()
            val getHbNum =
                wxAccessibilityService?.findById(Nodes.hbNumNode.nodeId)?.text.default()

            "${hbSender}，抢到：$getHbNum 元"
        }
    }

    private suspend fun backFromHBDetail(hbInfo: String?): Boolean {
        return delayAction {
            retryCheckTaskWithLog("$hbInfo  点击返回") {
                wxAccessibilityService.clickById(Nodes.hbBackNode.nodeId)
            }
        }
    }

}