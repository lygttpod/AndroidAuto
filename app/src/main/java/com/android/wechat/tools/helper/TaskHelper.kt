package com.android.wechat.tools.helper

import com.android.accessibility.ext.acc.pressBackButton
import com.android.accessibility.ext.goToWx
import com.android.accessibility.ext.toast
import com.android.wechat.tools.App
import com.android.wechat.tools.data.WxUserInfo
import com.android.wechat.tools.em.CheckStatus
import com.android.wechat.tools.em.FriendStatus
import com.android.wechat.tools.em.toFriendStatus
import com.android.wechat.tools.page.*
import com.android.wechat.tools.service.wxAccessibilityService

object TaskHelper {

    private var lastCheckUser: WxUserInfo? = null

    var myWxInfo: WxUserInfo? = null

    suspend fun startCheckFromList(list: List<String>) {
        FriendStatusHelper.clearFriendsStatusList()
        App.instance().goToWx()
        App.instance().toast("正在准备检测环境，请稍等")
        //判断当前是否进入到微信
        val inWxApp = WXHomePage.waitEnterWxApp()
        if (!inWxApp) return
        //判断当前是否已经成功打开微信
        val isHome = WXHomePage.backToHome()
        if (!isHome) return
        //点击底部通讯录Tab 点两次是为了让列表回到顶部初始状态
        val clickContactsTab = WXHomePage.clickContactsTab(true)
        if (!clickContactsTab) return
        list.forEach { singleTask(it) }
        if (WXHomePage.backToHome()) {
            wxAccessibilityService?.pressBackButton()
        }
        App.instance().toast("检测结束，请回到APP查看好友状态")
    }

    private suspend fun singleTask(user: String) {
        //回到微信首页
        val isHome = WXHomePage.backToHome()
        if (!isHome) return
        //点击底部通讯录Tab
        val clickContactsTab = WXHomePage.clickContactsTab()
        if (!clickContactsTab) return
        //判断是否从微信服务器拉到了用户信息
        val isShowUserList = WXContactPage.inPage()
        if (!isShowUserList) return
        //当前在通讯录页面，点击某一个用户
        val clickUser = WXContactPage.scrollToFindUserThenClick(user)
        if (!clickUser) return
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
        //第一次判断是否是正常好友
        val friendStatus = WXRemittancePage.checkIsNormalFriend()
        if (friendStatus != null && friendStatus.status == FriendStatus.NORMAL) {
            //提前判断出结果，此次任务结束
            userInfo.status = friendStatus.status
            FriendStatusHelper.addCheckResult(userInfo)
            return
        }
        //输入转账金额
        val inputMoney = WXRemittancePage.inputMoney()
        if (!inputMoney) return
        //点击转账按钮
        val clickTransfer = WXRemittancePage.clickTransferMoney()
        if (!clickTransfer) return
        //检查页面元素判断好友状态
        val status = WXRemittancePage.checkStatus()
        friendStatus!!.status = status.toFriendStatus()
        userInfo.status = friendStatus.status
        FriendStatusHelper.addCheckResult(userInfo)
        if (status == CheckStatus.BLACK || status == CheckStatus.DELETE || status == CheckStatus.ACCOUNT_EXCEPTION) {
            //被删除或者被拉黑的 会弹框，需要点击【我知道了】 关闭弹框
            WXRemittancePage.clickIKnow()
        }
        //回到首页 重复操作
        WXHomePage.backToHome()
    }

    suspend fun getUserList(isBack2App: Boolean = false): List<String> {
        FriendStatusHelper.clearFriends()
        App.instance().goToWx()
        App.instance().toast("正在准备检测环境，请稍等")
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
        val start = System.currentTimeMillis()
        val friends = WXContactPage.getAllFriends()
        //点击底部通讯录Tab，回到列表初始状态
        WXHomePage.clickContactsTab()
        FriendStatusHelper.addFriends(friends)
        val end = System.currentTimeMillis()
        FriendStatusHelper.taskCallBack?.onTaskEnd(end - start)

        if (isBack2App && WXHomePage.backToHome()) {
            wxAccessibilityService?.pressBackButton()
        }
        App.instance().toast("好友列表获取完毕")
        return friends
    }

    suspend fun quickCheck() {
        FriendStatusHelper.init()
        lastCheckUser = null
        App.instance().goToWx()
        App.instance().toast("正在准备检测环境，请稍等")
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
        val start = System.currentTimeMillis()
        if (!clickContactsTab) return
        quickCheckTask()
        val end = System.currentTimeMillis()
        FriendStatusHelper.taskCallBack?.onTaskEnd(end - start)
        if (WXHomePage.backToHome()) {
            wxAccessibilityService?.pressBackButton()
        }
        App.instance().toast("检测结束，请回到APP查看好友状态")
    }

    private suspend fun quickCheckTask() {
        //回到微信首页
        val isHome = WXHomePage.backToHome()
        if (!isHome) return
        //点击底部通讯录Tab
        val clickContactsTab = WXHomePage.clickContactsTab()
        if (!clickContactsTab) return
        //判断是否从微信服务器拉到了用户信息
        val isShowUserList = WXContactPage.inPage()
        if (!isShowUserList) return
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