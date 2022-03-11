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

import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import statusbar.lyric.BuildConfig
import statusbar.lyric.hook.app.*
import statusbar.lyric.utils.AppCenterUtils
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils

class MainHook : IXposedHookLoadPackage {
    var context: Context? = null

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
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
                Kuwo(lpparam).hook()
                LogUtils.e("hook酷我音乐结束")
            }
            "com.tencent.qqmusic" -> {
                LogUtils.e("正在hookQQ音乐")
                MeiZuStatusBarLyric.guiseFlyme(lpparam, true)
                AppCenterUtils(Utils.appCenterKey, lpparam)
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
                AppCenterUtils(Utils.appCenterKey, lpparam)
                LogUtils.e("Hook 咪咕音乐结束")
            }
            "com.miui.player" -> {
                LogUtils.e("正在Hook 小米音乐")
                MIPlayer(lpparam).hook()
                LogUtils.e("Hook 小米音乐结束")
            }
            "com.meizu.media.music" -> {
                LogUtils.e("正在Hook 魅族音乐")
                MeiZuStatusBarLyric.guiseFlyme(lpparam, true)
                AppCenterUtils(Utils.appCenterKey, lpparam)
                LogUtils.e("Hook 魅族音乐结束")
            }
            else -> {
                LogUtils.e("正在Hook ${lpparam.processName}")
                Api(lpparam).hook()
                LogUtils.e("Hook ${lpparam.processName}结束")
            }
        }
    }
}
