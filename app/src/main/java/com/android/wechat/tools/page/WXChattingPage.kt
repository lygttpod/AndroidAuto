package com.android.wechat.tools.page

import com.android.accessibility.ext.acc.clickById
import com.android.accessibility.ext.acc.findById
import com.android.accessibility.ext.acc.findByIdAndText
import com.android.accessibility.ext.acc.gestureClick
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.data.NodeInfo
import com.android.wechat.tools.service.wxAccessibilityService
import com.android.wechat.tools.version.nodeProxy

object WXChattingPage : IPage {

    interface Nodes {
        val chattingBottomPlusNode: NodeInfo
        val chattingTransferMoneyNode: NodeInfo

        companion object : Nodes by nodeProxy()
    }

    override fun pageClassName() = "com.tencent.mm.ui.chatting.ChattingUI"

    override fun pageTitleName() = "微信聊天页"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findById(Nodes.chattingBottomPlusNode.nodeId) != null
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
                wxAccessibilityService.clickById(Nodes.chattingBottomPlusNode.nodeId)
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
                val find = wxAccessibilityService?.findByIdAndText(
                    Nodes.chattingTransferMoneyNode.nodeId,
                    Nodes.chattingTransferMoneyNode.nodeText
                )
                if (find == null) {
                    false
                } else {
                    wxAccessibilityService?.gestureClick(find)
                    true
                }
            }
        }
    }


}