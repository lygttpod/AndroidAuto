package com.lygttpod.android.auto

import android.app.Application
import com.pgyersdk.crash.PgyCrashManager

class App : Application() {

    companion object {
        private var instance: Application? = null
        fun instance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        PgyCrashManager.register()
    }
}