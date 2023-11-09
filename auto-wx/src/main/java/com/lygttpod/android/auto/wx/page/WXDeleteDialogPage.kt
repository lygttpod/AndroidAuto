package com.lygttpod.android.auto.wx.page

import android.util.Log
import com.android.accessibility.ext.acc.*
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.lygttpod.android.auto.wx.data.NodeInfo
import com.lygttpod.android.auto.wx.service.wxAccessibilityService
import com.lygttpod.android.auto.wx.version.nodeProxy

object WXDeleteDialogPage : IPage {

    interface Nodes {
        val dialogContentNode: NodeInfo
        val dialogDeleteNode: NodeInfo

        companion object : Nodes by nodeProxy()
    }

    override fun pageClassName() = ""

    override fun pageTitleName() = "微信抢红包"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findByIdAndText(
            Nodes.dialogContentNode.nodeId,
            Nodes.dialogContentNode.nodeText
        ) != null
    }

    private suspend fun inPage() = retryCheckTaskWithLog("判断是否在删除聊天弹窗界面") { isMe() }

    suspend fun handleEvent() {
        val inPage = inPage()
        if (!inPage) return
        wxAccessibilityService.clickByIdAndText(
            Nodes.dialogDeleteNode.nodeId,
            Nodes.dialogDeleteNode.nodeText
        )
    }
}