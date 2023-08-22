package com.lygttpod.android.auto.wx.helper

import android.util.Log
import com.lygttpod.android.auto.wx.data.WxUserInfo

object FriendStatusHelper {

    interface TaskCallBack {
        fun onGetAllFriend(list: List<String>)
        fun onFriendChecked(wxUserInfo: WxUserInfo)
        fun onFriendChecked(list: MutableList<WxUserInfo>)
        fun onTaskStart()
        fun onTaskEnd(totalTime: Long)
    }

    var taskCallBack: TaskCallBack? = null

    private val checkResultList = mutableListOf<WxUserInfo>()
    private val friendList = mutableListOf<String>()

    fun init() {
        checkResultList.clear()
        friendList.clear()
    }

    fun addCheckResult(data: WxUserInfo) {
        Log.d("检查结果", "$data")
        checkResultList.add(data)
        taskCallBack?.onFriendChecked(data)
    }

    fun addCheckResults(list: MutableList<WxUserInfo>?) {
        list ?: return
        Log.d("检查结果", "${list.map { it.toString() +"\n" }}")
        checkResultList.addAll(list)
        taskCallBack?.onFriendChecked(list)
    }

    fun getResult() = checkResultList

    fun addFriends(list: List<String>) {
        friendList.clear()
        friendList.addAll(list)
        taskCallBack?.onGetAllFriend(list)
    }

    fun clearFriends() = friendList.clear()

    fun clearFriendsStatusList() = checkResultList.clear()

    fun getFriendList() = friendList
}