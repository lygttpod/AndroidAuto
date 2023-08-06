package com.android.wechat.tools.page

import com.android.accessibility.ext.acc.*
import com.android.accessibility.ext.default
import com.android.accessibility.ext.task.retryCheckTaskWithLog
import com.android.accessibility.ext.task.retryTaskWithLog
import com.android.wechat.tools.em.FriendStatus
import com.android.wechat.tools.em.CheckStatus
import com.android.wechat.tools.data.WxUserInfo
import com.android.wechat.tools.service.wxAccessibilityService

object WXRemittancePage : IPage {

    override fun pageClassName() = "com.tencent.mm.plugin.remittance.ui.RemittanceUI"

    override fun pageTitleName() = "转账页"

    override fun isMe(): Boolean {
        return false
    }

    /**
     * 在转账页先判断一次好友状态是否正常
     * 判断标准是【如果出现 [转账给 测试(**名)] 及昵称后边有加星的真名 就说明好友正常，否则还需要后边进一步验证 】
     */
    suspend fun checkIsNormalFriend(): WxUserInfo? {
        return delayAction {
            var wxUserInfo: WxUserInfo? = null
            wxUserInfo = retryTaskWithLog("获取转账页的用户名和微信号") {
                //android.widget.TextView → 转账给 测试(**名) → com.tencent.mm:id/inh
                val friendName = wxAccessibilityService
                    ?.findById("com.tencent.mm:id/inh")
                    ?.text
                    .default()
                    .split("转账给")
                    .getOrNull(1)
                    .default()
                    .trim()
                //android.widget.TextView → 微信号: wxid_xxx → com.tencent.mm:id/ini
                val wxCode = wxAccessibilityService
                    ?.findById("com.tencent.mm:id/ini")
                    ?.text
                    ?.split("微信号:")
                    ?.getOrNull(1)
                    .default()
                    .trim()
                if (friendName.isNotBlank()) {
                    val isNormal = friendName.contains("(*") && friendName.endsWith(")")
                    if (isNormal) {
                        WxUserInfo(friendName, wxCode, FriendStatus.NORMAL)
                    } else {
                        WxUserInfo(friendName, wxCode, FriendStatus.UNKNOW)
                    }
                } else {
                    null
                }
            }
            wxUserInfo
        }
    }

    /**
     * 输入测试金额
     */
    suspend fun inputMoney(money: String = "0.01"): Boolean {
        return delayAction {
            retryCheckTaskWithLog("输入转账金额") {
                wxAccessibilityService?.findById("com.tencent.mm:id/lg_")?.inputText(money) == true
            }
        }
    }

    /**
     * 点击转账按钮
     */
    suspend fun clickTransferMoney(): Boolean {
        return delayAction {
            retryCheckTaskWithLog("点击支付页的转账按钮") {
                wxAccessibilityService.clickById("com.tencent.mm:id/ffp")
            }
        }
    }

    /**
     * 根据转账后的提示去判断好友状态
     */
    suspend fun checkStatus(): CheckStatus? {
        return delayAction {
            retryTaskWithLog("查询支付状态") {
                val find =
                    wxAccessibilityService?.findByContainsText(
                        false,
                        CheckStatus.values().map { it.mark })
                if (find == null) {
                    null
                } else {
                    val status = CheckStatus.values().find { find.text.contains(it.mark) }
                    status
                }
            }
        }
    }

    /**
     * 转账异常时候会有弹框提醒，点击【我知道了】关闭弹框
     */
    suspend fun clickIKnow() {
        delayAction {
            retryCheckTaskWithLog("点击我知道了") {
                //android.widget.Button → 我知道了 → com.tencent.mm:id/guw
                wxAccessibilityService.clickByText("我知道了")
            }
        }
    }
}