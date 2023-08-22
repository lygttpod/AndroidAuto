package com.lygttpod.android.auto.wx.em

enum class FriendStatus(val status: String) {
    BLACK("被拉黑"),
    DELETE("被删除"),
    NORMAL("正常"),
    ACCOUNT_EXCEPTION("帐号异常"),
    UNKNOW("待检测"),
}