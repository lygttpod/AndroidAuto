package com.android.wechat.tools.helper

import com.android.accessibility.ext.goToWx
import com.android.accessibility.ext.toast
import com.android.wechat.tools.App
import com.android.wechat.tools.page.WXHomePage
import com.android.wechat.tools.page.group.WXCreateGroupPage
import com.android.wechat.tools.page.group.WXGroupChatPage
import com.android.wechat.tools.page.group.WXGroupInfoPage
import com.android.wechat.tools.page.group.WXGroupManagerPage
import kotlinx.coroutines.delay


object TaskByGroupHelper {

    val selectUser = mutableListOf<String>()

    suspend fun startCheckByCreateGroup() {
        FriendStatusHelper.init()
        App.instance().goToWx()
        App.instance().toast("正在准备检测环境，请稍等")
        delay(5000)
        //判断当前是否已经成功打开微信
        val isHome = WXHomePage.goHome()
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

        val selectUser = WXCreateGroupPage.selectUser()
        if (!selectUser) return

        val isClickComplete = WXCreateGroupPage.clickCompleteBtn()
        if (!isClickComplete) return

        val inGroupCHatPage = WXGroupChatPage.inPage()
        if (!inGroupCHatPage) return

        val result = WXGroupChatPage.checkUserStatus()
        FriendStatusHelper.addCheckResults(result)

        //检测完毕开始解散群和删除群
        val isClickMore = WXGroupChatPage.clickMoreBtn()
        if (!isClickMore) return

        val clickGroupManager = WXGroupInfoPage.clickGroupManager()
        if (!clickGroupManager) return

        val clickDisbandGroup = WXGroupManagerPage.clickDisbandGroup()
        if (!clickDisbandGroup) return

        val clickDisbandGroupDialog = WXGroupManagerPage.clickDisbandGroupDialog()
        if (!clickDisbandGroupDialog) return

        val clickMoreAgain = WXGroupChatPage.clickMoreBtn()
        if (!clickMoreAgain) return

        val clickDeleteGroup = WXGroupInfoPage.clickDeleteGroup()
        if (!clickDeleteGroup) return

        val clickDeleteGroupDialog = WXGroupInfoPage.clickDeleteGroupDialog()
        if (!clickDeleteGroupDialog) return

        //删除后回到首页，重复以上步骤

    }
}