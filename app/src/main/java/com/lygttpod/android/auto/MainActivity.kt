package com.lygttpod.android.auto

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.lygttpod.android.activity.result.api.ktx.showToast
import com.lygttpod.android.activity.result.api.observer.PermissionApi
import com.lygttpod.android.auto.databinding.ActivityMainBinding
import com.lygttpod.android.auto.update.UpdateApp
import com.lygttpod.android.auto.wx.service.WXAccessibility
import com.pgyersdk.update.PgyUpdateManager

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val permissionApi = PermissionApi(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        UpdateApp.checkVersion(this) { downloadUrl ->
            permissionApi.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                if (it) {
                    showToast("正在下载新版本")
                    PgyUpdateManager.downLoadApk(downloadUrl)
                } else {
                    showToast("未授权无法下载新版本哦")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        WXAccessibility.isInWXApp.set(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}