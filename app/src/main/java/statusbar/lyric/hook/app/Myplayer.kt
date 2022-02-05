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

package statusbar.lyric.hook.app

import android.app.AndroidAppHelper
import android.content.Context
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import de.robv.android.xposed.callbacks.XC_LoadPackage
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.ktx.hookAfterMethod


class Myplayer(private val lpparam: XC_LoadPackage.LoadPackageParam) {
    fun hook(){
        "remix.myplayer.util.p".hookAfterMethod("o", Context::class.java, classLoader = lpparam.classLoader) {
            it.result = true
        }
        "remix.myplayer.service.MusicService".hookAfterMethod("n1", String::class.java, classLoader = lpparam.classLoader) {
            val context: Context = it.thisObject as Context
            AppCenter.start(
                AndroidAppHelper.currentApplication(), "83d39340-9e06-406d-85f3-c663aed8e4ea",
                Analytics::class.java, Crashes::class.java
            )
            LogUtils.e("myplayer: " + it.args[0].toString())
            Utils.sendLyric(context, it.args[0].toString(), "Myplayer")
        }
    }
}