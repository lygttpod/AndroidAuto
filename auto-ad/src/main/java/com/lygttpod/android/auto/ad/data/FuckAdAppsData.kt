package com.lygttpod.android.auto.ad.data

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize

@Parcelize
data class FuckAdApps(val fuckAd: FuckAd) : Parcelable

@Parcelize
data class FuckAd(val apps: MutableList<AdApp>) : Parcelable

@Parcelize
data class AdApp(
    val appName: String,
    val packageName: String,
    val launcher: String,
    var lastSkipSuccessTime: Long = 0,
    var adNode: ADNode = ADNode()
) : Parcelable {
    fun skipSuccess() {
        lastSkipSuccessTime = System.currentTimeMillis()
    }

    /**
     * 五秒内成功跳过再次启动就不处理了，目的是为了减少检测次数
     */
    fun isSkipped(intervalTime: Long = 5_000): Boolean {
        val skipped = System.currentTimeMillis() - lastSkipSuccessTime <= intervalTime
        if (skipped) {
            Log.d(
                "FuckADTask",
                "isSkipped: ${intervalTime / 1000}秒内已经成功跳过一次广告，无需重复检测"
            )
        }
        return skipped
    }

    fun actionMaxLength(): Int = adNode.actionMaxLength

    fun getNodeId(): String = adNode.id
    fun getMaxLength(): Int = adNode.actionMaxLength
}

@Parcelize
data class ADNode(
    var action: String = "跳过,关闭",
    var actionMaxLength: Int = 5,
    var id: String = ""
) : Parcelable
