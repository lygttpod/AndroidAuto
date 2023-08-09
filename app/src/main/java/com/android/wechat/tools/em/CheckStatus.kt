package com.android.wechat.tools.em

enum class CheckStatus(val status: String, val mark: String) {
    BLACK("被拉黑", "请确认你和他（她）的好友关系是否正常"),
    DELETE("被删除", "你不是收款方好友，对方添加你为好友后才能发起转账"),
    ACCOUNT_EXCEPTION("帐号异常", "对方微信号已被限制登录，为保障你的资金安全，暂时无法完成交易"),
    NORMAL("正常", "付款方式"),
    NO_AUTH("自己未实名认证", "实名认证"),
}

fun CheckStatus?.toFriendStatus(): FriendStatus {
    return when (this) {
        CheckStatus.BLACK -> FriendStatus.BLACK
        CheckStatus.DELETE -> FriendStatus.DELETE
        CheckStatus.ACCOUNT_EXCEPTION -> FriendStatus.ACCOUNT_EXCEPTION
        CheckStatus.NORMAL -> FriendStatus.NORMAL
        CheckStatus.NO_AUTH -> FriendStatus.NORMAL
        else -> FriendStatus.UNKNOW
    }
}