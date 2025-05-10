/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/Block-Network/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as
 * published by Block-Network contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/Block-Network/StatusBarLyric/blob/main/LICENSE>.
 */

package statusbar.lyric.hook

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP.config
import statusbar.lyric.hook.module.Self
import statusbar.lyric.hook.module.SystemUILyric
import statusbar.lyric.hook.module.SystemUITest
import statusbar.lyric.tools.LogTools
import statusbar.lyric.tools.LogTools.log
import java.util.Locale

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        LogTools.init(config.outLog)

        EzXHelper.initHandleLoadPackage(lpparam)
        when (lpparam.packageName) {
            "com.android.systemui" -> {
                if (!config.masterSwitch) {
                    moduleRes.getString(R.string.master_off).log()
                    return
                }
                "${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}[${Locale.getDefault().language}] *${BuildConfig.BUILD_TYPE})".log()
                if (config.testMode) {
                    moduleRes.getString(R.string.hook_page).log()
                    initHooks(SystemUITest())
                } else {
                    moduleRes.getString(R.string.lyric_mode).log()
                    try {
                        initHooks(SystemUILyric())
                    } catch (t: Throwable) {
                        t.log()
                    }
                }
            }

            BuildConfig.APPLICATION_ID -> {
                initHooks(Self())
            }
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelper.initZygote(startupParam)
        if (!config.masterSwitch) {
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