package cn.fkj233.xposed.statusbarlyric.hook.app

import android.app.AndroidAppHelper
import android.content.Context
import android.content.Intent
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import cn.fkj233.xposed.statusbarlyric.utils.LogUtils
import cn.fkj233.xposed.statusbarlyric.utils.Utils
import cn.fkj233.xposed.statusbarlyric.utils.ktx.hookAfterMethod


class Api(private val lpparam: XC_LoadPackage.LoadPackageParam) {
    fun hook(lpparam: LoadPackageParam) {
        "StatusBarLyric.API.StatusBarLyric".hookAfterMethod("hasEnable", classLoader = lpparam.classLoader) {
            it.result = true
        }
        "StatusBarLyric.API.StatusBarLyric".hookAfterMethod("sendLyric", Context::class.java,
            String::class.java,
            String::class.java,
            String::class.java,
            Boolean::class.javaPrimitiveType, classLoader = lpparam.classLoader) {
            AppCenter.start(
                AndroidAppHelper.currentApplication(), "6534cbef-ab72-4b7b-971e-ed8e6352ba30",
                Analytics::class.java, Crashes::class.java
            )
            LogUtils.e("API: " + it.args[1])
            Utils.sendLyric(
                it.args[0] as Context,
                it.args[1] as String,
                it.args[2] as String,
                it.args[4] as Boolean,
                it.args[3] as String
            )
        }
        "StatusBarLyric.API.StatusBarLyric".hookAfterMethod("stopLyric", Context::class.java, classLoader = lpparam.classLoader) {
            (it.args[0] as Context).sendBroadcast(
                Intent().apply {
                    action = "Lyric_Server"
                    putExtra("Lyric_Type", "app_stop")
                }
            )
        }
    }
}