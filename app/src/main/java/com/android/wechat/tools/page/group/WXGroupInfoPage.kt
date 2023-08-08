package com.android.wechat.tools.page.group

import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.acc.findById
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.page.IPage
import com.android.wechat.tools.service.wxAccessibilityService


object WXGroupInfoPage : IPage {

    enum class NodeInfo(val nodeText: String, val nodeId: String, val des: String) {
        GroupManagerNode("群管理", "", "群聊信息页的【群管理】按钮"),
        GroupDeleteNode("删除", "com.tencent.mm:id/khj", "群聊信息页的【删除】按钮"),
        GroupDeleteDialogConfirmNode(
            "确定",
            "com.tencent.mm:id/knx",
            "点击群聊信息页的【删除】按钮后的弹窗的【确定】按钮"
        ),
    }

    override fun pageClassName() = ""

    override fun pageTitleName() = "群聊天信息页"

    override fun isMe(): Boolean {
        // 群管理 → id = android:id/text1
        return wxAccessibilityService?.findById(NodeInfo.GroupManagerNode.nodeText) != null
    }

    suspend fun clickGroupManager(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【群管理】按钮", timeOutMillis = 10000) {
                wxAccessibilityService.clickByText("群管理")
            }
        }
    }

    suspend fun clickDeleteGroup(): Boolean {
        return delayAction {
            //android.widget.TextView → text = 删除 → id = com.tencent.mm:id/khj
            retryCheckTaskWithLog("点击【删除】按钮", timeOutMillis = 10000) {
                wxAccessibilityService.clickByText(NodeInfo.GroupDeleteNode.nodeText)
            }
        }
    }

    suspend fun clickDeleteGroupDialog(): Boolean {
        return delayAction {
            //text = 清空聊天记录，并在聊天列表中删除。 → id = com.tencent.mm:id/kpi
            //确定 → id = com.tencent.mm:id/knx → description
            retryCheckTaskWithLog("点击【确定】按钮", timeOutMillis = 10000) {
                wxAccessibilityService.clickByText(NodeInfo.GroupDeleteDialogConfirmNode.nodeText)
            }
        }
    }
}