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

@file:Suppress("DEPRECATION")

package statusbar.lyric.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.MiuiStatusBarManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.Settings
import android.util.Base64
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import de.robv.android.xposed.XSharedPreferences
import statusbar.lyric.BuildConfig
import statusbar.lyric.config.Config
import java.io.*
import java.util.*


@SuppressLint("StaticFieldLeak")
object Utils {
    const val appCenterKey = "aa0a0b32-8d8d-43ca-8956-947e1ed8294d"

    private val hasMiuiSetting: Boolean = isPresent("android.provider.MiuiSettings")
    private val packNameToIconName = HashMap<String, String>().apply {
        put("com.netease.cloudmusic", "Netease")
        put("com.kugou", "KuGou")
        put("com.tencent.qqmusic", "QQMusic")
        put("remix.myplayer", "Myplayer")
        put("cmccwm.mobilemusic", "MiGu")
        put("cn.kuwo", "KuWo")
    }

    @JvmStatic
    fun getPref(key: String?): XSharedPreferences? {
        val pref = XSharedPreferences(BuildConfig.APPLICATION_ID, key)
        return if (pref.file.canRead()) pref else null
    }

    @SuppressLint("WorldReadableFiles")
    @Suppress("DEPRECATION")
    @JvmStatic
    fun getSP(context: Context, key: String?): SharedPreferences? {
        return context.createDeviceProtectedStorageContext().getSharedPreferences(key, Context.MODE_WORLD_READABLE)
    }

    @JvmStatic
    fun stringToBitmap(string: String?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val bitmapArray: ByteArray = Base64.decode(string, Base64.DEFAULT)
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    // 判断class是否存在
    @JvmStatic
    fun isPresent(name: String): Boolean {
        return try {
            Objects.requireNonNull(Thread.currentThread().contextClassLoader).loadClass(name)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    // 报错转内容
    @JvmStatic
    fun dumpException(e: Exception): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        return sw.toString()
    }

    @JvmStatic
    fun dumpIOException(e: IOException): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        return sw.toString()
    }

    @JvmStatic
    fun dumpNoSuchFieldError(e: NoSuchFieldError): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        return sw.toString()
    }

    @JvmStatic
    fun stringsListAdd(strArr: Array<String?>, newStr: String): Array<String?> {
        val newStrArr = arrayOfNulls<String>(strArr.size + 1)
        System.arraycopy(strArr, 0, newStrArr, 0, strArr.size)
        newStrArr[strArr.size] = newStr
        return newStrArr
    }

    //状态栏图标设置
    @JvmStatic
    fun setStatusBar(application: Context, isOpen: Boolean, config: Config) {
        var isCarrier = 1
        var notCarrier = 0
        if (isOpen) {
            isCarrier = 0
            notCarrier = 1
        }
        if (!hasMiuiSetting) {
            if (config.getHNoticeIcon() && Settings.System.getInt(application.contentResolver, "status_bar_show_notification_icon", 1) == notCarrier) {
                Settings.System.putInt(application.contentResolver, "status_bar_show_notification_icon", isCarrier)
            }
            if (config.getHNetSpeed() && Settings.System.getInt(application.contentResolver, "status_bar_show_notification_icon", 1) == notCarrier) {
                Settings.System.putInt(application.contentResolver, "status_bar_show_network_speed", isCarrier)
            }
        } else {
            if (config.getHNoticeIcon() && MiuiStatusBarManager.isShowNotificationIcon(application) != isOpen) {
                MiuiStatusBarManager.setShowNotificationIcon(application, isOpen)
            }
            if (config.getHNetSpeed() && MiuiStatusBarManager.isShowNetworkSpeed(application) != isOpen) {
                MiuiStatusBarManager.setShowNetworkSpeed(application, isOpen)
            }

        }
        if (config.getHCuk() && Settings.System.getInt(application.contentResolver, "status_bar_show_carrier_under_keyguard", 1) == isCarrier) {
            Settings.System.putInt(application.contentResolver, "status_bar_show_carrier_under_keyguard", notCarrier)
        }

    }

    @JvmStatic
    fun isServiceRunningList(context: Context, str: Array<String?>): Boolean {
        for (mStr in str) {
            if (mStr != null) {
                if (isAppRunning(context, mStr)) {
                    return true
                }
            }
        }
        return false
    }

