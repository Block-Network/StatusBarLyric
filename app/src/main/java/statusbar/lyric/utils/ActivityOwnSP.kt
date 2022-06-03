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

package statusbar.lyric.utils

import android.annotation.SuppressLint
import android.app.Activity
import statusbar.lyric.config.Config

@SuppressLint("StaticFieldLeak")
object ActivityOwnSP {
    lateinit var activity: Activity
    private val ownSP by lazy { Utils.getSP(activity, "Lyric_Config")!! }
    val ownSPConfig by lazy { Config(ownSP) }
    private val ownEditor by lazy { ownSP.edit() }

    const val version = 1

    fun set(key: String, any: Any) {
        when (any) {
            is Int -> ownEditor.putInt(key, any)
            is Float -> ownEditor.putFloat(key, any)
            is String -> ownEditor.putString(key, any)
            is Boolean -> ownEditor.putBoolean(key, any)
            is Long -> ownEditor.putLong(key, any)
        }
        ownEditor.apply()
    }

    fun updateConfigVer() {
        if (ownSP.getInt("ver", 0) < version) {
            set("ver", version)
            runCatching { set("LyricViewPosition", ownSP.getString("LyricViewPosition", "first") == "first") }
            runCatching { set("CustomizeViewPosition", ownSP.getString("CustomizeViewPosition", "first") == "first") }
        }
    }
}
