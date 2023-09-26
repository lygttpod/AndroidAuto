package com.lygttpod.android.auto.wx.page

import android.util.Log
import com.android.accessibility.ext.acc.clickById
import com.android.accessibility.ext.acc.clickByIdAndText
import com.android.accessibility.ext.acc.findById
import com.android.accessibility.ext.acc.findNodesById
import com.android.accessibility.ext.acc.inputText
import com.android.accessibility.ext.acc.isEditText
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.lygttpod.android.auto.wx.data.NodeInfo
import com.lygttpod.android.auto.wx.service.wxAccessibilityService
import com.lygttpod.android.auto.wx.version.nodeProxy

object WXChattingPage : IPage {

    interface Nodes {
        val chattingBottomRootNode: NodeInfo
        val chattingBottomPlusNode: NodeInfo
        val chattingTransferMoneyNode: NodeInfo
        val chattingSendMsgNode: NodeInfo
        val chattingEditTextNode: NodeInfo

        companion object : Nodes by nodeProxy()
    }

    override fun pageClassName() = "com.tencent.mm.ui.chatting.ChattingUI"

    override fun pageTitleName() = "微信聊天页"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findById(Nodes.chattingBottomRootNode.nodeId) != null
    }

    suspend fun checkInPage(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("检查当前是是否打开聊天页") {
                isMe()
            }
        }
    }

    /**
     * 点击更多功能按钮
     */
    suspend fun clickMoreOption(): Boolean {
        return delayAction(delayMillis = 1000) {
            retryCheckTaskWithLog("点击聊天页的功能区【+】按钮") {
                val clickMoreOption =
                    wxAccessibilityService.clickById(Nodes.chattingBottomPlusNode.nodeId)
                if (!clickMoreOption) {
                    val findSendBtn =
                        wxAccessibilityService?.findById(Nodes.chattingSendMsgNode.nodeId) != null
                    if (findSendBtn) {
                        Log.d("LogTracker", "发现功能区是【发送】按钮，需要先去清空输入框的的内容")
                        val clear = wxAccessibilityService
                            ?.findNodesById(Nodes.chattingEditTextNode.nodeId)
                            ?.lastOrNull { it.isEditText() }
                            ?.inputText("")
                            ?: false
                        if (clear) {
                            Log.d("LogTracker", "已清空输入的草本文本")
                        }
                    }
                }
                clickMoreOption
            }
        }
    }

    /**
     * 点击转账
     */
    suspend fun clickTransferMoney(): Boolean {
        return delayAction(delayMillis = 1000) {
            retryCheckTaskWithLog("点击聊天页功能区的【转账】按钮") {
//                wxAccessibilityService?.printNodeInfo()
//                wxAccessibilityService.findWithClickByText("转账")
                wxAccessibilityService.clickByIdAndText(
                    Nodes.chattingTransferMoneyNode.nodeId,
                    Nodes.chattingTransferMoneyNode.nodeText
                )
            }
        }
    }

}