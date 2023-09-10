package com.lygttpod.android.auto.ad.data

import android.util.Log

data class FuckAdApps(val fuckAd: FuckAd)

data class FuckAd(val apps: List<AdApp>)

data class AdApp(
    val appName: String,
    val packageName: String,
    val launcher: String,
    var lastSkipSuccessTime: Long = 0,
    val adNodes: List<ADNode> = listOf(ADNode(action = "跳过"), ADNode(action = "关闭"))
) {
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
}

data class ADNode(
    val action: String
)
