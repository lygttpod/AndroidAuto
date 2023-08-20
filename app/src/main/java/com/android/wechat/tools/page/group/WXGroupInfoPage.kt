package com.android.wechat.tools.page.group

import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.acc.findById
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.data.NodeInfo
import com.android.wechat.tools.page.IPage
import com.android.wechat.tools.service.wxAccessibilityService
import com.android.wechat.tools.version.nodeProxy


object WXGroupInfoPage : IPage {

    interface Nodes {
        val groupManagerNode: NodeInfo
        val groupDeleteNode: NodeInfo
        val groupDeleteDialogConfirmNode: NodeInfo

        companion object : Nodes by nodeProxy()
    }

    override fun pageClassName() = ""

    override fun pageTitleName() = "群聊天信息页"

    override fun isMe(): Boolean {
        // 群管理 → id = android:id/text1
        return wxAccessibilityService?.findById(Nodes.groupManagerNode.nodeText) != null
    }

    suspend fun clickGroupManager(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【群管理】按钮") {
                wxAccessibilityService.clickByText("群管理")
            }
        }
    }

    suspend fun clickDeleteGroup(): Boolean {
        return delayAction {
            //android.widget.TextView → text = 删除 → id = com.tencent.mm:id/khj
            retryCheckTaskWithLog("点击【删除】按钮") {
                wxAccessibilityService.clickByText(Nodes.groupDeleteNode.nodeText)
            }
        }
    }

    suspend fun clickDeleteGroupDialog(): Boolean {
        return delayAction {
            //text = 清空聊天记录，并在聊天列表中删除。 → id = com.tencent.mm:id/kpi
            //确定 → id = com.tencent.mm:id/knx → description
            retryCheckTaskWithLog("点击【确定】按钮") {
                wxAccessibilityService.clickByText(Nodes.groupDeleteDialogConfirmNode.nodeText)
            }
        }
    }
}