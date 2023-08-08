package com.android.wechat.tools.page

import com.android.accessibility.ext.acc.*
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.service.wxAccessibilityService

object WXChattingPage : IPage {

    enum class NodeInfo(val nodeText: String, val nodeId: String, val des: String) {
        ChattingBottomPlusNode("", "com.tencent.mm:id/b3q", "聊天页底部的【+】按钮"),
        ChattingTransferMoneyNode("转账", "com.tencent.mm:id/vg", "聊天页底部功能区的【转账】按钮")
    }

    override fun pageClassName() = "com.tencent.mm.ui.chatting.ChattingUI"

    override fun pageTitleName() = "微信聊天页"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findById(NodeInfo.ChattingBottomPlusNode.nodeId) != null
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
        return delayAction {
            retryCheckTaskWithLog("点击聊天页的功能区【+】按钮") {
                wxAccessibilityService.clickById(NodeInfo.ChattingBottomPlusNode.nodeId)
            }
        }
    }

    /**
     * 点击转账
     */
    suspend fun clickTransferMoney(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击聊天页功能区的【转账】按钮") {
//                wxAccessibilityService?.printNodeInfo()
//                wxAccessibilityService.findWithClickByText("转账")
                val find = wxAccessibilityService?.findByIdAndText(
                    NodeInfo.ChattingTransferMoneyNode.nodeId,
                    NodeInfo.ChattingTransferMoneyNode.nodeText
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