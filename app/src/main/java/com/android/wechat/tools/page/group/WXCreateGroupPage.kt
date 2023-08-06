package com.android.wechat.tools.page.group

import android.util.Log
import com.android.accessibility.ext.acc.click
import com.android.accessibility.ext.acc.clickById
import com.android.accessibility.ext.acc.findByText
import com.android.accessibility.ext.acc.findChildNodes
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.wechat.tools.helper.TaskByGroupHelper
import com.android.wechat.tools.page.IPage
import com.android.wechat.tools.service.wxAccessibilityService

object WXCreateGroupPage : IPage {

    override fun pageClassName(): String = ""

    override fun pageTitleName() = "发起群聊"

    override fun isMe(): Boolean {
        //页面 android.widget.TextView → text = 发起群聊 → id = android:id/text1
        return wxAccessibilityService?.findByText("发起群聊") != null
    }

    /**
     * 回到微信首页
     */
    suspend fun inPage(): Boolean {
        return retryCheckTaskWithLog("判断是否是在发起群聊页", timeOutMillis = 10000) {
            isMe()
        }
    }

    // TODO: 当前只是一屏的用户，需要完善自动滚动查找用户
    suspend fun selectUser(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("找到当前页需要检测的用户", timeOutMillis = 60_000) {
                //androidx.recyclerview.widget.RecyclerView → text =  → id = com.tencent.mm:id/j9o
                //android.widget.TextView → text = XXX → id = com.tencent.mm:id/hg4
                val tempList = mutableListOf<String>()
                wxAccessibilityService.findChildNodes(
                    "com.tencent.mm:id/j9o",
                    "com.tencent.mm:id/hg4"
                ).forEach {
                    Log.d("selectUser", "selectUser: ${it.text.default()}")
                    tempList.add(it.text.default())
                    val nickName = it.text.default()
                    if (TaskByGroupHelper.selectUser.contains(nickName).not()) {
                        TaskByGroupHelper.selectUser.add(nickName)
                        it.click()
                    }
                }
                tempList.isNotEmpty()
            }
        }
    }

    /**
     * 点击完成按钮
     */
    suspend fun clickCompleteBtn(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击完成按钮") {
                //android.widget.Button → text = 完成 → id = com.tencent.mm:id/e9s
                wxAccessibilityService.clickById("com.tencent.mm:id/e9s")
            }
        }
    }
}