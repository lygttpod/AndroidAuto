package com.lygttpod.android.auto.ad

import android.content.Context
import com.tencent.mmkv.MMKV


val AppContext = FuckAdManager.appContext ?: error("请先在application中调用FuckAdManager.init(this)初始化")

object FuckAdManager {

    var appContext: Context? = null
    fun init(context: Context) {
        this.appContext = context
        MMKV.initialize(context)
    }
}