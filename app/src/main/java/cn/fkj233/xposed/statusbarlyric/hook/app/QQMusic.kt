package cn.fkj233.xposed.statusbarlyric.hook.app

import android.app.AndroidAppHelper
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import de.robv.android.xposed.callbacks.XC_LoadPackage
import cn.fkj233.xposed.statusbarlyric.hook.MeiZuStatusBarLyric.guiseFlyme


class QQMusic(private val lpparam: XC_LoadPackage.LoadPackageParam) {
    fun hook(){
        AppCenter.start(
            AndroidAppHelper.currentApplication(), "1ddba47c-cfe2-406e-86a2-0e7fa94785a4",
            Analytics::class.java, Crashes::class.java
        )
        guiseFlyme(lpparam, true)
    }
}