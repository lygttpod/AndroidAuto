package com.lygttpod.android.auto.tools

import android.content.Context


val AppContext = AutoTools.appContext ?: error("请先在application中调用AutoTools.init(this)初始化")

object AutoTools {

    var appContext: Context? = null
    fun init(context: Context) {
        this.appContext = context
    }
}