package com.lygttpod.android.auto.wx.page.group

import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.acc.findByText
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.lygttpod.android.auto.wx.data.NodeInfo
import com.lygttpod.android.auto.wx.page.IPage
import com.lygttpod.android.auto.wx.service.wxAccessibilityService
import com.lygttpod.android.auto.wx.version.nodeProxy


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
        return wxAccessibilityService?.findByText(Nodes.groupManagerNode.nodeText) != null
    }

    suspend fun groupManagerBtnShow(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("判断是否在【群聊信息页】") {
                isMe()
            }
        }
    }

    suspend fun clickGroupManager(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【群管理】按钮") {
                wxAccessibilityService.clickByText(Nodes.groupManagerNode.nodeText)
            }
        }
    }

    suspend fun deleteShow(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("判断【删除】按钮是否显示") {
                wxAccessibilityService?.findByText(Nodes.groupDeleteNode.nodeText) != null
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
                wxAccessibilityService.clickByText(
                    Nodes.groupDeleteDialogConfirmNode.nodeText,
                    false
                )
            }
        }
    }
}