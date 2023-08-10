package com.android.wechat.tools.ktx

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.android.wechat.tools.helper.FloatManager

fun Context?.gotoOverlayPermission() {
    this ?: return
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    intent.data = Uri.parse("package:" + this.packageName)
    this.startActivity(intent)
}

fun Context?.canDrawOverlays(): Boolean {
    this ?: return false
    return Settings.canDrawOverlays(this)
}

fun Context?.showPrintFloat() {
    this ?: return
    if (canDrawOverlays()) {
        FloatManager.showPrintFloat()
    } else {
        gotoOverlayPermission()
    }
}

fun Context?.showTaskProgressFloat(progress: String? = null) {
    this ?: return
    if (canDrawOverlays()) {
        FloatManager.showTaskProgressFloat(progress)
    } else {
        gotoOverlayPermission()
    }
}

