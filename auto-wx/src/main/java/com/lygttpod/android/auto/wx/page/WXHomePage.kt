package com.lygttpod.android.auto.wx.page

import android.util.Log
import com.android.accessibility.ext.acc.*
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.lygttpod.android.auto.wx.data.NodeInfo
import com.lygttpod.android.auto.wx.service.WXAccessibility
import com.lygttpod.android.auto.wx.service.wxAccessibilityService
import com.lygttpod.android.auto.wx.version.nodeProxy
import kotlinx.coroutines.delay

object WXHomePage : IPage {

    interface Nodes {
        val homeBottomNavNode: NodeInfo
        val bottomNavContactsTabNode: NodeInfo
        val bottomNavMineTabNode: NodeInfo
        val homeRightTopPlusNode: NodeInfo
        val createGroupNode: NodeInfo

        companion object : Nodes by nodeProxy()
    }

    override fun pageClassName() = "com.tencent.mm.ui.LauncherUI"

    override fun pageTitleName() = "微信首页"

    /**
     * com.tencent.mm:id/fj3是微信首页底导布局id
     * 找到这个节点就可以说明当前在微信首页
     */
    override fun isMe(): Boolean {
        return wxAccessibilityService?.findById(Nodes.homeBottomNavNode.nodeId) != null
    }

    /**
     * 等待打开微信APP
     */
    suspend fun waitEnterWxApp(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("等待打开微信APP") {
                val inApp = isMe()
                WXAccessibility.isInWXApp.set(inApp)
                inApp
            }
        }
    }

    /**
     * 回到微信首页
     */
    suspend fun backToHome(): Boolean {
        return retryCheckTaskWithLog("打开[微信首页]") {
            if (isMe()) {
                true
            } else {
                // TODO: 判断不准确，待完善
                if (WXAccessibility.isInWXApp.get()) {
                    wxAccessibilityService?.pressBackButton()
                } else {
                    throw RuntimeException("检测到不再微信首页了，终止自动化程序")
                }
                false
            }
        }
    }

    /**
     * 点击通讯录tab
     */
    suspend fun clickContactsTab(doubleClick: Boolean = false): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【通讯录】tab") {
                val tabNode = wxAccessibilityService?.findByIdAndText(Nodes.bottomNavContactsTabNode.nodeId, Nodes.bottomNavContactsTabNode.nodeText)
                if (doubleClick) {
                    Log.d("clickContactsTab", "双击 通讯录 tab 第一次")
                    tabNode.click()
                    delay(300)
                    Log.d("clickContactsTab", "双击 通讯录 tab 第二次")
                    tabNode.click()
                } else {
                    if (tabNode?.isSelected == true) {
                        Log.d("clickContactsTab", "已经在 通讯录 页面了  无需再点击")
                        true
                    } else {
                        Log.d("clickContactsTab", "单击 通讯录 tab")
                        tabNode.click()
                    }
                }
            }
        }
    }

    /**
     * 点击通讯录tab
     */
    suspend fun clickMineTab(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【我的】tab") {
                wxAccessibilityService?.findByIdAndText(
                    Nodes.bottomNavMineTabNode.nodeId,
                    Nodes.bottomNavMineTabNode.nodeText
                ).click()
            }
        }
    }

    /**
     * 点击微信首页右上角的➕按钮
     */
    suspend fun clickRightTopPlusBtn(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击首页右上角的【＋】按钮") {
                //android.widget.RelativeLayout → text =  → id = com.tencent.mm:id/grs → description = 更多功能按钮
                wxAccessibilityService.clickById(Nodes.homeRightTopPlusNode.nodeId)
//                wxAccessibilityService?.findById("com.tencent.mm:id/grs")?.click() == true
            }
        }
    }

    /**
     * 点击发起群聊按钮
     */
    suspend fun clickCreateGroupBtn(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【发起群聊】按钮") {
                //android.widget.TextView → text = 发起群聊 → id = com.tencent.mm:id/knx
                wxAccessibilityService.clickById(Nodes.createGroupNode.nodeId)
//                wxAccessibilityService?.findById("com.tencent.mm:id/knx")?.click() == true
            }
        }
    }

}