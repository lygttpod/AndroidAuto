package com.lygttpod.android.auto.ad.ktx

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.util.Log
import com.lygttpod.android.auto.ad.AppContext
import com.lygttpod.android.auto.ad.data.AdApp


fun Context.loadAsset(fileName: String): String? {
    try {
        return assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

@Suppress("DEPRECATION")
fun Context.queryAllInstallApp(): MutableList<AdApp> {
    val packageManager = this.packageManager
    // 创建一个 Intent，用于查询所有启动的应用程序
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    // 使用 queryIntentActivities 获取所有匹配的应用列表
    val appInfoList =
        packageManager.queryIntentActivities(intent, 0)
            .filterNot { isSystemApp(it) || isSelf(it) }
            .map {
                val appName = it.loadLabel(packageManager).toString()
                val packageName = it.activityInfo.packageName
                val className = it.activityInfo.name
                AdApp(appName, packageName, className)
            }
            .toMutableList()

    return appInfoList
}

// 检查应用是否为系统应用
private fun isSystemApp(resolveInfo: ResolveInfo): Boolean {
    val regex = "com\\.android\\.[^.]+" // 匹配系统应用 com.android. 后面跟着任意不包含 . 的字符
    val result = resolveInfo.activityInfo.packageName.matches(Regex(regex))
    Log.d("isSystemApp", "isSystemApp: ${resolveInfo.activityInfo.packageName} : $result")
    return result
}

private fun isSelf(resolveInfo: ResolveInfo): Boolean {
    return resolveInfo.activityInfo.packageName == AppContext.packageName
}
