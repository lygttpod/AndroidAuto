package com.android.accessibility.ext

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast

/**
 * 打开系统设置中辅助功能
 */
fun Context.openAccessibilitySetting() {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

fun Context.isAccessibilityOpened(serviceClass: Class<out AccessibilityService>): Boolean {
    val serviceName: String = this.applicationContext.packageName + "/" + serviceClass.canonicalName
    var accessibilityEnabled = 0
    try {
        accessibilityEnabled =
            Settings.Secure.getInt(this.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
    } catch (e: Settings.SettingNotFoundException) {
        e.printStackTrace()
    }
    val ms = TextUtils.SimpleStringSplitter(':')
    if (accessibilityEnabled == 1) {
        val settingValue = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (settingValue != null) {
            ms.setString(settingValue)
            while (ms.hasNext()) {
                val accessibilityService = ms.next()
                if (accessibilityService.equals(serviceName, ignoreCase = true)) {
                    return true
                }
            }
        }
    }
    return false
}

/**
 * 打开个人微信
 */
fun Context.goToWx() = Intent(Intent.ACTION_MAIN)
    .apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        component = ComponentName(
            "com.tencent.mm",
            "com.tencent.mm.ui.LauncherUI",
        )
    }
    .apply(::startActivity)

fun Context.toast(msg: String) {
    runOnUiThread {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

fun runOnUiThread(action: Runnable) {
    val uiThread = Looper.getMainLooper().thread
    val handler = Handler(Looper.getMainLooper())
    if (Thread.currentThread() !== uiThread) {
        handler.post(action)
    } else {
        action.run()
    }
}