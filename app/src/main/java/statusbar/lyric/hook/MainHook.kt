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
import statusbar.lyric.tools.LogTools.log
import java.util.Locale

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!XposedOwnSP.config.masterSwitch) {
            moduleRes.getString(R.string.master_off).log()
            return
        }

        EzXHelper.initHandleLoadPackage(lpparam)
        when (lpparam.packageName) {
            "com.android.systemui" -> {
                "${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}[${Locale.getDefault().language}] *${BuildConfig.BUILD_TYPE})".log()
                if (XposedOwnSP.config.testMode) {
                    moduleRes.getString(R.string.hook_page).log()
                    initHooks(SystemUITest())
                } else {
                    moduleRes.getString(R.string.lyric_mode).log()
                    initHooks(SystemUILyric())
                }
            }
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelper.initZygote(startupParam)
        if (!XposedOwnSP.config.masterSwitch) {
            moduleRes.getString(R.string.master_off).log()
            return
        }
    }

    private fun initHooks(vararg hook: BaseHook) {
        hook.forEach {
            try {
                if (it.isInit) return
                it.init()
                it.isInit = true
                "${moduleRes.getString(R.string.hook_succeeded)}:${it.javaClass.simpleName}".log()
            } catch (e: Exception) {
                "${moduleRes.getString(R.string.hook_failed)}:${it.javaClass.simpleName}".log()
                e.log()
            }
        }
    }
}