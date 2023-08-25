package com.lygttpod.android.auto.update

import android.content.Context
import android.widget.Toast
import com.lygttpod.android.auto.ktx.LogUtils
import com.pgyersdk.update.DownloadFileListener
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import com.pgyersdk.update.javabean.AppBean
import java.io.File

object UpdateApp {
    fun checkVersion(context: Context, downloadClick: ((String) -> Unit)?) {
        PgyUpdateManager
            .Builder()
            .setUserCanRetry(true)
            .setDeleteHistroyApk(false)
            .setUpdateManagerListener(object : UpdateManagerListener {
                override fun onNoUpdateAvailable() {
                    LogUtils.log("there is no new version")
                }

                override fun onUpdateAvailable(appBean: AppBean) {
                    LogUtils.log("there is new version can update, new versionCode is " + appBean.versionCode)
                    VersionTipDialog.showDialog(context, appBean, downloadClick)
                }

                override fun checkUpdateFailed(e: Exception?) {
                    LogUtils.log("check update failed : $e")
                }

            })
            .setDownloadFileListener(object : DownloadFileListener {
                override fun downloadFailed() {
                    LogUtils.log("download apk failed")
                    Toast.makeText(context, "下载失败，稍后再试", Toast.LENGTH_SHORT).show()
                }

                override fun downloadSuccessful(file: File) {
                    LogUtils.log("download apk success: file = ${file.path}")
                    PgyUpdateManager.installApk(file)
                }

                override fun onProgressUpdate(vararg args: Int?) {
                    LogUtils.log("update download apk progress$args")
                }

            })
            .register()
    }
}