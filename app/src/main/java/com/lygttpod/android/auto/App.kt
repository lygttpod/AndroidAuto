package com.lygttpod.android.auto

import android.app.Application
import com.lygttpod.android.auto.tools.AutoTools

class App : Application() {

    companion object {
        private var instance: Application? = null
        fun instance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        AutoTools.init(this)
    }
}