package com.android.wechat.tools.page

import com.android.accessibility.ext.acc.*
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.service.wxAccessibilityService

object WXChattingPage : IPage {

    override fun pageClassName() = "com.tencent.mm.ui.chatting.ChattingUI"

    override fun pageTitleName() = "微信聊天页"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findById("com.tencent.mm:id/b3q") != null
    }

    suspend fun checkInPage(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("检查当前是是否打开聊天页") {
                isMe()
            }
        }
    }

    /**
     * 点击更多功能按钮
     */
    suspend fun clickMoreOption(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击聊天页的功能区按钮") {
                wxAccessibilityService.clickById("com.tencent.mm:id/b3q")
            }
        }
    }

    /**
     * 点击转账
     */
    suspend fun clickTransferMoney(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击聊天页功能区的转账") {
//                wxAccessibilityService?.printNodeInfo()
//                wxAccessibilityService.findWithClickByText("转账")
                val find = wxAccessibilityService?.findByIdAndText("com.tencent.mm:id/vg", "转账")
                if (find == null) {
                    false
                } else {
                    wxAccessibilityService?.gestureClick(find)
                    true
                }
            }
        }
    }


}