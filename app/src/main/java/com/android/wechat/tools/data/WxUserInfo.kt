package com.android.wechat.tools.data

import com.android.wechat.tools.em.FriendStatus

data class WxUserInfo(
    val nickName: String,
    val wxCode: String? = null,
    var status: FriendStatus = FriendStatus.UNKNOW
) {
    override fun toString(): String {
        return "nickName: $nickName → wxCode: $wxCode → status: ${status.status}"
    }
}
