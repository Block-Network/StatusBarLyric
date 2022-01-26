package cn.fkj233.xposed.statusbarlyric.hook.app

import android.app.AndroidAppHelper
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import de.robv.android.xposed.callbacks.XC_LoadPackage
import cn.fkj233.xposed.statusbarlyric.utils.LogUtils
import cn.fkj233.xposed.statusbarlyric.utils.Utils
import cn.fkj233.xposed.statusbarlyric.utils.ktx.hookAfterMethod


class Kugou(private val lpparam: XC_LoadPackage.LoadPackageParam) {
    fun hook() {
        "android.media.AudioManager".hookAfterMethod("isBluetoothA2dpOn", classLoader = lpparam.classLoader) {
            it.result = true
        }
        "com.kugou.framework.player.c".hookAfterMethod("a", HashMap::class.java, classLoader = lpparam.classLoader) {
            AppCenter.start(
                AndroidAppHelper.currentApplication(), "d99b2230-6449-4fb3-ba0e-7e47cc470d6d",
                Analytics::class.java, Crashes::class.java
            )
            LogUtils.e("酷狗音乐:" + (it.args[0] as HashMap<*, *>).values.toList()[0])
            Utils.sendLyric(
                AndroidAppHelper.currentApplication(),
                "" + (it.args[0] as HashMap<*, *>).values.toList()[0],
                "KuGou"
            )
        }
    }
}