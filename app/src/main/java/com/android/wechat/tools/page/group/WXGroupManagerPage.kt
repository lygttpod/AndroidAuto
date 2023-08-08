package com.android.wechat.tools.page.group

import com.android.accessibility.ext.acc.clickById
import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.acc.findByText
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.page.IPage
import com.android.wechat.tools.service.wxAccessibilityService


object WXGroupManagerPage : IPage {

    enum class NodeInfo(val nodeText: String, val nodeId: String, val des: String) {
        GroupManagerDisbandNode(
            "解散该群聊",
            "com.tencent.mm:id/khj",
            "群管理页的【解散该群聊】按钮"
        ),
        GroupManagerDialogConfirmNode(
            "解散",
            "com.tencent.mm:id/knx",
            "点击群管理页的【解散该群聊】按钮后的弹窗的【解散】按钮"
        ),
    }

    override fun pageClassName() = ""

    override fun pageTitleName() = "群管理"

    override fun isMe(): Boolean {
        //解散该群聊 → id = com.tencent.mm:id/khj
        return wxAccessibilityService?.findByText(NodeInfo.GroupManagerDisbandNode.nodeText) != null
    }

    suspend fun clickDisbandGroup(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【解散该群聊】按钮") {
                wxAccessibilityService.clickByText(NodeInfo.GroupManagerDisbandNode.nodeText)
            }
        }
    }


    suspend fun clickDisbandGroupDialog(): Boolean {
        return delayAction {
            //text = 解散群聊后，群成员和群主都将被移出群聊。 → id = com.tencent.mm:id/kpi
            //text = 解散 → id = com.tencent.mm:id/knx
            retryCheckTaskWithLog("点击【解散】按钮") {
                wxAccessibilityService.clickById(NodeInfo.GroupManagerDialogConfirmNode.nodeId)
            }
        }
    }
}