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


class MainHook : IXposedHookLoadPackage {
    var context: Context? = null
    var init: Boolean = false
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        LogUtils.e("Debug已开启")
        LogUtils.e("${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE} *${BuildConfig.BUILD_TYPE})")
        LogUtils.e("当前包名: " + lpparam.packageName)

        when (lpparam.packageName) {
            "com.android.systemui" -> {
                LogUtils.e("正在hook系统界面")
                SystemUI(lpparam).hook()
                AppCenterUtils("1ddba47c-cfe2-406e-86a2-0e7fa94785a4")
                LogUtils.e("hook系统界面结束")
            }
            "com.netease.cloudmusic" -> {
                LogUtils.e("正在hook网易云音乐")
                Netease(lpparam).hook()
                AppCenterUtils("257e24bd-9f54-4250-9038-d27f348bfdc5")
                LogUtils.e("hook网易云音乐结束")
            }
            "com.kugou.android" -> {
                LogUtils.e("正在hook酷狗音乐")
                Kugou(lpparam).hook()
                AppCenterUtils("d99b2230-6449-4fb3-ba0e-7e47cc470d6d")
                LogUtils.e("hook酷狗音乐结束")
            }
            "cn.kuwo.player" -> {
                LogUtils.e("正在hook酷我音乐")
                Kuwo(lpparam).hook()
                AppCenterUtils("d99b2230-6449-4fb3-ba0e-7e47cc470d6d")
                LogUtils.e("hook酷我音乐结束")
            }
            "com.tencent.qqmusic" -> {
                LogUtils.e("正在hookQQ音乐")
                MeiZuStatusBarLyric.guiseFlyme(lpparam, true)
                AppCenterUtils("3b0a2096-452b-4564-8b78-ab98c7dea7fb")
                LogUtils.e("hookQQ音乐结束")
            }
            "remix.myplayer" -> {
                LogUtils.e("正在Hook myplayer")
                Myplayer(lpparam).hook()
                AppCenterUtils("83d39340-9e06-406d-85f3-c663aed8e4ea")
                LogUtils.e("hook myplayer结束")
            }
            "cmccwm.mobilemusic" -> {
                LogUtils.e("正在Hook 咪咕音乐")
                MeiZuStatusBarLyric.guiseFlyme(lpparam, true)
                AppCenterUtils("c2b6176d-7875-4b8d-924a-f62465c8dfda")
                LogUtils.e("Hook 咪咕音乐结束")
            }
            "com.meizu.media.music" -> MeiZuStatusBarLyric.guiseFlyme(lpparam, true)
            else -> {
                Api(lpparam).hook()
                AppCenterUtils("6534cbef-ab72-4b7b-971e-ed8e6352ba30")
            }
        }
    }

}