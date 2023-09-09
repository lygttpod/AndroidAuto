package com.lygttpod.android.auto.ad.ktx

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


private val gson = Gson()

fun <T> fromJson(json: String): T {
    return gson.fromJson(json, object : TypeToken<T>() {}.type)
}

fun <T> String?.toObject(): T? {
    val json = this ?: return null
    return fromJson<T>(json)
}