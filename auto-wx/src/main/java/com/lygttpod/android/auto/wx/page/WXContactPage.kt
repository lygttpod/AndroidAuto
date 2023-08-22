package com.lygttpod.android.auto.wx.page

import android.util.Log
import com.android.accessibility.ext.acc.*
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.accessibility.ext.task.retryTaskWithLog
import com.lygttpod.android.auto.wx.data.NodeInfo
import com.lygttpod.android.auto.wx.helper.TaskHelper
import com.lygttpod.android.auto.wx.service.wxAccessibilityService
import com.lygttpod.android.auto.wx.version.nodeProxy
import kotlin.streams.toList

object WXContactPage : IPage {

    interface Nodes {
        val contactTitleNode: NodeInfo
        val contactListNode: NodeInfo
        val contactUserNode: NodeInfo

        companion object : Nodes by nodeProxy()
    }

    override fun pageClassName() = "com.tencent.mm.plugin.profile.ui.ContactInfoUI"

    override fun pageTitleName() = "通讯录tab页"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findByIdAndText(
            Nodes.contactTitleNode.nodeId,
            Nodes.contactTitleNode.nodeText
        ) != null
    }

    suspend fun inPage(): Boolean {
        return retryCheckTaskWithLog("判断是否在通讯录列表并且获得了用户列表") {
            isMe() && isShowUserList()
        }
    }

    private fun isShowUserList(): Boolean {
        return wxAccessibilityService?.findById(Nodes.contactUserNode.nodeId) != null
    }

    /**
     * 通过字段滑动列表去找到需要点击的用户
     */
    suspend fun scrollToFindUserThenClick(userName: String): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【$userName】用户", timeOutMillis = 60_000) {
                wxAccessibilityService.scrollToClickByText(
                    Nodes.contactListNode.nodeId,
                    userName
                )
            }
        }
    }

    /**
     * 通过上次点击昵称去找下一个需要点击的节点
     */
    suspend fun scrollToClickNextNodeByCurrentText(lastUser: String?): String? {
        return delayAction {
            val taskName =
                if (lastUser.isNullOrBlank()) "寻找并点击第一个好友" else "寻找并点击【$lastUser】的下一个好友"
            retryTaskWithLog(taskName, timeOutMillis = 5 * 60_000) {
                val find = wxAccessibilityService.scrollToFindNextNodeByCurrentText(
                    Nodes.contactListNode.nodeId,
                    Nodes.contactUserNode.nodeId,
                    lastUser,
                    filterTexts = mutableListOf("微信团队", "文件传输助手").apply {
                        TaskHelper.myWxInfo?.nickName?.let { this.add(it) }
                    }
                )
                find.click()
                find?.text.default()
            }
        }
    }

    suspend fun getAllFriends(): List<String> {
        return delayAction {
            retryTaskWithLog("获取通讯录好友列表", timeOutMillis = 2 * 60_000) {
                val friends = wxAccessibilityService.findAllChildByScroll(
                    Nodes.contactListNode.nodeId,
                    Nodes.contactUserNode.nodeId
                )
                val result = friends.stream().map { it.text.default() }
                    .filter { it != "微信团队" && it != "文件传输助手" }.toList()
                Log.d("printNodeInfo", "好友个数: ${result.size} ----> 好友列表：${result}")
                result
            } ?: listOf()
        }
    }
}