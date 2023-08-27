package com.lygttpod.android.auto.wx.helper

import android.content.Context
import com.android.accessibility.ext.acc.pressBackButton
import com.android.accessibility.ext.goToWx
import com.android.accessibility.ext.toast
import com.lygttpod.android.auto.wx.data.WxUserInfo
import com.lygttpod.android.auto.wx.page.WXHomePage
import com.lygttpod.android.auto.wx.page.group.WXCreateGroupPage
import com.lygttpod.android.auto.wx.page.group.WXGroupChatPage
import com.lygttpod.android.auto.wx.page.group.WXGroupInfoPage
import com.lygttpod.android.auto.wx.page.group.WXGroupManagerPage
import com.lygttpod.android.auto.wx.service.wxAccessibilityService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 拉群检测好友状态任务
 */
object TaskByGroupHelper {

    private val taskScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    //微信规则：1、同时最多邀请40人创建群；2、超过30人的群会出现单独向用户发送群聊邀请的消息
    //所以就设为一个稍微安全一点的值
    const val GROUP_USER_MAX_COUNT = 28

    //已检测过的用户列表
    val alreadyCheckedUsers = mutableListOf<String>()

    private val mutex = Mutex()

    fun startTask(context: Context) {
        taskScope.launch {
            if (mutex.isLocked) {
                context.toast("上次任务还没结束哦(有重试机制)，请稍等再试")
                return@launch
            }
            mutex.withLock {
                FriendStatusHelper.taskCallBack?.onTaskStart("正在执行【拉群检测】任务")
                val start = System.currentTimeMillis()
                startCheckByCreateGroup(context)
                val end = System.currentTimeMillis()
                FriendStatusHelper.taskCallBack?.onTaskEnd(end - start)
            }
        }
    }

    private suspend fun startCheckByCreateGroup(context: Context) {
        alreadyCheckedUsers.clear()
        FriendStatusHelper.clearFriendsStatusList()
        context.goToWx()
        //判断当前是否进入到微信
        val inWxApp = WXHomePage.waitEnterWxApp()
        if (!inWxApp) return
        singleTask()
        //任务结束返回APP
        val isHome = WXHomePage.backToHome()
        if (isHome) {
            wxAccessibilityService?.pressBackButton()
        }
    }

    private suspend fun singleTask() {
        val isHome = WXHomePage.backToHome()
        if (!isHome) return
        //微信首页添加好友的图标
        val isClickPlusBtn = WXHomePage.clickRightTopPlusBtn()
        if (!isClickPlusBtn) return
        //点击发起群聊按钮
        val isClickCreateGroup = WXHomePage.clickCreateGroupBtn()
        if (!isClickCreateGroup) return
        //判断当前是在发起群聊页
        val isCreateGroupPage = WXCreateGroupPage.inPage()
        if (!isCreateGroupPage) return
        //选择这次选择入群的好友列表
        val selectUser = WXCreateGroupPage.selectUser()
        //当发现选择建群的用户少于2个人的时候，无法建群(即选择一个人是无法拉群的)
        if (selectUser.size <= 1) {
            if (selectUser.size == 1) {
                FriendStatusHelper.addCheckResults(mutableListOf(WxUserInfo(selectUser.first())))
            }
            WXHomePage.backToHome()
            return
        }
        //点击完成，开始建群
        val isClickComplete = WXCreateGroupPage.clickCompleteBtn()
        if (!isClickComplete) return
        //判断是否进入到群聊页，因为创建群需要一定的时间
        val inGroupCHatPage = WXGroupChatPage.inPage()
        if (!inGroupCHatPage) return
        //在群里判断好友状态
        val result = WXGroupChatPage.checkUserStatus()
        FriendStatusHelper.addCheckResults(result)

        //=======检测完毕开始解散群和删除群=======

        //点击群聊右上角按钮
        val isClickMore = WXGroupChatPage.clickMoreBtn()
        if (!isClickMore) return
        //是否在群聊信息页
        val groupManagerBtnShow = WXGroupInfoPage.groupManagerBtnShow()
        if (!groupManagerBtnShow) return
        //点击群管理按钮
        val clickGroupManager = WXGroupInfoPage.clickGroupManager()
        if (!clickGroupManager) return
        //是否在群管页面
        val inGroupManagerPage = WXGroupManagerPage.inPage()
        if (!inGroupManagerPage) return
        //点击解散该群聊按钮
        val clickDisbandGroup = WXGroupManagerPage.clickDisbandGroup()
        if (!clickDisbandGroup) return
        //点击弹框确定解散按钮
        val clickDisbandGroupDialog = WXGroupManagerPage.clickDisbandGroupDialog()
        if (!clickDisbandGroupDialog) return
        //再次点击群聊右上角按钮
        val clickMoreAgain = WXGroupChatPage.clickMoreBtn()
        if (!clickMoreAgain) return
        //是否出现删除按钮
        val deleteShow = WXGroupInfoPage.deleteShow()
        if (!deleteShow) return
        //点击删除按钮
        val clickDeleteGroup = WXGroupInfoPage.clickDeleteGroup()
        if (!clickDeleteGroup) return
        //点击弹框确定删除按钮
        val clickDeleteGroupDialog = WXGroupInfoPage.clickDeleteGroupDialog()
        if (!clickDeleteGroupDialog) return

        delay(2000)
        //循环执行以上步骤
        singleTask()
    }
}