package com.lygttpod.android.auto.ad.task

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.accessibility.ext.toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lygttpod.android.auto.ad.AppContext
import com.lygttpod.android.auto.ad.accessibility.FuckADAccessibility
import com.lygttpod.android.auto.ad.data.AdApp
import com.lygttpod.android.auto.ad.data.FuckAdApps
import com.lygttpod.android.auto.ad.ktx.loadAsset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext


object FuckADTask {

    private val mutex = Mutex()

    private val fuckADTaskScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var fuckAdApps: FuckAdApps? = null
    private fun loadAdConfig(): FuckAdApps? {
        return try {
            val json = AppContext.loadAsset("fuck_ad_app_config.json")
            if (json.isNullOrEmpty()) {
                null
            } else {
                val data: FuckAdApps =
                    Gson().fromJson(json, object : TypeToken<FuckAdApps>() {}.type)
                data
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun analysisAppConfig() {
        fuckADTaskScope.launch {
            fuckAdApps = loadAdConfig()
            Log.d("FuckADTask", "analysisAppConfig: ${fuckAdApps?.toString()}")
        }
    }

    fun fuckAD(event: AccessibilityEvent) {
        fuckADTaskScope.launch {
            val packageName = event.packageName.default()
            val className = event.className.default()
            val adApp =
                fuckAdApps?.fuckAd?.apps?.find { it.packageName == packageName && it.launcher == className }
            adApp?.let {
                Log.d("FuckADTask", "打开了【${it.appName}】的【${it.launcher}】")
                if (mutex.isLocked) return@let
                skipAd(it)
            }
        }
    }

    private suspend fun skipAd(adApp: AdApp) {
        mutex.withLock {
            val acc = FuckADAccessibility.fuckADAccessibility ?: return@withLock
            val actions = adApp.adNodes.map { it.action }.filter { it.isNotBlank() }
            retryCheckTaskWithLog(
                "查找【${adApp.appName}】的【$actions】节点",
                timeOutMillis = 5000
            ) {
                val skipResult = actions.find { acc.clickByText(it) } != null
                if (skipResult) {
                    withContext(Dispatchers.Main) {
                        AppContext.toast("自动跳过广告啦")
                        Log.d("FuckADTask", "自动跳过广告啦")
                    }
                }
                skipResult
            }
        }
    }
}