package com.lygttpod.android.auto.ktx
import android.util.Log
import com.lygttpod.android.auto.BuildConfig

object LogUtils {
    private const val TAG = "AndroidAuto"
    fun log(log: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, log)
        }
    }
}