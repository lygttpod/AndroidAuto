package com.android.wechat.tools.helper

import android.util.Log
import com.android.accessibility.ext.acc.pressBackButton
import com.android.accessibility.ext.goToWx
import com.android.accessibility.ext.toast
import com.android.wechat.tools.App
import com.android.wechat.tools.ktx.formatTime
import com.android.wechat.tools.page.WXHomePage
import com.android.wechat.tools.page.group.WXCreateGroupPage
import com.android.wechat.tools.page.group.WXGroupChatPage
import com.android.wechat.tools.page.group.WXGroupInfoPage
import com.android.wechat.tools.page.group.WXGroupManagerPage
import com.android.wechat.tools.service.wxAccessibilityService
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 拉群检测好友状态任务
 */
object TaskByGroupHelper {

    //微信规则：1、同时最多邀请40人创建群；2、超过30人的群会出现单独向用户发送群聊邀请的消息
    //所以就设为一个稍微安全一点的值
    const val GROUP_USER_MAX_COUNT = 28

    //已检测过的用户列表
    val alreadyCheckedUsers = mutableListOf<String>()

    //全部好友是否检查完毕
    var isCheckedFinished = AtomicBoolean(false)

    suspend fun startCheckByCreateGroup() {
        alreadyCheckedUsers.clear()
        isCheckedFinished.set(false)
        FriendStatusHelper.init()
        App.instance().goToWx()
        App.instance().toast("正在准备检测环境，稍后自动开始")
        FriendStatusHelper.taskCallBack?.onTaskStart()
        val taskStart = System.currentTimeMillis()
        singleTask()
        val taskEnd = System.currentTimeMillis() - taskStart
        FriendStatusHelper.taskCallBack?.onTaskEnd(taskEnd)
        Log.d("LogTracker", "startCheckByCreateGroup: 任务执行结束，耗时${taskEnd.formatTime()}")
        Log.d("result", "${alreadyCheckedUsers}")
        //任务结束返回APP
        wxAccessibilityService?.pressBackButton()
    }

    private suspend fun singleTask() {
        //判断当前是否进入到微信
        val inWxApp = WXHomePage.waitEnterWxApp()
        if (!inWxApp) return
        //判断当前是否已经成功打开微信
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
        Log.d("LogTracker", "selectUser: ${selectUser?.size}")
        //当发现选择建群的用户少于2个人的时候，无法建群(即选择一个人是无法拉群的)
        if (selectUser == null || selectUser.size < 2) {
            wxAccessibilityService?.pressBackButton()
            return
        }
        //未监测完就循环执行以上步骤
        if (!isCheckedFinished.get()) {
            singleTask()
        }

//        //点击完成，开始建群
//        val isClickComplete = WXCreateGroupPage.clickCompleteBtn()
//        if (!isClickComplete) return
//        //判断是否进入到群聊页，因为创建群需要一定的时间
//        val inGroupCHatPage = WXGroupChatPage.inPage()
//        if (!inGroupCHatPage) return
//        //在群里判断好友状态
//        val result = WXGroupChatPage.checkUserStatus()
//        FriendStatusHelper.addCheckResults(result)
//
//        //=======检测完毕开始解散群和删除群=======
//
//        //点击群聊右上角按钮
//        val isClickMore = WXGroupChatPage.clickMoreBtn()
//        if (!isClickMore) return
//        //点击群管理按钮
//        val clickGroupManager = WXGroupInfoPage.clickGroupManager()
//        if (!clickGroupManager) return
//        //点击解散该群聊按钮
//        val clickDisbandGroup = WXGroupManagerPage.clickDisbandGroup()
//        if (!clickDisbandGroup) return
//        //点击弹框确定解散按钮
//        val clickDisbandGroupDialog = WXGroupManagerPage.clickDisbandGroupDialog()
//        if (!clickDisbandGroupDialog) return
//        //再次点击群聊右上角按钮
//        val clickMoreAgain = WXGroupChatPage.clickMoreBtn()
//        if (!clickMoreAgain) return
//        //点击删除按钮
//        val clickDeleteGroup = WXGroupInfoPage.clickDeleteGroup()
//        if (!clickDeleteGroup) return
//        //点击弹框确定删除按钮
//        val clickDeleteGroupDialog = WXGroupInfoPage.clickDeleteGroupDialog()
//        if (!clickDeleteGroupDialog) return
//
//        delay(2000)
//
//        //未监测完就循环执行以上步骤
//        if (!isCheckedFinished.get()) {
//            singleTask()
//        }

        //本次检测流程全部执行完毕
    }
}