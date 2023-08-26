package com.lygttpod.android.auto.wx.helper

import android.content.Context
import com.android.accessibility.ext.acc.pressBackButton
import com.android.accessibility.ext.goToWx
import com.android.accessibility.ext.toast
import com.lygttpod.android.auto.wx.data.WxUserInfo
import com.lygttpod.android.auto.wx.em.CheckStatus
import com.lygttpod.android.auto.wx.em.FriendStatus
import com.lygttpod.android.auto.wx.em.toFriendStatus
import com.lygttpod.android.auto.wx.helper.FriendStatusHelper.lastCheckUser
import com.lygttpod.android.auto.wx.page.WXChattingPage
import com.lygttpod.android.auto.wx.page.WXContactInfoPage
import com.lygttpod.android.auto.wx.page.WXContactPage
import com.lygttpod.android.auto.wx.page.WXHomePage
import com.lygttpod.android.auto.wx.page.WXMinePage
import com.lygttpod.android.auto.wx.page.WXRemittancePage
import com.lygttpod.android.auto.wx.service.wxAccessibilityService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object TaskHelper {

    private val taskScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    var myWxInfo: WxUserInfo? = null

    private val mutex = Mutex()

    fun startGetUserTask(context: Context, isBack2App: Boolean = false) {
        taskScope.launch {
            if (mutex.isLocked) {
                context.toast("上次任务还没结束哦(有重试机制)，请稍等再试")
                return@launch
            }
            mutex.withLock {
                FriendStatusHelper.taskCallBack?.onTaskStart("正在执行【获取好友】任务")
                val start = System.currentTimeMillis()
                getUserList(context, isBack2App)
                val end = System.currentTimeMillis()
                FriendStatusHelper.taskCallBack?.onTaskEnd(end - start)
            }
        }
    }

    private suspend fun getUserList(context: Context, isBack2App: Boolean = false): List<String> {
        context.goToWx()
        //判断当前是否进入到微信
        val inWxApp = WXHomePage.waitEnterWxApp()
        if (!inWxApp) return listOf()
        //回到微信首页
        val isHome = WXHomePage.backToHome()
        if (!isHome) return listOf()
        //点击底部通讯录Tab 点两次是为了让列表回到顶部初始状态
        val clickContactsTab = WXHomePage.clickContactsTab(true)
        if (!clickContactsTab) return listOf()
        //判断是否从微信服务器拉到了用户信息
        val isShowUserList = WXContactPage.inPage()
        if (!isShowUserList) return listOf()
        val friends = WXContactPage.getAllFriends()
        //点击底部通讯录Tab，回到列表初始状态
        WXHomePage.clickContactsTab()
        FriendStatusHelper.addFriends(friends)
        if (isBack2App && WXHomePage.backToHome()) {
            wxAccessibilityService?.pressBackButton()
        }
        return friends
    }

    fun startCheckTask(context: Context, isContinueLastCheck: Boolean = false) {
        taskScope.launch {
            if (mutex.isLocked) {
                context.toast("上次任务还没结束哦(有重试机制)，请稍等再试")
                return@launch
            }
            mutex.withLock {
                FriendStatusHelper.taskCallBack?.onTaskStart("正在执行【假转账检测】任务")
                val start = System.currentTimeMillis()
                quickCheck(context, isContinueLastCheck)
                val end = System.currentTimeMillis()
                FriendStatusHelper.taskCallBack?.onTaskEnd(end - start)
            }
        }
    }

    private suspend fun quickCheck(context: Context, isContinueLastCheck: Boolean = false) {
        if (!isContinueLastCheck) {
            FriendStatusHelper.reset()
        }
        context.goToWx()
        //判断当前是否进入到微信
        val inWxApp = WXHomePage.waitEnterWxApp()
        if (!inWxApp) return
        //回到微信首页
        val isHome = WXHomePage.backToHome()
        if (!isHome) return
        val clickMineTab = WXHomePage.clickMineTab()
        if (!clickMineTab) return
        myWxInfo = WXMinePage.getMyWxInfo()
        //点击底部通讯录Tab
        val clickContactsTab = WXHomePage.clickContactsTab(true)
        if (!clickContactsTab) return
        quickCheckTask(isContinueLastCheck)
        if (WXHomePage.backToHome()) {
            wxAccessibilityService?.pressBackButton()
        }
    }

    private suspend fun quickCheckTask(isContinueLastCheck: Boolean = false) {
        //回到微信首页
        val isHome = WXHomePage.backToHome()
        if (!isHome) return
        //点击底部通讯录Tab
        val clickContactsTab = WXHomePage.clickContactsTab()
        if (!clickContactsTab) return
        //判断是否从微信服务器拉到了用户信息
        val isShowUserList = WXContactPage.inPage()
        if (!isShowUserList) return
        //【续捡】继续上次检测过的好友往下检测
        if (isContinueLastCheck) {
            WXContactPage.scrollToLastUserPosition(lastCheckUser?.nickName)
        }
        //当前在通讯录页面，点击某一个用户
        val findUser = WXContactPage.scrollToClickNextNodeByCurrentText(lastCheckUser?.nickName)
        if (findUser.isNullOrBlank()) return
        //判断是否进入到通讯录用户信息页
        val inPage = WXContactInfoPage.inPage()
        if (!inPage) return
        val userInfo = WXContactInfoPage.getUserInfo() ?: return
        //点击发消息按钮
        val clickSendMsg = WXContactInfoPage.clickSendMsg()
        if (!clickSendMsg) return
        //检查当前是否进入到聊天页（测试中发现有时候点击有效，但是没有进入到聊天页，所有在重试一次）
        val check = WXChattingPage.checkInPage()
        if (!check) {
            //再次点击发消息按钮
            val click = WXContactInfoPage.clickSendMsg()
            if (!click) return
        }
        //点击聊天页功能区按钮
        val clickMoreOption = WXChattingPage.clickMoreOption()
        if (!clickMoreOption) return
        //点击聊天页功能区转账按钮
        val clickTransferMoney = WXChattingPage.clickTransferMoney()
        if (!clickTransferMoney) return
        //输入转账金额
        val inputMoney = WXRemittancePage.inputMoney()
        if (!inputMoney) return
        //第一次判断是否是正常好友
        val friendStatus = WXRemittancePage.checkIsNormalFriend()
        if (friendStatus != null && friendStatus.status == FriendStatus.NORMAL) {
            //提前判断出结果，此次任务结束
            userInfo.status = friendStatus.status
            lastCheckUser = userInfo
            FriendStatusHelper.addCheckResult(userInfo)
            quickCheckTask()
            return
        }
        //点击转账按钮
        val clickTransfer = WXRemittancePage.clickTransferMoney()
        if (!clickTransfer) return
        //检查页面元素判断好友状态
        val status = WXRemittancePage.checkStatus()
        friendStatus!!.status = status.toFriendStatus()
        userInfo.status = friendStatus.status
        lastCheckUser = userInfo
        FriendStatusHelper.addCheckResult(userInfo)
        if (status == CheckStatus.BLACK || status == CheckStatus.DELETE || status == CheckStatus.ACCOUNT_EXCEPTION) {
            //被删除或者被拉黑的 会弹框，需要点击【我知道了】 关闭弹框
            WXRemittancePage.clickIKnow()
        }
        quickCheckTask()
    }
}