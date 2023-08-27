package com.lygttpod.android.auto.wx.page.group

import com.android.accessibility.ext.acc.clickById
import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.acc.findByText
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.lygttpod.android.auto.wx.data.NodeInfo
import com.lygttpod.android.auto.wx.page.IPage
import com.lygttpod.android.auto.wx.service.wxAccessibilityService
import com.lygttpod.android.auto.wx.version.nodeProxy


object WXGroupManagerPage : IPage {

    interface Nodes {
        val groupManagerDisbandNode: NodeInfo
        val groupManagerDialogConfirmNode: NodeInfo

        companion object : Nodes by nodeProxy()
    }

    override fun pageClassName() = ""

    override fun pageTitleName() = "群管理"

    override fun isMe(): Boolean {
        //解散该群聊 → id = com.tencent.mm:id/khj
        return wxAccessibilityService?.findByText(Nodes.groupManagerDisbandNode.nodeText) != null
    }

    suspend fun inPage(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("检测是否在【群管理】页面") {
                isMe()
            }
        }
    }

    suspend fun clickDisbandGroup(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【解散该群聊】按钮") {
                wxAccessibilityService.clickByText(Nodes.groupManagerDisbandNode.nodeText)
            }
        }
    }


    suspend fun clickDisbandGroupDialog(): Boolean {
        return delayAction {
            //text = 解散群聊后，群成员和群主都将被移出群聊。 → id = com.tencent.mm:id/kpi
            //text = 解散 → id = com.tencent.mm:id/knx
            retryCheckTaskWithLog("点击【解散】按钮") {
                wxAccessibilityService.clickById(Nodes.groupManagerDialogConfirmNode.nodeId, false)
            }
        }
    }
}