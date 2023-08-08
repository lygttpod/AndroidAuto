package com.android.wechat.tools.page.group

import android.util.Log
import com.android.accessibility.ext.acc.*
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.accessibility.ext.task.retryTaskWithLog
import com.android.wechat.tools.helper.TaskByGroupHelper
import com.android.wechat.tools.page.IPage
import com.android.wechat.tools.service.wxAccessibilityService

object WXCreateGroupPage : IPage {

    enum class NodeInfo(val nodeText: String, val nodeId: String, val des: String) {
        CreateGroupPageTitleNode("发起群聊", "android:id/text1", "发起群聊页面"),
        CreateGroupUserListNode("", "com.tencent.mm:id/j9o", "用户列表-RecyclerView"),
        CreateGroupUserNode("", "com.tencent.mm:id/hg4", "用户昵称"),
        CreateGroupCompleteNode("完成", "com.tencent.mm:id/e9s", "右下角的【完成】按钮"),
    }

    override fun pageClassName(): String = ""

    override fun pageTitleName() = "发起群聊"

    override fun isMe(): Boolean {
        //页面 android.widget.TextView → text = 发起群聊 → id = android:id/text1
        return wxAccessibilityService?.findByText(NodeInfo.CreateGroupPageTitleNode.nodeText) != null
    }

    /**
     * 是否在聊天页获取到用户列表数据
     */
    suspend fun inPage(): Boolean {
        return retryCheckTaskWithLog("判断是否是在发起群聊页", timeOutMillis = 10000) { isMe() && isShowUserList() }
    }

    private fun isShowUserList(): Boolean {
        return wxAccessibilityService?.findById(NodeInfo.CreateGroupUserNode.nodeId) != null
    }

    suspend fun selectUser(): List<String>? {
        return delayAction {
            retryTaskWithLog("选中需要拉群的用户", timeOutMillis = 5 * 60_000) {
                //androidx.recyclerview.widget.RecyclerView → text =  → id = com.tencent.mm:id/j9o
                //android.widget.TextView → text = XXX → id = com.tencent.mm:id/hg4

                if (TaskByGroupHelper.alreadyCheckedUsers.isEmpty()) {
                    scroll2SelectUser()
                } else {
                    val lastUser = TaskByGroupHelper.alreadyCheckedUsers.last()
                    val scroll2TargetScreen = retryTaskWithLog(
                        "滚动到上次选中的最后一个好友【${lastUser}】的位置",
                        timeOutMillis = 2 * 60_000
                    ) {
                        wxAccessibilityService.scrollToFindByText(
                            NodeInfo.CreateGroupUserListNode.nodeId,
                            lastUser
                        )
                    }
                    if (scroll2TargetScreen != null) {
                        scroll2SelectUser(TaskByGroupHelper.alreadyCheckedUsers.lastOrNull())
                    } else {
                        null
                    }
                }

            }
        }
    }

    private suspend fun scroll2SelectUser(startText: String? = null): List<String>? {
        return retryTaskWithLog("通过滑动去选中好友", timeOutMillis = 5 * 60_000) {
            val findNodes = wxAccessibilityService.selectChildByScroll(
                NodeInfo.CreateGroupUserListNode.nodeId,
                NodeInfo.CreateGroupUserNode.nodeId,
                TaskByGroupHelper.GROUP_USER_MAX_COUNT,
                startText = startText
            ) {
                TaskByGroupHelper.isCheckedFinished.set(true)
            }
            if (findNodes.size == 1) {
                //TODO 最后恰好剩一个待验证的好友了，一个人是无法拉群的，那就自行验证吧
                Log.d(
                    "scroll2SelectUser",
                    "最后恰好剩一个待验证的好友【${findNodes.lastOrNull()}】了，一个人是无法拉群的"
                )
            }
            val originSize = TaskByGroupHelper.alreadyCheckedUsers.size
            TaskByGroupHelper.alreadyCheckedUsers.addAll(findNodes)
            val newSize = TaskByGroupHelper.alreadyCheckedUsers.size
            Log.d(
                "selectUser",
                "originSize = $originSize  newSize = $newSize "
            )
            findNodes
        }

    }

    /**
     * 点击完成按钮
     */
    suspend fun clickCompleteBtn(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【完成】按钮") {
                //android.widget.Button → text = 完成 → id = com.tencent.mm:id/e9s
                wxAccessibilityService.clickById(NodeInfo.CreateGroupCompleteNode.nodeId)
            }
        }
    }
}