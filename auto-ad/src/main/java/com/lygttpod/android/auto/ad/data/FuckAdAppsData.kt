package com.lygttpod.android.auto.ad.data

data class FuckAdApps(val fuckAd: FuckAd)

data class FuckAd(val apps: List<AdApp>)

data class AdApp(
    val appName: String,
    val packageName: String,
    val launcher: String,
    val adNodes: List<ADNode>
)

data class ADNode(
    val action: String
)
