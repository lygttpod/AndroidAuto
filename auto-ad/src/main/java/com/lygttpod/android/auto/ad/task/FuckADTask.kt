package com.lygttpod.android.auto.ad.task

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.MutableLiveData
import com.android.accessibility.ext.acc.clickByText
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.accessibility.ext.toast
import com.lygttpod.android.auto.ad.AppContext
import com.lygttpod.android.auto.ad.accessibility.FuckADAccessibility
import com.lygttpod.android.auto.ad.data.AdApp
import com.lygttpod.android.auto.ad.data.FuckAd
import com.lygttpod.android.auto.ad.data.FuckAdApps
import com.lygttpod.android.auto.ad.ktx.queryAllInstallApp
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
        set(value) {
            field = value
            fuckAdAppsLiveData.postValue(value)
        }

    var fuckAdAppsLiveData = MutableLiveData<FuckAdApps?>()
    private fun loadAdConfig(): FuckAdApps? {
        return try {
            FuckAdApps(fuckAd = FuckAd(AppContext.queryAllInstallApp()))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun analysisAppConfig() {
        fuckADTaskScope.launch {
            if (fuckAdApps == null) {
                fuckAdApps = loadAdConfig()
                Log.d("FuckADTask", "analysisAppConfig: ${fuckAdApps?.toString()}")
            }
        }
    }

    fun fuckAD(event: AccessibilityEvent) {
        if (mutex.isLocked) return
        val packageName = event.packageName.default()
        fuckAdApps?.fuckAd?.apps?.find { it.packageName == packageName }?.let {
            Log.d("FuckADTask", "打开了【${it.appName}】的【${it.launcher}】")
            if (it.isSkipped()) return@let
            fuckADTaskScope.launch {
                if (skipAd(it)) { it.skipSuccess() }
            }
        }
    }

    private suspend fun skipAd(adApp: AdApp): Boolean {
        return mutex.withLock {
            val acc = FuckADAccessibility.fuckADAccessibility ?: return false
            val actions = adApp.adNodes.map { it.action }.filter { it.isNotBlank() }
            retryCheckTaskWithLog(
                "查找【${adApp.appName}】的【$actions】节点",
                timeOutMillis = 5000
            ) {
                val skipResult = actions.find { acc.clickByText(it) } != null
                if (skipResult) {
                    withContext(Dispatchers.Main) {
                        AppContext.toast("自动跳过广告啦")
                        Log.d("FuckADTask", "自动跳过【${adApp.appName}】的广告啦")
                    }
                }
                skipResult
            }
        }
    }
}