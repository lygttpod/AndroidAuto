package com.lygttpod.android.auto.wx.data

import com.lygttpod.android.auto.wx.em.FriendStatus

data class WxUserInfo(
    val nickName: String,
    val wxCode: String? = null,
    var status: FriendStatus = FriendStatus.UNKNOW
) {
    override fun toString(): String {
        return "nickName: $nickName → wxCode: $wxCode → status: ${status.status}"
    }
}
