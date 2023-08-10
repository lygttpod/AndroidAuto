package com.android.wechat.tools.helper

import android.annotation.SuppressLint
import android.widget.TextView
import com.android.accessibility.ext.acc.printNodeInfo
import com.android.accessibility.ext.isAccessibilityOpened
import com.android.accessibility.ext.toast
import com.android.wechat.tools.App
import com.android.wechat.tools.R
import com.android.wechat.tools.ktx.canDrawOverlays
import com.android.wechat.tools.service.WXAccessibility
import com.android.wechat.tools.service.wxAccessibilityService
import com.hjq.window.EasyWindow

object FloatManager {

    private const val FLOAT_PRINT = "float_print"
    private const val FLOAT_TASK_PROGRESS = "float_task_progress"

    @SuppressLint("StaticFieldLeak")
    private var printFloat: EasyWindow<*>? = null

    @SuppressLint("StaticFieldLeak")
    private var taskProgressFloat: EasyWindow<*>? = null

    fun showPrintFloat() {
        if (!App.instance().canDrawOverlays()) return
        if (printFloat?.isShowing == true) {
            printFloat?.cancel()
            return
        }
        if (printFloat == null) {
            printFloat = EasyWindow.with(App.instance())
                .setTag(FLOAT_PRINT)
                .setContentView(R.layout.widget_float_print)
                .setWidth(150)
                .setHeight(150)
                .setOnClickListener(
                    R.id.tv_print_node
                ) { window, view ->
                    if (App.instance().isAccessibilityOpened(WXAccessibility::class.java)) {
                        wxAccessibilityService?.printNodeInfo()
                        App.instance().toast("请在AS控制台查看当前页面布局信息")
                    } else {
                        App.instance().toast("请先开启无障碍服务")
                    }
                }
                .setDraggable()
        }
        printFloat?.show()
    }

    fun showTaskProgressFloat(progress: String? = null) {
        if (!App.instance().canDrawOverlays()) return
        if (taskProgressFloat == null) {
            taskProgressFloat = EasyWindow.with(App.instance())
                .setTag(FLOAT_TASK_PROGRESS)
                .setContentView(R.layout.widget_float_task_progress)
                .setWidth(200)
                .setHeight(200)
                .setOnClickListener(R.id.tv_task) { window, view ->

                }
                .setXOffset(500)
                .setYOffset(500)
                .setDraggable()
        }
        taskProgressFloat?.let { float ->
            progress?.let { float.findViewById<TextView>(R.id.tv_task).text = it }
            float.show()
        }
    }

}