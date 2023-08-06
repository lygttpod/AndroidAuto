package com.android.wechat.tools.page

import com.android.accessibility.ext.acc.findByText
import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.service.wxAccessibilityService

object WXContactInfoPage : IPage {
    override fun pageClassName() = "com.tencent.mm.plugin.profile.ui.ContactInfoUI"

    override fun pageTitleName() = "通讯录用户信息页"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findByText("发消息") != null
    }

    /**
     * 点击发消息按钮
     */
    suspend fun clickSendMsg(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击发消息") {
                wxAccessibilityService.clickByText("发消息")
            }
        }
    }

}