    // 判断程序是否运行
    @JvmStatic
    fun isAppRunning(context: Context, str: String): Boolean {
        if (isServiceRunning(context, str)) {
            return true
        }
        val runningServices =
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningTasks(200)
        if (runningServices.size <= 0) {
            return false
        }
        for (runningServiceInfo in runningServices) {
            if (runningServiceInfo.baseActivity!!.className.contains(str)) {
                LogUtils.e("程序运行: $str")
                return true
            }
        }
        return false
    }

    // 判断服务是否运行
    @JvmStatic
    fun isServiceRunning(context: Context, str: String): Boolean {
        val runningServices =
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(200)
        if (runningServices.size <= 0) {
            return false
        }
        for (runningServiceInfo in runningServices) {
            if (runningServiceInfo.service.className.contains(str)) {
                LogUtils.e("服务运行: $str")
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun inAnim(str: String?): Animation? {
        val animationSet = AnimationSet(true)
        val translateAnimation: TranslateAnimation = when (str) {
            "top" -> TranslateAnimation(0F, 0F, 100F, 0F)
            "lower" -> TranslateAnimation(0F, 0F, -100F, 0F)
            "left" -> TranslateAnimation(100F, 0F, 0F, 0F)
            "right" -> TranslateAnimation(-100F, 0F, 0F, 0F)
            else -> return null
        }
        // 设置动画300ms
        translateAnimation.duration = 300
        val alphaAnimation = AlphaAnimation(0F, 1F)
        // 设置动画300ms
        alphaAnimation.duration = 300
        animationSet.addAnimation(translateAnimation)
        animationSet.addAnimation(alphaAnimation)
        return animationSet
    }

    @JvmStatic
    fun outAnim(str: String?): Animation? {
        val animationSet = AnimationSet(true)
        val translateAnimation: TranslateAnimation = when (str) {
            "top" -> TranslateAnimation(0F, 0F, 0F, -100F)
            "lower" -> TranslateAnimation(0F, 0F, 0F, 100F)
            "left" -> TranslateAnimation(0F, -100F, 0F, 0F)
            "right" -> TranslateAnimation(0F, 100F, 0F, 0F)
            else -> return null
        }
        // 设置动画300ms
        translateAnimation.duration = 300
        val alphaAnimation = AlphaAnimation(1F, 0F)
        // 设置动画300ms
        alphaAnimation.duration = 300
        animationSet.addAnimation(translateAnimation)
        animationSet.addAnimation(alphaAnimation)
        return animationSet
    }

    @JvmStatic
    fun sendLyric(context: Context?, lyric: String?, icon: String?) {
        context?.sendBroadcast(
            Intent().apply {
                action = "Lyric_Server"
                putExtra("Lyric_Data", lyric)
                putExtra("Lyric_Icon", icon)
                putExtra("Lyric_Type", "hook")
            }
        )
    }

    @JvmStatic
    fun packNameGetIconName(packName: String?): String? {
        return packNameToIconName[packName]
    }

    @JvmStatic
    fun sendLyric(context: Context, lyric: String?, icon: String?, useSystemMusicActive: Boolean, packName: String?) {
        context.sendBroadcast(
            Intent().apply {
                action = "Lyric_Server"
                putExtra("Lyric_Data", lyric)
                putExtra("Lyric_Type", "app")
                putExtra("Lyric_PackName", packName)
                putExtra("Lyric_Icon", icon)
                putExtra("Lyric_UseSystemMusicActive", useSystemMusicActive)
            }
        )
    }

    /**
     * dp2px
     */
    @JvmStatic
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    @JvmStatic
    fun voidShell(command: String, isSu: Boolean) {
        try {
            if (isSu) {
                val p = Runtime.getRuntime().exec("su")
                val outputStream = p.outputStream
                val dataOutputStream = DataOutputStream(outputStream)
                dataOutputStream.writeBytes(command)
                dataOutputStream.flush()
                dataOutputStream.close()
                outputStream.close()
            } else {
                Runtime.getRuntime().exec(command)
            }
        } catch (ignored: Throwable) {
        }
    }
}
