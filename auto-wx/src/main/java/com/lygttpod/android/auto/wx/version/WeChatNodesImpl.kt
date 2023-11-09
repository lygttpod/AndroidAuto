package com.lygttpod.android.auto.wx.version

import com.lygttpod.android.auto.wx.data.NodeInfo
import com.lygttpod.android.auto.wx.helper.DeleteTaskHelper
import com.lygttpod.android.auto.wx.page.WXChattingPage
import com.lygttpod.android.auto.wx.page.WXContactInfoPage
import com.lygttpod.android.auto.wx.page.WXContactPage
import com.lygttpod.android.auto.wx.page.WXDeleteDialogPage
import com.lygttpod.android.auto.wx.page.WXHBPage
import com.lygttpod.android.auto.wx.page.WXHomePage
import com.lygttpod.android.auto.wx.page.WXMinePage
import com.lygttpod.android.auto.wx.page.WXRemittancePage
import com.lygttpod.android.auto.wx.page.group.WXCreateGroupPage
import com.lygttpod.android.auto.wx.page.group.WXGroupChatPage
import com.lygttpod.android.auto.wx.page.group.WXGroupInfoPage
import com.lygttpod.android.auto.wx.page.group.WXGroupManagerPage

@Suppress("EnumEntryName")
enum class WeChatNodesImpl(val version: String) :
    WXHomePage.Nodes,
    WXMinePage.Nodes,
    WXContactPage.Nodes,
    WXContactInfoPage.Nodes,
    WXChattingPage.Nodes,
    WXRemittancePage.Nodes,
    WXHBPage.Nodes,
    WXDeleteDialogPage.Nodes,
    WXCreateGroupPage.Nodes,
    WXGroupChatPage.Nodes,
    WXGroupInfoPage.Nodes,
    WXGroupManagerPage.Nodes {

    WechatVersion_8_0_42("8.0.42") { // 8.0.42版本和8.0.41版本元素没变化
        //首页
        override val homeBottomNavNode = NodeInfo("", "com.tencent.mm:id/huj", "首页底导布局")
        override val bottomNavContactsTabNode =
            NodeInfo("通讯录", "com.tencent.mm:id/h7q", "首页底导【通讯录】tab")
        override val bottomNavMineTabNode =
            NodeInfo("我", "com.tencent.mm:id/h7q", "首页底导【我】tab")
        override val homeRightTopPlusNode =
            NodeInfo("", "com.tencent.mm:id/jga", "首页右上角【加号】按钮")
        override val createGroupNode = NodeInfo(
            "发起群聊",
            "com.tencent.mm:id/obc",
            "点击首页右上角【加号】按钮后弹框中的【发起群聊】按钮"
        )


        //我的页面
        override val mineNickNameNode = NodeInfo("", "com.tencent.mm:id/kbb", "【我】页面的微信昵称")
        override val mineWxCodeNode = NodeInfo("", "com.tencent.mm:id/ouv", "【我】页面的微信号")


        //通讯录列表
        override val contactTitleNode = NodeInfo("通讯录", "android:id/text1", "通讯录标题")
        override val contactListNode = NodeInfo("", "com.tencent.mm:id/mg", "通讯录列表-RecyclerView")
        override val contactUserNode = NodeInfo("", "com.tencent.mm:id/kbq", "通讯录列表中的好友")


        //联系人信息页
        override val contactInfoSendMsgNode =
            NodeInfo("发消息", "com.tencent.mm:id/o3b", "通讯录用户信息页的【发消息】按钮")
        override val contactInfoNickNameNode =
            NodeInfo("", "com.tencent.mm:id/cf8", "通讯录用户信息页的【微信昵称】")
        override val contactInfoWxCodeNode = NodeInfo("", "com.tencent.mm:id/cff", "通讯录用户信息页的【微信号】")


        //聊天页功能区
        override val chattingBottomRootNode = NodeInfo("", "com.tencent.mm:id/k52", "聊天页底部功能区的跟节点FrameLayout")
        override val chattingBottomPlusNode = NodeInfo("", "com.tencent.mm:id/bjz", "聊天页底部的【+】按钮")
        override val chattingTransferMoneyNode = NodeInfo("转账", "com.tencent.mm:id/a12", "聊天页底部功能区的【转账】按钮")
        override val chattingSendMsgNode = NodeInfo("", "com.tencent.mm:id/bql", "聊天页底部功能区的【发送】按钮")
        override val chattingEditTextNode = NodeInfo("", "com.tencent.mm:id/bkk", "聊天页底部功能区的【输入框】EditText")


        //转账页
        override val remittanceUserNode =
            NodeInfo("", "com.tencent.mm:id/lwo", "转账页的转账对象node【转账给 测试(**名)】")
        override val remittanceWxCodeNode =
            NodeInfo("", "com.tencent.mm:id/lwp", "转账页的微信号【微信号: wxid_xxx】")
        override val remittanceInputMoneyNode =
            NodeInfo("", "com.tencent.mm:id/pbn", "转账页的输入转账金额的输入框")
        override val remittanceTransferMoneyNode =
            NodeInfo("", "com.tencent.mm:id/hql", "转账页的【转账】按钮")
        override val remittanceDialogConfirmNode = NodeInfo(
            "我知道了",
            "com.tencent.mm:id/jln",
            "转账页的转账弹框【我知道了】按钮"
        )


        //聊天页抢红包
        override val chatPageTitleNode = NodeInfo("", "com.tencent.mm:id/obn", "聊天页面用户昵称或群名字")
        override val hbPatentNode = NodeInfo("", "com.tencent.mm:id/b4t", "红包父布局-别人发的红包中【恭喜发财，大吉大利】的第一个带id的父节点LinearLayout")
        override val hbReceiveNode = NodeInfo("", "com.tencent.mm:id/a3m", "红包【已领取】")
        override val hbOpenNode = NodeInfo("", "com.tencent.mm:id/j6h", "【开】红包，ImageButton")
        override val hbBackNode = NodeInfo("", "com.tencent.mm:id/nnc", "从红包领取页【返回】")
        override val hbSenderNode = NodeInfo("", "com.tencent.mm:id/j0j", "红包详情页的xxx的红包")
        override val hbNumNode = NodeInfo("", "com.tencent.mm:id/iyw", "红包详情页中抢到的红包金额")
        override val hbMissNode = NodeInfo("看看大家的手气", "com.tencent.mm:id/j6d", "被抢完的红包弹框")
        override val hbMissCloseNode = NodeInfo("", "com.tencent.mm:id/j6f", "关闭被抢完的红包弹框")


        // TODO: 2023/10/31 未适配
        //删除聊天记录弹窗
        override val dialogContentNode = NodeInfo("删除后，将清空该聊天的消息记录", "com.tencent.mm:id/guo", "删除聊天记录弹窗中内容")
        override val dialogDeleteNode = NodeInfo("删除", "com.tencent.mm:id/guw", "删除聊天记录弹窗中【删除】按钮")


        //群聊发起页
        override val createGroupPageTitleNode = NodeInfo("发起群聊", "android:id/text1", "发起群聊页面")
        override val createGroupUserListNode =
            NodeInfo("", "com.tencent.mm:id/mim", "用户列表-RecyclerView")
        override val createGroupUserNode = NodeInfo("", "com.tencent.mm:id/kbq", "用户昵称")
        override val createGroupCompleteNode = NodeInfo("完成", "com.tencent.mm:id/g6_", "右下角的【完成】按钮")


        //群聊页
        override val groupChatPageTitleNode = NodeInfo("群聊(x)", "com.tencent.mm:id/obn", "新建的群聊页面群名字")
        override val groupChatContentListNode =
            NodeInfo("", "com.tencent.mm:id/bp0", "群聊内容列表-RecyclerView")
        override val groupChatMsgNode = NodeInfo("", "com.tencent.mm:id/bkl", "群聊消息内容")
        override val groupChatRightTopNode = NodeInfo("", "com.tencent.mm:id/fq", "群聊页右上角按钮")


        //群聊信息页
        override val groupManagerNode = NodeInfo("群管理", "", "群聊信息页的【群管理】按钮")
        override val groupDeleteNode = NodeInfo("删除", "com.tencent.mm:id/o3b", "群聊信息页的【删除】按钮")
        override val groupDeleteDialogConfirmNode = NodeInfo(
            "确定",
            "com.tencent.mm:id/obc",
            "点击群聊信息页的【删除】按钮后的弹窗的【确定】按钮"
        )


        //群管理页
        override val groupManagerDisbandNode = NodeInfo(
            "解散该群聊",
            "com.tencent.mm:id/o3b",
            "群管理页的【解散该群聊】按钮"
        )
        override val groupManagerDialogConfirmNode = NodeInfo(
            "解散",
            "com.tencent.mm:id/obc",
            "点击群管理页的【解散该群聊】按钮后的弹窗的【解散】按钮"
        )
    },
    WechatVersion_8_0_41("8.0.41") {
        //首页
        override val homeBottomNavNode = NodeInfo("", "com.tencent.mm:id/huj", "首页底导布局")
        override val bottomNavContactsTabNode =
            NodeInfo("通讯录", "com.tencent.mm:id/h7q", "首页底导【通讯录】tab")
        override val bottomNavMineTabNode =
            NodeInfo("我", "com.tencent.mm:id/h7q", "首页底导【我】tab")
        override val homeRightTopPlusNode =
            NodeInfo("", "com.tencent.mm:id/jga", "首页右上角【加号】按钮")
        override val createGroupNode = NodeInfo(
            "发起群聊",
            "com.tencent.mm:id/obc",
            "点击首页右上角【加号】按钮后弹框中的【发起群聊】按钮"
        )


        //我的页面
        override val mineNickNameNode = NodeInfo("", "com.tencent.mm:id/kbb", "【我】页面的微信昵称")
        override val mineWxCodeNode = NodeInfo("", "com.tencent.mm:id/ouv", "【我】页面的微信号")


        //通讯录列表
        override val contactTitleNode = NodeInfo("通讯录", "android:id/text1", "通讯录标题")
        override val contactListNode = NodeInfo("", "com.tencent.mm:id/mg", "通讯录列表-RecyclerView")
        override val contactUserNode = NodeInfo("", "com.tencent.mm:id/kbq", "通讯录列表中的好友")


        //联系人信息页
        override val contactInfoSendMsgNode =
            NodeInfo("发消息", "com.tencent.mm:id/o3b", "通讯录用户信息页的【发消息】按钮")
        override val contactInfoNickNameNode =
            NodeInfo("", "com.tencent.mm:id/cf8", "通讯录用户信息页的【微信昵称】")
        override val contactInfoWxCodeNode = NodeInfo("", "com.tencent.mm:id/cff", "通讯录用户信息页的【微信号】")


        //聊天页功能区
        override val chattingBottomRootNode = NodeInfo("", "com.tencent.mm:id/k52", "聊天页底部功能区的跟节点FrameLayout")
        override val chattingBottomPlusNode = NodeInfo("", "com.tencent.mm:id/bjz", "聊天页底部的【+】按钮")
        override val chattingTransferMoneyNode = NodeInfo("转账", "com.tencent.mm:id/a12", "聊天页底部功能区的【转账】按钮")
        override val chattingSendMsgNode = NodeInfo("", "com.tencent.mm:id/bql", "聊天页底部功能区的【发送】按钮")
        override val chattingEditTextNode = NodeInfo("", "com.tencent.mm:id/bkk", "聊天页底部功能区的【输入框】EditText")


        //转账页
        override val remittanceUserNode =
            NodeInfo("", "com.tencent.mm:id/lwo", "转账页的转账对象node【转账给 测试(**名)】")
        override val remittanceWxCodeNode =
            NodeInfo("", "com.tencent.mm:id/lwp", "转账页的微信号【微信号: wxid_xxx】")
        override val remittanceInputMoneyNode =
            NodeInfo("", "com.tencent.mm:id/pbn", "转账页的输入转账金额的输入框")
        override val remittanceTransferMoneyNode =
            NodeInfo("", "com.tencent.mm:id/hql", "转账页的【转账】按钮")
        override val remittanceDialogConfirmNode = NodeInfo(
            "我知道了",
            "com.tencent.mm:id/jln",
            "转账页的转账弹框【我知道了】按钮"
        )


        //聊天页抢红包
        override val chatPageTitleNode = NodeInfo("", "com.tencent.mm:id/obn", "聊天页面用户昵称或群名字")
        override val hbPatentNode = NodeInfo("", "com.tencent.mm:id/b4t", "红包父布局-别人发的红包中【恭喜发财，大吉大利】的第一个带id的父节点LinearLayout")
        override val hbReceiveNode = NodeInfo("", "com.tencent.mm:id/a3m", "红包【已领取】")
        override val hbOpenNode = NodeInfo("", "com.tencent.mm:id/j6h", "【开】红包，ImageButton")
        override val hbBackNode = NodeInfo("", "com.tencent.mm:id/nnc", "从红包领取页【返回】")
        override val hbSenderNode = NodeInfo("", "com.tencent.mm:id/j0j", "红包详情页的xxx的红包")
        override val hbNumNode = NodeInfo("", "com.tencent.mm:id/iyw", "红包详情页中抢到的红包金额")
        override val hbMissNode = NodeInfo("看看大家的手气", "com.tencent.mm:id/j6d", "被抢完的红包弹框")
        override val hbMissCloseNode = NodeInfo("", "com.tencent.mm:id/j6f", "关闭被抢完的红包弹框")


        // TODO: 2023/10/31 未适配 
        //删除聊天记录弹窗
        override val dialogContentNode = NodeInfo("删除后，将清空该聊天的消息记录", "com.tencent.mm:id/guo", "删除聊天记录弹窗中内容")
        override val dialogDeleteNode = NodeInfo("删除", "com.tencent.mm:id/guw", "删除聊天记录弹窗中【删除】按钮")


        //群聊发起页
        override val createGroupPageTitleNode = NodeInfo("发起群聊", "android:id/text1", "发起群聊页面")
        override val createGroupUserListNode =
            NodeInfo("", "com.tencent.mm:id/mim", "用户列表-RecyclerView")
        override val createGroupUserNode = NodeInfo("", "com.tencent.mm:id/kbq", "用户昵称")
        override val createGroupCompleteNode = NodeInfo("完成", "com.tencent.mm:id/g6_", "右下角的【完成】按钮")


        //群聊页
        override val groupChatPageTitleNode = NodeInfo("群聊(x)", "com.tencent.mm:id/obn", "新建的群聊页面群名字")
        override val groupChatContentListNode =
            NodeInfo("", "com.tencent.mm:id/bp0", "群聊内容列表-RecyclerView")
        override val groupChatMsgNode = NodeInfo("", "com.tencent.mm:id/bkl", "群聊消息内容")
        override val groupChatRightTopNode = NodeInfo("", "com.tencent.mm:id/fq", "群聊页右上角按钮")


        //群聊信息页
        override val groupManagerNode = NodeInfo("群管理", "", "群聊信息页的【群管理】按钮")
        override val groupDeleteNode = NodeInfo("删除", "com.tencent.mm:id/o3b", "群聊信息页的【删除】按钮")
        override val groupDeleteDialogConfirmNode = NodeInfo(
            "确定",
            "com.tencent.mm:id/obc",
            "点击群聊信息页的【删除】按钮后的弹窗的【确定】按钮"
        )


        //群管理页
        override val groupManagerDisbandNode = NodeInfo(
            "解散该群聊",
            "com.tencent.mm:id/o3b",
            "群管理页的【解散该群聊】按钮"
        )
        override val groupManagerDialogConfirmNode = NodeInfo(
            "解散",
            "com.tencent.mm:id/obc",
            "点击群管理页的【解散该群聊】按钮后的弹窗的【解散】按钮"
        )
    },
    WechatVersion_8_0_40("8.0.40") {
        //首页
        override val homeBottomNavNode = NodeInfo("", "com.tencent.mm:id/fj3", "首页底导布局")
        override val bottomNavContactsTabNode =
            NodeInfo("通讯录", "com.tencent.mm:id/f2s", "首页底导【通讯录】tab")
        override val bottomNavMineTabNode =
            NodeInfo("我", "com.tencent.mm:id/f2s", "首页底导【我】tab")
        override val homeRightTopPlusNode =
            NodeInfo("", "com.tencent.mm:id/grs", "首页右上角【加号】按钮")
        override val createGroupNode = NodeInfo(
            "发起群聊",
            "com.tencent.mm:id/knx",
            "点击首页右上角【加号】按钮后弹框中的【发起群聊】按钮"
        )


        //我的页面
        override val mineNickNameNode = NodeInfo("", "com.tencent.mm:id/hfq", "【我】页面的微信昵称")
        override val mineWxCodeNode = NodeInfo("", "com.tencent.mm:id/l29", "【我】页面的微信号")


        //通讯录列表
        override val contactTitleNode = NodeInfo("通讯录", "android:id/text1", "通讯录标题")
        override val contactListNode = NodeInfo("", "com.tencent.mm:id/js", "通讯录列表-RecyclerView")
        override val contactUserNode = NodeInfo("", "com.tencent.mm:id/hg4", "通讯录列表中的好友")


        //联系人信息页
        override val contactInfoSendMsgNode =
            NodeInfo("发消息", "com.tencent.mm:id/khj", "通讯录用户信息页的【发消息】按钮")
        override val contactInfoNickNameNode =
            NodeInfo("", "com.tencent.mm:id/bq1", "通讯录用户信息页的【微信昵称】")
        override val contactInfoWxCodeNode = NodeInfo("", "com.tencent.mm:id/bq9", "通讯录用户信息页的【微信号】")


        //聊天页功能区
        override val chattingBottomRootNode = NodeInfo("", "com.tencent.mm:id/k52", "聊天页底部功能区的跟节点FrameLayout")
        override val chattingBottomPlusNode = NodeInfo("", "com.tencent.mm:id/b3q", "聊天页底部的【+】按钮")
        override val chattingTransferMoneyNode = NodeInfo("转账", "com.tencent.mm:id/vg", "聊天页底部功能区的【转账】按钮")
        override val chattingSendMsgNode = NodeInfo("", "com.tencent.mm:id/bql", "聊天页底部功能区的【发送】按钮")
        override val chattingEditTextNode = NodeInfo("", "com.tencent.mm:id/bkk", "聊天页底部功能区的【输入框】EditText")


        //转账页
        override val remittanceUserNode =
            NodeInfo("", "com.tencent.mm:id/inh", "转账页的转账对象node【转账给 测试(**名)】")
        override val remittanceWxCodeNode =
            NodeInfo("", "com.tencent.mm:id/ini", "转账页的微信号【微信号: wxid_xxx】")
        override val remittanceInputMoneyNode =
            NodeInfo("", "com.tencent.mm:id/lg_", "转账页的输入转账金额的输入框")
        override val remittanceTransferMoneyNode =
            NodeInfo("", "com.tencent.mm:id/ffp", "转账页的【转账】按钮")
        override val remittanceDialogConfirmNode = NodeInfo(
            "我知道了",
            "com.tencent.mm:id/guw",
            "转账页的转账弹框【我知道了】按钮"
        )


        //聊天页抢红包
        override val chatPageTitleNode = NodeInfo("", "com.tencent.mm:id/ko4", "聊天页面用户昵称或群名字")
        override val hbPatentNode = NodeInfo("", "com.tencent.mm:id/ape", "红包父布局-别人发的红包中【恭喜发财，大吉大利】的第一个带id的父节点LinearLayout")
        override val hbReceiveNode = NodeInfo("", "com.tencent.mm:id/xs", "红包【已领取】")
        override val hbOpenNode = NodeInfo("", "com.tencent.mm:id/gir", "【开】红包，ImageButton")
        override val hbBackNode = NodeInfo("", "com.tencent.mm:id/k6i", "从红包领取页【返回】")
        override val hbSenderNode = NodeInfo("", "com.tencent.mm:id/ge3", "红包详情页的xxx的红包")
        override val hbNumNode = NodeInfo("", "com.tencent.mm:id/gcj", "红包详情页中抢到的红包金额")
        override val hbMissNode = NodeInfo("看看大家的手气", "com.tencent.mm:id/gin", "被抢完的红包弹框")
        override val hbMissCloseNode = NodeInfo("", "com.tencent.mm:id/gip", "关闭被抢完的红包弹框")


        //删除聊天记录弹窗
        override val dialogContentNode = NodeInfo("删除后，将清空该聊天的消息记录", "com.tencent.mm:id/guo", "删除聊天记录弹窗中内容")
        override val dialogDeleteNode = NodeInfo("删除", "com.tencent.mm:id/guw", "删除聊天记录弹窗中【删除】按钮")


        //群聊发起页
        override val createGroupPageTitleNode = NodeInfo("发起群聊", "android:id/text1", "发起群聊页面")
        override val createGroupUserListNode =
            NodeInfo("", "com.tencent.mm:id/j9o", "用户列表-RecyclerView")
        override val createGroupUserNode = NodeInfo("", "com.tencent.mm:id/hg4", "用户昵称")
        override val createGroupCompleteNode = NodeInfo("完成", "com.tencent.mm:id/e9s", "右下角的【完成】按钮")


        //群聊页
        override val groupChatPageTitleNode = NodeInfo("群聊(x)", "com.tencent.mm:id/ko4", "新建的群聊页面群名字")
        override val groupChatContentListNode =
            NodeInfo("", "com.tencent.mm:id/b79", "群聊内容列表-RecyclerView")
        override val groupChatMsgNode = NodeInfo("", "com.tencent.mm:id/b4b", "群聊消息内容")
        override val groupChatRightTopNode = NodeInfo("", "com.tencent.mm:id/eo", "群聊页右上角按钮")


        //群聊信息页
        override val groupManagerNode = NodeInfo("群管理", "", "群聊信息页的【群管理】按钮")
        override val groupDeleteNode = NodeInfo("删除", "com.tencent.mm:id/khj", "群聊信息页的【删除】按钮")
        override val groupDeleteDialogConfirmNode = NodeInfo(
            "确定",
            "com.tencent.mm:id/knx",
            "点击群聊信息页的【删除】按钮后的弹窗的【确定】按钮"
        )


        //群管理页
        override val groupManagerDisbandNode = NodeInfo(
            "解散该群聊",
            "com.tencent.mm:id/khj",
            "群管理页的【解散该群聊】按钮"
        )
        override val groupManagerDialogConfirmNode = NodeInfo(
            "解散",
            "com.tencent.mm:id/knx",
            "点击群管理页的【解散该群聊】按钮后的弹窗的【解散】按钮"
        )
    },
}