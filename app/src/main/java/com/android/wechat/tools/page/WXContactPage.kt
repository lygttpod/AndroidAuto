package com.android.wechat.tools.page

import android.util.Log
import com.android.accessibility.ext.acc.*
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.service.wxAccessibilityService

object WXContactPage : IPage {
    override fun pageClassName() = "com.tencent.mm.plugin.profile.ui.ContactInfoUI"

    override fun pageTitleName() = "通讯录tab页"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findByIdAndText("android:id/text1", "通讯录") != null
    }

    /**
     * 点击设置的用户名
     */
    suspend fun clickUser(userName: String): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击用户《$userName》") {
                wxAccessibilityService.clickByText(userName)
            }
        }
    }

    /**
     * 通过字段滑动列表去找到需要点击的用户
     */
    suspend fun scrollToFindUserThenClick(userName: String): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击用户$userName", timeOutMillis = 60000) {
                wxAccessibilityService.scrollToClickByText("com.tencent.mm:id/js", userName)
            }
        }
    }

    suspend fun findAllFriends(): List<String> {
        val friends = wxAccessibilityService.findAllChildByScroll(
            "com.tencent.mm:id/js",
            "com.tencent.mm:id/hg4",
            stopFindBlock = {
                //通讯录列表最后是 text = xxx个朋友 → id = com.tencent.mm:id/bmm
                //找到这个元素说明滚动到底了，可以终止滚动搜索
                wxAccessibilityService?.findById("com.tencent.mm:id/bmm") != null
            }
        )
        val result = friends.map { it.text.default() }.toList()
        Log.d("printNodeInfo", "好友个数: ${result.size}====好友列表：${result}")
        return result
    }
}