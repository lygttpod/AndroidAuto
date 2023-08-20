package com.android.wechat.tools.page.group

import com.android.accessibility.ext.acc.clickById
import com.android.accessibility.ext.acc.findById
import com.android.accessibility.ext.acc.findByText
import com.android.accessibility.ext.acc.selectChildByScroll
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.accessibility.ext.task.retryTaskWithLog
import com.android.wechat.tools.data.NodeInfo
import com.android.wechat.tools.helper.TaskByGroupHelper
import com.android.wechat.tools.page.IPage
import com.android.wechat.tools.service.wxAccessibilityService
import com.android.wechat.tools.version.nodeProxy

object WXCreateGroupPage : IPage {

    interface Nodes {
        val createGroupPageTitleNode: NodeInfo
        val createGroupUserListNode: NodeInfo
        val createGroupUserNode: NodeInfo
        val createGroupCompleteNode: NodeInfo

        companion object : Nodes by nodeProxy()
    }

    override fun pageClassName(): String = ""

    override fun pageTitleName() = "发起群聊"

    override fun isMe(): Boolean {
        //页面 android.widget.TextView → text = 发起群聊 → id = android:id/text1
        return wxAccessibilityService?.findByText(Nodes.createGroupPageTitleNode.nodeText) != null
    }

    /**
     * 是否在聊天页获取到用户列表数据
     */
    suspend fun inPage(): Boolean {
        return retryCheckTaskWithLog(
            "判断是否是在发起群聊页",
            timeOutMillis = 30_000
        ) { isMe() && isShowUserList() }
    }

    private fun isShowUserList(): Boolean {
        return wxAccessibilityService?.findById(Nodes.createGroupUserNode.nodeId) != null
    }

    suspend fun selectUser(): List<String> {
        val lastUser = TaskByGroupHelper.alreadyCheckedUsers.lastOrNull()
        val selectCount = TaskByGroupHelper.GROUP_USER_MAX_COUNT
        val log = if (lastUser.isNullOrBlank()) {
            "通过滑动去选中${selectCount}个好友"
        } else {
            "通过滑动去选中${lastUser}之后的${selectCount}个好友"
        }
        return retryTaskWithLog(log, timeOutMillis = 5 * 60_000) {
            val findNodes = wxAccessibilityService.selectChildByScroll(
                Nodes.createGroupUserListNode.nodeId,
                Nodes.createGroupUserNode.nodeId,
                TaskByGroupHelper.GROUP_USER_MAX_COUNT,
                lastText = TaskByGroupHelper.alreadyCheckedUsers.lastOrNull()
            )
            //TODO 当 findNodes.size == 1 说明最后恰好剩一个待验证的好友了，一个人是无法拉群的，那就自行验证吧
            TaskByGroupHelper.alreadyCheckedUsers.addAll(findNodes)
            findNodes
        } ?: listOf()
    }

    /**
     * 点击完成按钮
     */
    suspend fun clickCompleteBtn(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击【完成】按钮") {
                //android.widget.Button → text = 完成 → id = com.tencent.mm:id/e9s
                wxAccessibilityService.clickById(Nodes.createGroupCompleteNode.nodeId)
            }
        }
    }
}