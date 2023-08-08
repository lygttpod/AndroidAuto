package com.android.wechat.tools.page

import android.util.Log
import com.android.accessibility.ext.acc.findAllChildByScroll
import com.android.accessibility.ext.acc.findById
import com.android.accessibility.ext.acc.findByIdAndText
import com.android.accessibility.ext.acc.scrollToClickByText
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.accessibility.ext.task.retryTaskWithLog
import com.android.wechat.tools.service.wxAccessibilityService
import kotlin.streams.toList

object WXContactPage : IPage {

    enum class NodeInfo(val nodeText: String, val nodeId: String, val des: String) {
        ContactTitleNode("通讯录", "android:id/text1", "通讯录标题"),
        ContactListNode("", "com.tencent.mm:id/js", "通讯录列表-RecyclerView"),
        ContactUserNode("", "com.tencent.mm:id/hg4", "通讯录列表中的好友"),
        ContactUserCountNode("", "com.tencent.mm:id/bmm", "通讯录列表最底部的【xxx个朋友】node"),
    }

    override fun pageClassName() = "com.tencent.mm.plugin.profile.ui.ContactInfoUI"

    override fun pageTitleName() = "通讯录tab页"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findByIdAndText(
            NodeInfo.ContactTitleNode.nodeId,
            NodeInfo.ContactTitleNode.nodeText
        ) != null
    }

    /**
     * 通过字段滑动列表去找到需要点击的用户
     */
    suspend fun scrollToFindUserThenClick(userName: String): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【$userName】用户", timeOutMillis = 60_000) {
                wxAccessibilityService.scrollToClickByText(
                    NodeInfo.ContactListNode.nodeId,
                    userName
                )
            }
        }
    }

    suspend fun getAllFriends(): List<String> {
        return delayAction {
            retryTaskWithLog("获取通讯录好友列表", timeOutMillis = 2 * 60_000) {
                val friends = wxAccessibilityService.findAllChildByScroll(
                    NodeInfo.ContactListNode.nodeId,
                    NodeInfo.ContactUserNode.nodeId,
                    stopFindBlock = {
                        //通讯录列表最后是 text = xxx个朋友 → id = com.tencent.mm:id/bmm
                        //找到这个元素说明滚动到底了，可以终止滚动搜索
                        wxAccessibilityService?.findById(NodeInfo.ContactUserCountNode.nodeId) != null
                    }
                )
                val result = friends.stream().map { it.text.default() }
                    .filter { it != "微信团队" && it != "文件传输助手" }.toList()
                Log.d("printNodeInfo", "好友个数: ${result.size} ----> 好友列表：${result}")
                result
            } ?: listOf()
        }
    }
}