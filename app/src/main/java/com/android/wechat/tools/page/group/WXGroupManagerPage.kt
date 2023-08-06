package com.android.wechat.tools.page.group

import com.android.accessibility.ext.acc.clickById
import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.acc.findByText
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.page.IPage
import com.android.wechat.tools.service.wxAccessibilityService


object WXGroupManagerPage : IPage {
    override fun pageClassName() = ""

    override fun pageTitleName() = "群管理"

    override fun isMe(): Boolean {
        //解散该群聊 → id = com.tencent.mm:id/khj
        return wxAccessibilityService?.findByText("解散该群聊") != null
    }

    suspend fun clickDisbandGroup(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击解散该群聊") {
                wxAccessibilityService.clickByText("解散该群聊")
            }
        }
    }


    suspend fun clickDisbandGroupDialog(): Boolean {
        return delayAction {
            //text = 解散群聊后，群成员和群主都将被移出群聊。 → id = com.tencent.mm:id/kpi
            //text = 解散 → id = com.tencent.mm:id/knx
            retryCheckTaskWithLog("点击解散") {
                wxAccessibilityService.clickById("com.tencent.mm:id/knx")
            }
        }
    }
}