package statusbar.lyric.hook.app

import android.app.AndroidAppHelper
import android.content.Context
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import de.robv.android.xposed.callbacks.XC_LoadPackage
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.ktx.hookAfterMethod


class Myplayer(private val lpparam: XC_LoadPackage.LoadPackageParam) {
    fun hook(){
        "remix.myplayer.util.p".hookAfterMethod("o", Context::class.java, classLoader = lpparam.classLoader) {
            it.result = true
        }
        "remix.myplayer.service.MusicService".hookAfterMethod("n1", String::class.java, classLoader = lpparam.classLoader) {
            val context: Context = it.thisObject as Context
            AppCenter.start(
                AndroidAppHelper.currentApplication(), "83d39340-9e06-406d-85f3-c663aed8e4ea",
                Analytics::class.java, Crashes::class.java
            )
            LogUtils.e("myplayer: " + it.args[0].toString())
            Utils.sendLyric(context, it.args[0].toString(), "Myplayer")
        }
    }
}