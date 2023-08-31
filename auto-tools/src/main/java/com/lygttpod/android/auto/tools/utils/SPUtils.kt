package com.lygttpod.android.auto.tools.utils

import android.content.Context
import android.content.SharedPreferences

object SPUtils {
    private fun getSharedPreference(context: Context, fileName: String): SharedPreferences {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    fun getEditor(context: Context, fileName: String): SharedPreferences.Editor {
        return getSharedPreference(context, fileName).edit()
    }

    fun saveValue(context: Context, filename: String, key: String, value: Any?) {
        when (value) {
            is Int -> saveInt(context, filename, key, value)
            is Long -> saveLong(context, filename, key, value)
            is Float -> saveFloat(context, filename, key, value)
            is Boolean -> saveBoolean(context, filename, key, value)
            else -> saveString(context, filename, key, value?.toString() ?: "")
        }
    }

    fun saveBoolean(context: Context, filename: String, key: String, value: Boolean) {
        getEditor(context, filename).putBoolean(key, value).commit()
    }

    fun getBoolean(context: Context, filename: String, key: String): Boolean {
        return getSharedPreference(context, filename).getBoolean(key, false)
    }

    fun getBoolean(
        context: Context,
        filename: String,
        key: String,
        defaultValue: Boolean
    ): Boolean {
        return getSharedPreference(context, filename).getBoolean(key, defaultValue)
    }

    fun saveLong(context: Context, filename: String, key: String, value: Long) {
        getEditor(context, filename).putLong(key, value).commit()
    }

    fun saveFloat(context: Context, filename: String, key: String, value: Float) {
        getEditor(context, filename).putFloat(key, value).commit()
    }

    fun getLong(context: Context, filename: String, key: String): Long {
        return getSharedPreference(context, filename).getLong(key, 0)
    }

    fun getLong(context: Context, filename: String, key: String, defaultValue: Long): Long {
        return getSharedPreference(context, filename).getLong(key, defaultValue)
    }

    fun saveInt(context: Context, filename: String, key: String, value: Int) {
        getEditor(context, filename).putInt(key, value).commit()
    }

    fun getInt(context: Context, filename: String, key: String): Int {
        return getSharedPreference(context, filename).getInt(key, 0)
    }

    fun getInt(context: Context, filename: String, key: String, defaultValue: Int): Int {
        return getSharedPreference(context, filename).getInt(key, defaultValue)
    }

    fun saveString(context: Context, filename: String, key: String, value: String) {
        getEditor(context, filename).putString(key, value).commit()
    }

    fun getString(context: Context, filename: String, key: String): String? {
        return getSharedPreference(context, filename).getString(key, "")
    }

    fun getString(context: Context, filename: String, key: String, defaultValue: String): String? {
        return getSharedPreference(context, filename).getString(key, defaultValue)
    }
}