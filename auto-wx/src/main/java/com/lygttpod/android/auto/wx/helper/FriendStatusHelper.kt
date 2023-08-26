package com.lygttpod.android.auto.wx.helper

import android.util.Log
import com.lygttpod.android.auto.wx.data.WxUserInfo
import com.lygttpod.android.auto.wx.em.FriendStatus

object FriendStatusHelper {

    interface TaskCallBack {
        fun onTaskStart(taskName: String)
        fun onTaskEnd(totalTime: Long)
    }

    var taskCallBack: TaskCallBack? = null

    //好友状态检测结果
    private val userResultList = mutableListOf<WxUserInfo>()


    //上次检测到的好友，【假转账方法中「续捡」要用】
    var lastCheckUser: WxUserInfo? = null

    fun reset() {
        lastCheckUser = null
        clearFriendsStatusList()
    }

    fun addCheckResult(data: WxUserInfo) {
        Log.d("检查结果", "$data")
        val find = userResultList.indexOfFirst { it.nickName == data.nickName }
        if (find == -1) {
            userResultList.add(data)
        } else {
            userResultList[find] = data
        }
    }

    fun addCheckResults(list: MutableList<WxUserInfo>?) {
        list ?: return
        Log.d("检查结果", "${list.map { it.toString() + "\n" }}")
        userResultList.addAll(list)
    }

    fun addFriends(list: List<String>) {
        userResultList.clear()
        userResultList.addAll(list.map { WxUserInfo(it) })
    }

    fun clearFriendsStatusList() = userResultList.clear()

    fun getUserResultList() = userResultList

    fun filterNotNormalData(): MutableList<WxUserInfo> {
        return userResultList.filterNot { it.status == FriendStatus.NORMAL || it.status == FriendStatus.UNKNOW }
            .toMutableList()
    }

    fun filterUnCheckData(): MutableList<WxUserInfo> {
        return userResultList.filter { it.status == FriendStatus.UNKNOW }
            .toMutableList()
    }

    fun filterAllData(): MutableList<WxUserInfo> {
        return userResultList
    }

}