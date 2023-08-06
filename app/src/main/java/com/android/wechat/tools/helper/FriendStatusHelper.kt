package com.android.wechat.tools.helper

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.wechat.tools.data.WxUserInfo

object FriendStatusHelper {

    interface TaskCallBack {
        fun onFriendChecked(wxUserInfo: WxUserInfo)
    }

    var taskCallBack: TaskCallBack? = null

    var friendListResult = MutableLiveData<List<String>>()
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

    fun getResult() = checkResultList

    fun addFriends(list: List<String>) {
        friendList.clear()
        friendList.addAll(list)
        friendListResult.postValue(list)
    }

    fun clearFriends() = friendList.clear()

    fun clearFriendsStatusList() = checkResultList.clear()

    fun getFriendList() = friendList
}