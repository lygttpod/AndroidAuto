package com.android.wechat.tools.service

import android.accessibilityservice.AccessibilityService
import android.app.Application
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.MutableLiveData
import com.android.accessibility.ext.AsyncAccessibilityService
import com.android.accessibility.ext.default

val wxAccessibilityServiceLiveData = MutableLiveData<AccessibilityService?>(null)
val wxAccessibilityService: AccessibilityService? get() = wxAccessibilityServiceLiveData.value

val wxAppLiveData = MutableLiveData(false)

class WXAccessibility : AsyncAccessibilityService() {

    companion object {
        var isInWXApp = false
    }

    private var currentEventPackage: String = ""

    override fun targetPackageName() = "com.tencent.mm"

    override fun onCreate() {
        super.onCreate()
        wxAccessibilityServiceLiveData.value = this
    }

    override fun onDestroy() {
        wxAccessibilityServiceLiveData.value = null
        wxAppLiveData.value = false
        super.onDestroy()
    }

    override fun asyncHandleAccessibilityEvent(event: AccessibilityEvent) {
        val temp = event.packageName.default()
        Log.d("WXAccessibility", "==========$event")
        // TODO: 这种方式判断的太准确，某些场景下状态是不对的，只能作为临时方案，待后续找到更好的方案
        if (currentEventPackage == targetPackageName() && temp != targetPackageName()) {
            //说明是从微信退出了，终止自动化脚本
            Log.d("WXAccessibility", "==========检测到当前不在微信里边===========")
            isInWXApp = false
        } else if (currentEventPackage != targetPackageName() && temp == targetPackageName() && currentEventPackage != temp) {
            //说明打开了微信
            Log.d("WXAccessibility", "==========检测到当前打开微信了===========")
            isInWXApp = true
        }
        currentEventPackage = temp

        wxAppLiveData.postValue(targetPackageName() == event.packageName)
    }
}