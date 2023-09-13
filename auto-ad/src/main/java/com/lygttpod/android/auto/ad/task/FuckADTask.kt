package com.lygttpod.android.auto.ad.task

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.MutableLiveData
import com.android.accessibility.ext.acc.clickByCustomRule
import com.android.accessibility.ext.acc.inListView
import com.android.accessibility.ext.acc.isTextView
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.accessibility.ext.toast
import com.lygttpod.android.auto.ad.AppContext
import com.lygttpod.android.auto.ad.accessibility.FuckADAccessibility
import com.lygttpod.android.auto.ad.data.AdApp
import com.lygttpod.android.auto.ad.data.FilterKeywordData
import com.lygttpod.android.auto.ad.data.FuckAd
import com.lygttpod.android.auto.ad.data.FuckAdApps
import com.lygttpod.android.auto.ad.ktx.queryAllInstallApp
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext


object FuckADTask {

    private const val APP_CONFIG = "appConfig"
    private const val FILTER_KEYWORD = "filterKeyword"

    private val mutex = Mutex()

    private val fuckADTaskScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var fuckAdApps: FuckAdApps? = null
        set(value) {
            field = value
            fuckAdAppsLiveData.postValue(value)
        }

    var fuckAdAppsLiveData = MutableLiveData<FuckAdApps?>()

    private var filterKeywordData: FilterKeywordData? = null

    fun updateKeywordList(keyword: String) {
        fuckADTaskScope.launch {
            getKeywordList()?.let {
                if (it.list.contains(keyword)) {
                    withContext(Dispatchers.Main) {
                        AppContext.toast("已经添加过啦，不要重复添加哦")
                    }
                    return@launch
                } else {
                    it.list.add(keyword)
                    MMKV.defaultMMKV().encode(FILTER_KEYWORD, it)
                    withContext(Dispatchers.Main) {
                        AppContext.toast("添加成功")
                    }
                }
            }
        }
    }

    fun getKeywordList(): FilterKeywordData? {
        filterKeywordData =
            MMKV.defaultMMKV().decodeParcelable(FILTER_KEYWORD, FilterKeywordData::class.java)
                ?: FilterKeywordData(list = mutableListOf("关闭闹钟", "关闭提醒"))
        return filterKeywordData
    }

    fun updateData(ad: AdApp) {
        fuckAdApps?.fuckAd?.apps?.find { it.appName == ad.packageName }
            ?.apply { adNode = ad.adNode }
        save2File()
    }

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
                fuckAdApps =
                    MMKV.defaultMMKV().decodeParcelable(APP_CONFIG, FuckAdApps::class.java, null)
            }
            loadAdConfig()?.let {
                if (fuckAdApps == null) {
                    fuckAdApps = it
                } else {
                    it.fuckAd.apps.forEach { newData ->
                        val oldData =
                            fuckAdApps!!.fuckAd.apps.find { it.packageName == newData.packageName }
                        if (oldData != null) {
                            newData.adNode = oldData.adNode
                        }
                    }
                    fuckAdApps = it
                }
                save2File()
            }
        }
    }

    private fun save2File() {
        fuckADTaskScope.launch {
            MMKV.defaultMMKV().remove(APP_CONFIG)
            MMKV.defaultMMKV().encode(APP_CONFIG, fuckAdApps)
        }
    }

    fun fuckAD(event: AccessibilityEvent) {
        if (mutex.isLocked) return
        val packageName = event.packageName.default()
        fuckAdApps?.fuckAd?.apps?.find { it.packageName == packageName }?.let {
            Log.d("FuckADTask", "打开了【${it.appName}】的【${it.launcher}】")
            if (it.isSkipped()) return@let
            fuckADTaskScope.launch {
                if (skipAd(it)) {
                    it.skipSuccess()
                }
            }
        }
    }

    private suspend fun skipAd(adApp: AdApp): Boolean {
        return mutex.withLock {
            val acc = FuckADAccessibility.fuckADAccessibility ?: return false
            val actions = adApp.adNode.action.split(",")
            val id = adApp.adNode.id
            retryCheckTaskWithLog(
                "查找【${adApp.appName}】的【$actions】节点",
                timeOutMillis = 5000
            ) {
                val skipResult = acc.clickByCustomRule {
                    if (id.isNotBlank()) {
                        it.viewIdResourceName == id
                    } else {
                        if (it.isTextView()) {
                            val text = it.text.default()
                            //通过多重判断大大减少误触的概率
                            filterKeywordData?.list?.find { it == text } == null
                                    && text.length <= adApp.actionMaxLength()
                                    && actions.find { action -> text.contains(action) } != null
                                    && it.inListView().not()
                        } else {
                            false
                        }
                    }
                }
                if (skipResult) {
                    withContext(Dispatchers.Main) {
                        Log.d("FuckADTask", "自动跳过【${adApp.appName}】的广告啦")
                    }
                }
                skipResult
            }
        }
    }
}