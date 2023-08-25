package com.lygttpod.android.auto.update

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.lygttpod.android.auto.R
import com.lygttpod.android.auto.databinding.DialogVersionTipBinding
import com.lygttpod.android.auto.ktx.dp2px
import com.pgyersdk.update.javabean.AppBean


class VersionTipDialog(context: Context) : Dialog(context, R.style.loading_dialog) {

    companion object {
        private var appBean: AppBean? = null
        private var downloadClick: ((String) -> Unit)? = null
        fun showDialog(context: Context, appBean: AppBean, downloadClick: ((String) -> Unit)?) {
            Companion.appBean = appBean
            Companion.downloadClick = downloadClick
            VersionTipDialog(context).show()
        }
    }

    private lateinit var binding: DialogVersionTipBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogVersionTipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        initWindow()
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnDownload.setOnClickListener {
            downloadClick?.invoke(
                appBean?.downloadURL ?: return@setOnClickListener
            )
            if (appBean?.isShouldForceToUpdate != true) {
                dismiss()
            }
        }
    }

    private fun initWindow() {
        window?.setLayout(
            context.dp2px(250f),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun show() {
        super.show()
        showData()
    }

    private fun showData() {
        val content = appBean?.releaseNote
        val versionName = appBean?.versionName
        val forceToUpdate = appBean?.isShouldForceToUpdate ?: false
        binding.tvContent.text = if (content.isNullOrBlank()) "代码优化" else content
        binding.tvTitle.text = context.getString(R.string.version_update_title, versionName)
        binding.btnCancel.visibility = if (forceToUpdate) View.GONE else View.VISIBLE
    }
}