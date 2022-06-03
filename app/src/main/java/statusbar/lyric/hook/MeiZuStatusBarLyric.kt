/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/577fkj/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/StatusBarLyric/blob/main/LICENSE>.
 */

package statusbar.lyric.hook

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.content.Context
import android.os.Build
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import statusbar.lyric.utils.AppCenterUtils
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.ktx.hookAfterMethod
import statusbar.lyric.utils.ktx.lpparam

@SuppressLint("StaticFieldLeak")
object MeiZuStatusBarLyric {
    @SuppressLint("StaticFieldLeak")
    var context: Context? = null

    //模拟flyme
    fun guiseFlyme(hookNotification: Boolean) {
        AppCenterUtils(Utils.appCenterKey) // 获取Context
        Application::class.java.hookAfterMethod("attach", Context::class.java) {
            context = it.args[0] as Context
        }
        "android.os.SystemProperties".hookAfterMethod("get", String::class.java, String::class.java) {
            XposedHelpers.setStaticObjectField(Build::class.java, "BRAND", "meizu")
            XposedHelpers.setStaticObjectField(Build::class.java, "MANUFACTURER", "Meizu")
            XposedHelpers.setStaticObjectField(Build::class.java, "DEVICE", "m1892")
            XposedHelpers.setStaticObjectField(Build::class.java, "DISPLAY", "Flyme")
            XposedHelpers.setStaticObjectField(Build::class.java, "PRODUCT", "meizu_16thPlus_CN")
            XposedHelpers.setStaticObjectField(Build::class.java, "MODEL", "meizu 16th Plus")
        }
        "java.lang.Class".hookAfterMethod("getDeclaredField", String::class.java) {
            when (it.args[0].toString()) {
                "FLAG_ALWAYS_SHOW_TICKER" -> it.result = MeiZuNotification::class.java.getDeclaredField("FLAG_ALWAYS_SHOW_TICKER_HOOK")
                "FLAG_ONLY_UPDATE_TICKER" -> it.result = MeiZuNotification::class.java.getDeclaredField("FLAG_ONLY_UPDATE_TICKER_HOOK")
            }
        }
        "java.lang.Class".hookAfterMethod("getField", String::class.java) {
            when (it.args[0].toString()) {
                "FLAG_ALWAYS_SHOW_TICKER" -> it.result = MeiZuNotification::class.java.getDeclaredField("FLAG_ALWAYS_SHOW_TICKER_HOOK")
                "FLAG_ONLY_UPDATE_TICKER" -> it.result = MeiZuNotification::class.java.getDeclaredField("FLAG_ONLY_UPDATE_TICKER_HOOK")
            }
        }
        if (!hookNotification) {
            return
        }
        "android.app.NotificationManager".hookAfterMethod("notify", Int::class.javaPrimitiveType, Notification::class.java) {
            val notification = it.args[1] as Notification
            val charSequence = notification.tickerText
            LogUtils.e(notification.toString())
            if (notification.flags == 0) {
                context?.let { it1 -> Utils.sendLyric(it1, "", Utils.packNameGetIconName(lpparam.packageName)) }
                return@hookAfterMethod
            }
            val isLyric = notification.flags and MeiZuNotification.FLAG_ALWAYS_SHOW_TICKER != 0 || notification.flags and MeiZuNotification.FLAG_ONLY_UPDATE_TICKER != 0
            if (charSequence == null || !isLyric) {
                return@hookAfterMethod
            }
            context?.let { it1 -> Utils.sendLyric(it1, charSequence.toString(), Utils.packNameGetIconName(lpparam.packageName)) }
        }
    }
}