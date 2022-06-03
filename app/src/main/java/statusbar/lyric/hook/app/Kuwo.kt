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
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.ktx.isNotNull
import statusbar.lyric.utils.ktx.hookAfterMethod


class Kuwo : BaseHook() {
    override fun hook() {
        super.hook()
        "android.bluetooth.BluetoothAdapter".hookAfterMethod("isEnabled") {
            it.result = true
        }
        "cn.kuwo.mod.playcontrol.RemoteControlLyricMgr".hookAfterMethod("updateLyricText", String::class.java) {
            val str = it.args[0] as String
            LogUtils.e("酷我音乐:$str")
            if (it.args[0].isNotNull() && str != "") {
                Utils.sendLyric(AndroidAppHelper.currentApplication(), "" + str, "KuWo")
            }
            it.result = null
        }
    }
}