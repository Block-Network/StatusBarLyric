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
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import de.robv.android.xposed.callbacks.XC_LoadPackage
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.ktx.hookAfterMethod


class Kuwo(private val lpparam: XC_LoadPackage.LoadPackageParam) {
    fun hook(){
        "android.bluetooth.BluetoothAdapter".hookAfterMethod("isEnabled", classLoader = lpparam.classLoader) {
            it.result = true
        }
        "cn.kuwo.mod.playcontrol.RemoteControlLyricMgr".hookAfterMethod("updateLyricText", String::class.java, classLoader = lpparam.classLoader) {
            AppCenter.start(
                AndroidAppHelper.currentApplication(), "d99b2230-6449-4fb3-ba0e-7e47cc470d6d",
                Analytics::class.java, Crashes::class.java
            )
            val str = it.args[0] as String
            LogUtils.e("酷我音乐:$str")
            if (it.args[0] != null && str != "") {
                Utils.sendLyric(AndroidAppHelper.currentApplication(), "" + str, "KuWo")
            }
            it.result = null
        }
    }
}