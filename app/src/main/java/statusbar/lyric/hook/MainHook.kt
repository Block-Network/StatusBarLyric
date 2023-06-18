package statusbar.lyric.hook

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import com.github.kyuubiran.ezxhelper.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP
import statusbar.lyric.hook.app.SystemUILyric
import statusbar.lyric.hook.app.SystemUITest
import statusbar.lyric.tools.LogTools
import java.util.Locale

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!XposedOwnSP.config.masterSwitch) {
            LogTools.e(moduleRes.getString(R.string.MasterOff))
            return
        }
        EzXHelper.initHandleLoadPackage(lpparam)
        if (lpparam.packageName == "com.android.systemui") {
            LogTools.e("${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}[${Locale.getDefault().language}] *${BuildConfig.BUILD_TYPE})")
            if (XposedOwnSP.config.testMode) {
                LogTools.e(moduleRes.getString(R.string.TestMode))
                initHooks(SystemUITest())
            } else {
                LogTools.e(moduleRes.getString(R.string.LyricMode))
                initHooks(SystemUILyric())
            }

        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        if (!XposedOwnSP.config.masterSwitch) {
            LogTools.e(moduleRes.getString(R.string.MasterOff))
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
                LogTools.e("${moduleRes.getString(R.string.HookSucceeded)}:${it.javaClass.simpleName}")
            } catch (_: Exception) {
                LogTools.e("${moduleRes.getString(R.string.HookFailed)}:${it.javaClass.simpleName}")
            }
        }
    }
}