package com.android.wechat.tools.page

import android.util.Log
import com.android.accessibility.ext.acc.findById
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryTaskWithLog
import com.android.wechat.tools.data.NodeInfo
import com.android.wechat.tools.data.WxUserInfo
import com.android.wechat.tools.service.wxAccessibilityService
import com.android.wechat.tools.version.nodeProxy


object WXMinePage : IPage {

    private val TAG = WXMinePage::class.java.simpleName

    interface Nodes {
        val mineNickNameNode: NodeInfo
        val mineWxCodeNode: NodeInfo

        companion object : Nodes by nodeProxy()
    }

    override fun pageClassName() = ""

    override fun pageTitleName() = "我的页面"

    override fun isMe(): Boolean {
        return wxAccessibilityService?.findById(Nodes.mineNickNameNode.nodeId)?.text.default()
            .isNotBlank()
    }

    suspend fun getMyWxInfo(): WxUserInfo? {
        return retryTaskWithLog("获取【我的】页面的微信昵称和微信号") {
            val nickName =
                wxAccessibilityService?.findById(Nodes.mineNickNameNode.nodeId)?.text.default()
            val wxCode =
                wxAccessibilityService?.findById(Nodes.mineWxCodeNode.nodeId)?.text?.split("微信号：")
                    ?.getOrNull(1).default().trim()

            if (nickName.isNotBlank() && wxCode.isNotBlank()) {
                Log.d(TAG, "我的微信昵称: $nickName   我的微信号：$wxCode")
                WxUserInfo(nickName, wxCode)
            } else {
                null
            }
        }
    }
}