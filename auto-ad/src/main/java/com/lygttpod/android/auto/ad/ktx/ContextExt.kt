package com.lygttpod.android.auto.ad.ktx

import android.content.Context

fun Context.loadAsset(fileName: String): String? {
    try {
        return assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}
