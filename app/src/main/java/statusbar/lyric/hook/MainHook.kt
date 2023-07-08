package statusbar.lyric.hook

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
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
            LogTools.xp(moduleRes.getString(R.string.MasterOff))
            return
        }

        EzXHelper.initHandleLoadPackage(lpparam)
        when (lpparam.packageName) {
            "com.android.systemui" -> {
                LogTools.xp("${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}[${Locale.getDefault().language}] *${BuildConfig.BUILD_TYPE})")
                if (XposedOwnSP.config.testMode) {
                    LogTools.xp(moduleRes.getString(R.string.HookPage))
                    initHooks(SystemUITest())
                } else {
                    LogTools.xp(moduleRes.getString(R.string.LyricMode))
                    initHooks(SystemUILyric())
                }
            }
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelper.initZygote(startupParam)
        if (!XposedOwnSP.config.masterSwitch) {
            LogTools.xp(moduleRes.getString(R.string.MasterOff))
            return
        }
    }

    private fun initHooks(vararg hook: BaseHook) {
        hook.forEach {
            if (it.isInit) return
            it.init()
            it.isInit = true
            LogTools.xp("${moduleRes.getString(R.string.HookSucceeded)}:${it.javaClass.simpleName}")
//            try {
//                if (it.isInit) return
//                it.init()
//                it.isInit = true
//                LogTools.xp("${moduleRes.getString(R.string.HookSucceeded)}:${it.javaClass.simpleName}")
//            } catch (_: Exception) {
//                LogTools.xp("${moduleRes.getString(R.string.HookFailed)}:${it.javaClass.simpleName}")
//            }
        }
    }
}