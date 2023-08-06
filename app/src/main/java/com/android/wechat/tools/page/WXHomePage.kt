package com.android.wechat.tools.page

import com.android.accessibility.ext.acc.findById
import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.acc.pressBackButton
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.service.WXAccessibility
import com.android.wechat.tools.service.wxAccessibilityService

object WXHomePage : IPage {

    override fun pageClassName() = "com.tencent.mm.ui.LauncherUI"

    override fun pageTitleName() = "微信首页"

    /**
     * com.tencent.mm:id/fj3是微信首页底导布局id
     * 找到这个节点就可以说明当前在微信首页
     */
    override fun isMe(): Boolean {
        return wxAccessibilityService?.findById("com.tencent.mm:id/fj3") != null
    }

    /**
     * 回到微信首页
     */
    suspend fun goHome(): Boolean {
        return retryCheckTaskWithLog("打开微信首页", timeOutMillis = 10000) {
            if (isMe()) {
                true
            } else {
                delayAction(200) {
                    if (WXAccessibility.isInWXApp) {
                        wxAccessibilityService?.pressBackButton()
                    } else {
                        throw RuntimeException("检测到退出微信了，终止自动化程序")
                    }
                }
                false
            }
        }
    }

    /**
     * 点击通讯录tab
     */
    suspend fun clickContactsTab(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击通讯录tab") {
                wxAccessibilityService.clickByText("通讯录")
            }
        }
    }

}