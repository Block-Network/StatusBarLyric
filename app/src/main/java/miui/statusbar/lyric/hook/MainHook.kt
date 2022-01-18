package miui.statusbar.lyric.hook

import android.app.Application
import android.content.Context
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import miui.statusbar.lyric.BuildConfig
import miui.statusbar.lyric.hook.app.*
import miui.statusbar.lyric.utils.Utils
import miui.statusbar.lyric.utils.ktx.hookAfterMethod
import miui.statusbar.lyric.utils.LogUtils
import miui.statusbar.lyric.utils.XposedOwnSP.apiList


class MainHook: IXposedHookLoadPackage {
    var context: Context? = null
    var init: Boolean = false
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        Application::class.java.hookAfterMethod("attach", Context::class.java) {
            context = it.args[0] as Context
            Utils.context = context
            if (!init) {
                if (!lpparam.packageName.equals("com.android.systemui")) {
                    AppCenter.start(
                        it.thisObject as Application, "9b618dc1-602a-4af1-82ee-c60e4a243e1f",
                        Analytics::class.java, Crashes::class.java
                    )
                }
            }
            init = true
        }
        LogUtils.e("Debug已开启")
        LogUtils.e("${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE} *${BuildConfig.BUILD_TYPE})")
        LogUtils.e("当前包名: " + lpparam.packageName)

        when (lpparam.packageName) {
            "com.android.systemui" -> {
                LogUtils.e("正在hook系统界面")
                SystemUI(lpparam).hook()
                LogUtils.e("hook系统界面结束")
            }
            "com.netease.cloudmusic" -> {
                LogUtils.e("正在hook网易云音乐")
                Netease(lpparam).hook()
                LogUtils.e("hook网易云音乐结束")
            }
            "com.kugou.android" -> {
                LogUtils.e("正在hook酷狗音乐")
                Kugou(lpparam).hook()
                LogUtils.e("hook酷狗音乐结束")
            }
            "cn.kuwo.player" -> {
                LogUtils.e("正在hook酷我音乐")
                Kugou(lpparam).hook()
                LogUtils.e("hook酷我音乐结束")
            }
            "com.tencent.qqmusic" -> {
                LogUtils.e("正在hookQQ音乐")
                QQMusic(lpparam).hook()
                LogUtils.e("hookQQ音乐结束")
            }
            "remix.myplayer" -> {
                LogUtils.e("正在Hook myplayer")
                Myplayer(lpparam).hook()
                LogUtils.e("hook myplayer结束")
            }
            "cmccwm.mobilemusic" -> {
                LogUtils.e("正在Hook 咪咕音乐")
                MeiZuStatusBarLyric.guiseFlyme(lpparam, true)
                LogUtils.e("Hook 咪咕音乐结束")
            }
            "com.meizu.media.music" -> MeiZuStatusBarLyric.guiseFlyme(lpparam, true)
            else -> {
                if (apiList.hasEnable(lpparam.packageName) == true) {
                    Api(lpparam).hook(lpparam)
                }
            }
        }
    }

}