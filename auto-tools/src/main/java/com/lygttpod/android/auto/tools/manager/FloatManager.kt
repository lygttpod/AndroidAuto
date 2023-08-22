package com.lygttpod.android.auto.tools.manager

import android.annotation.SuppressLint
import android.app.Application
import com.android.accessibility.ext.acc.printNodeInfo
import com.android.accessibility.ext.isAccessibilityOpened
import com.android.accessibility.ext.toast
import com.hjq.window.EasyWindow
import com.lygttpod.android.auto.tools.accessibility.AutoToolsAccessibility
import com.lygttpod.android.auto.tools.accessibility.AutoToolsAccessibility.Companion.autoToolsAccessibility
import com.lygttpod.android.auto.tools.R
import com.lygttpod.android.auto.tools.manager.ContentManger.printContent
import com.lygttpod.android.auto.tools.ktx.canDrawOverlays

object FloatManager {

    private const val FLOAT_PRINT = "float_print"

    @SuppressLint("StaticFieldLeak")
    private var printFloat: EasyWindow<*>? = null

    fun showPrintFloat(context: Application) {
        if (!context.canDrawOverlays()) return
        if (printFloat?.isShowing == true) {
            printFloat?.cancel()
            return
        }
        if (printFloat == null) {
            printFloat = EasyWindow.with(context)
                .setTag(FLOAT_PRINT)
                .setContentView(R.layout.widget_float_print)
                .setWidth(150)
                .setHeight(150)
                .setOnClickListener(
                    R.id.tv_print_node
                ) { window, view ->
                    if (context.isAccessibilityOpened(AutoToolsAccessibility::class.java)) {
                        printContent = autoToolsAccessibility?.printNodeInfo()
                        context.toast("请在AS控制台查看当前页面布局信息")
                    } else {
                        context.toast("请先开启无障碍服务")
                    }
                }
                .setDraggable()
        }
        printFloat?.show()
    }


}