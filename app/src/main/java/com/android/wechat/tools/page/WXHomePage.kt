package com.android.wechat.tools.page

import com.android.accessibility.ext.acc.*
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.service.WXAccessibility
import com.android.wechat.tools.service.wxAccessibilityService
import kotlinx.coroutines.delay

object WXHomePage : IPage {

    enum class NodeInfo(val nodeText: String, val nodeId: String, val des: String) {
        HomeBottomNavNode("", "com.tencent.mm:id/fj3", "首页底导布局"),
        BottomNavContactsTabNode("通讯录", "com.tencent.mm:id/f2s", "首页底导【通讯录】tab"),
        BottomNavMineTabNode("我", "com.tencent.mm:id/f2s", "首页底导【我】tab"),
        HomeRightTopPlusNode("", "com.tencent.mm:id/grs", "首页右上角【加号】按钮"),
        CreateGroupNode(
            "发起群聊",
            "com.tencent.mm:id/knx",
            "点击首页右上角【加号】按钮后弹框中的【发起群聊】按钮"
        ),
    }

    override fun pageClassName() = "com.tencent.mm.ui.LauncherUI"

    override fun pageTitleName() = "微信首页"

    /**
     * com.tencent.mm:id/fj3是微信首页底导布局id
     * 找到这个节点就可以说明当前在微信首页
     */
    override fun isMe(): Boolean {
        return wxAccessibilityService?.findById(NodeInfo.HomeBottomNavNode.nodeId) != null
    }

    /**
     * 等待打开微信APP
     */
    suspend fun waitEnterWxApp(): Boolean {
        return retryCheckTaskWithLog("等待打开微信APP") {
            WXAccessibility.isInWXApp.get()
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
                if (doubleClick) {
                    wxAccessibilityService?.findByIdAndText(
                        NodeInfo.BottomNavContactsTabNode.nodeId,
                        NodeInfo.BottomNavContactsTabNode.nodeText
                    ).click()
                    delay(300)
                }
                wxAccessibilityService?.findByIdAndText(
                    NodeInfo.BottomNavContactsTabNode.nodeId,
                    NodeInfo.BottomNavContactsTabNode.nodeText
                ).click()
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
                    NodeInfo.BottomNavMineTabNode.nodeId,
                    NodeInfo.BottomNavMineTabNode.nodeText
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
                wxAccessibilityService.clickById(NodeInfo.HomeRightTopPlusNode.nodeId)
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
                wxAccessibilityService.clickById(NodeInfo.CreateGroupNode.nodeId)
//                wxAccessibilityService?.findById("com.tencent.mm:id/knx")?.click() == true
            }
        }
    }

}