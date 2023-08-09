package com.android.wechat.tools.page

import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.acc.findById
import com.android.accessibility.ext.acc.findByText
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.accessibility.ext.task.retryTaskWithLog
import com.android.wechat.tools.data.WxUserInfo
import com.android.wechat.tools.service.wxAccessibilityService

object WXContactInfoPage : IPage {

    enum class NodeInfo(val nodeText: String, val nodeId: String, val des: String) {
        ContactInfoSendMsgNode("发消息", "com.tencent.mm:id/khj", "通讯录用户信息页的【发消息】按钮"),
        ContactInfoNickNameNode("", "com.tencent.mm:id/bq1", "通讯录用户信息页的【微信昵称】"),
        ContactInfoWxCodeNode("", "com.tencent.mm:id/bq9", "通讯录用户信息页的【微信号】"),
    }

    override fun pageClassName() = "com.tencent.mm.plugin.profile.ui.ContactInfoUI"

    override fun pageTitleName() = "通讯录用户信息页"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findByText(NodeInfo.ContactInfoSendMsgNode.nodeText) != null
    }

    suspend fun inPage(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("判断是否进入到【通讯录用户信息页】") { isMe() }
        }
    }

    /**
     * 点击发消息按钮
     */
    suspend fun clickSendMsg(): Boolean {
        return delayAction(delayMillis = 1000) {
            retryCheckTaskWithLog("点击【发消息】按钮") {
                //【发消息】的按钮ID和【音视频通话】的ID一样，所以用文本区分
                wxAccessibilityService.clickByText(NodeInfo.ContactInfoSendMsgNode.nodeText)
            }
        }
    }

    suspend fun getUserInfo(): WxUserInfo? {
        //android.widget.TextView → text = Allen → id = com.tencent.mm:id/bq1
        //android.widget.TextView → text = 微信号:  lygttpod → id = com.tencent.mm:id/bq9
        return retryTaskWithLog("获取用户信息") {
            val nickName = wxAccessibilityService
                ?.findById(NodeInfo.ContactInfoNickNameNode.nodeId)
                ?.text
                .default()
            //android.widget.TextView → 微信号: wxid_xxx → com.tencent.mm:id/ini
            val wxCode = wxAccessibilityService
                ?.findById(NodeInfo.ContactInfoWxCodeNode.nodeId)
                ?.text
                ?.split("微信号:")
                ?.getOrNull(1)
                .default()
                .trim()
            if (nickName.isNotBlank() && wxCode.isNotBlank()) {
                WxUserInfo(nickName, wxCode)
            } else {
                null
            }
        }
    }

}