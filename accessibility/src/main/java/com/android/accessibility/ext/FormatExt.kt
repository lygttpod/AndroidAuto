package com.android.accessibility.ext

fun String?.default(default: String = "") = if (this.isNullOrBlank()) default else this

fun CharSequence?.default(default: String = "") =
    if (this.isNullOrBlank()) default else this.toString()