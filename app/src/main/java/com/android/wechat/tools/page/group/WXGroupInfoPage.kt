package com.android.wechat.tools.page.group

import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.acc.findById
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.page.IPage
import com.android.wechat.tools.service.wxAccessibilityService


object WXGroupInfoPage : IPage {
    override fun pageClassName() = ""

    override fun pageTitleName() = "群聊天信息页"

    override fun isMe(): Boolean {
        // 群管理 → id = android:id/text1
        return wxAccessibilityService?.findById("群管理") != null
    }

    suspend fun clickGroupManager(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击群管理", timeOutMillis = 10000) {
                wxAccessibilityService.clickByText("群管理")
            }
        }
    }

    suspend fun clickDeleteGroup(): Boolean {
        return delayAction {
            //android.widget.TextView → text = 删除 → id = com.tencent.mm:id/khj
            retryCheckTaskWithLog("点击删除群", timeOutMillis = 10000) {
                wxAccessibilityService.clickByText("删除")
            }
        }
    }

    suspend fun clickDeleteGroupDialog(): Boolean {
        return delayAction {
            //text = 清空聊天记录，并在聊天列表中删除。 → id = com.tencent.mm:id/kpi
            //确定 → id = com.tencent.mm:id/knx → description
            retryCheckTaskWithLog("点击确定", timeOutMillis = 10000) {
                wxAccessibilityService.clickByText("确定")
            }
        }
    }
}