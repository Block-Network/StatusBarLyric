package statusbar.lyric.hook

import com.github.kyuubiran.ezxhelper.EzXHelper
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import statusbar.lyric.BuildConfig
import statusbar.lyric.config.XposedOwnSP
import statusbar.lyric.hook.app.SystemUILyric
import statusbar.lyric.hook.app.SystemUITest
import statusbar.lyric.tools.LogTools
import java.util.Locale

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!XposedOwnSP.config.masterSwitch) {
            LogTools.e("lyricSwitch is off")
            return
        }
        EzXHelper.initHandleLoadPackage(lpparam)
        if (lpparam.packageName == "com.android.systemui") {
            LogTools.e("Debug enable")
            LogTools.e("${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}[${Locale.getDefault().language}] *${BuildConfig.BUILD_TYPE})")
            LogTools.e("This packName: ${lpparam.packageName}")
            if (XposedOwnSP.config.testMode) {
                initHooks(SystemUITest())
            } else {
                initHooks(SystemUILyric())
            }

        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        if (!XposedOwnSP.config.masterSwitch) {
            LogTools.e("lyricSwitch is off")
            return
        }
        EzXHelper.initZygote(startupParam)
    }

    private fun initHooks(vararg hook: BaseHook) {
        hook.forEach {
            try {
                if (it.isInit) return
                it.init()
                it.isInit = true
                LogTools.e("Inited hook: ${it.javaClass.name}")
            } catch (_: Exception) {
                LogTools.e("Failed init hook: ${it.javaClass.name}")
            }
        }
    }
}