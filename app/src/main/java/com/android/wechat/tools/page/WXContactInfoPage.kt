package com.android.wechat.tools.page

import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.acc.findByText
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.service.wxAccessibilityService

object WXContactInfoPage : IPage {

    enum class NodeInfo(val nodeText: String, val nodeId: String, val des: String) {
        ContactInfoSendMsgNode("发消息", "", "通讯录用户信息页的【发消息】按钮")
    }

    override fun pageClassName() = "com.tencent.mm.plugin.profile.ui.ContactInfoUI"

    override fun pageTitleName() = "通讯录用户信息页"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findByText(NodeInfo.ContactInfoSendMsgNode.nodeText) != null
    }

    /**
     * 点击发消息按钮
     */
    suspend fun clickSendMsg(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【发消息】按钮") {
                wxAccessibilityService.clickByText(NodeInfo.ContactInfoSendMsgNode.nodeText)
            }
        }
    }

}