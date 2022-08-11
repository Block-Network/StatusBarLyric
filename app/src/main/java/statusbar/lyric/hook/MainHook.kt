/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/577fkj/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/StatusBarLyric/blob/main/LICENSE>.
 */

package statusbar.lyric.hook

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import statusbar.lyric.BuildConfig
import statusbar.lyric.hook.app.*
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.ktx.init
import java.util.*

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        LogUtils.e("Debug enable")
        LogUtils.e("${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}[${Locale.getDefault().language}] *${BuildConfig.BUILD_TYPE})")
        LogUtils.e("This packName: ${lpparam.packageName}")
        init(lpparam)

        val hook = when (lpparam.packageName) {
            "com.android.systemui" -> {
                LogUtils.e("start hook systemui")
                SystemUI()
            }
            "com.netease.cloudmusic" -> {
                LogUtils.e("start hook netease")
                Netease()
            }
            "com.kugou.android", "com.kugou.android.lite" -> {
                LogUtils.e("start hook kugou")
                Kugou(lpparam.packageName)
            }
            "cn.kuwo.player" -> {
                LogUtils.e("start hook kuwo")
                Kuwo()
            }
            "com.tencent.qqmusic" -> {
                LogUtils.e("start hook qqmusic")
                MeiZuStatusBarLyric.guiseFlyme(true)
                null
            }
            "remix.myplayer" -> {
                LogUtils.e("start Hook myplayer")
                Myplayer()
            }
            "cmccwm.mobilemusic" -> {
                LogUtils.e("start Hook migu")
                MeiZuStatusBarLyric.guiseFlyme(true)
                null
            }
            "com.miui.player" -> {
                LogUtils.e("start Hook xiaomi Player")
                Miplayer()
            }
            "com.meizu.media.music" -> {
                LogUtils.e("start Hook Meizu Music")
                MeiZuStatusBarLyric.guiseFlyme(true)
                null
            }
            "com.r.rplayer" -> {
                LogUtils.e("start Hook RPlayer")
                RPlayer()
            }
            else -> {
                LogUtils.e("start Hook ${lpparam.processName}")
                Api()
            }
        }
        hook?.hook()
        LogUtils.e("Hook ${lpparam.processName} end")
    }
}
