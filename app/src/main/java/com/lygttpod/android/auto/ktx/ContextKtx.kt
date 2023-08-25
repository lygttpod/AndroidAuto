package com.lygttpod.android.auto.ktx

import android.content.Context

fun Context.dp2px(dipValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}