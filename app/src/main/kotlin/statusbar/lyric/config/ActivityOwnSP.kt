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

package statusbar.lyric.config

import android.annotation.SuppressLint
import statusbar.lyric.BuildConfig
import statusbar.lyric.MainActivity.Companion.context
import statusbar.lyric.config.Config.Companion.CONFIG_NAME
import statusbar.lyric.tools.Tools

@SuppressLint("StaticFieldLeak")
object ActivityOwnSP {
    val ownSP by lazy { Tools.getSP(context, CONFIG_NAME)!! }
    val config by lazy { Config(ownSP) }
    private val ownEditor by lazy { ownSP.edit() }

    private fun set(key: String, any: Any) {
        when (any) {
            is Int -> ownEditor.putInt(key, any)
            is Float -> ownEditor.putFloat(key, any)
            is String -> ownEditor.putString(key, any)
            is Boolean -> ownEditor.putBoolean(key, any)
            is Long -> ownEditor.putLong(key, any)
        }
        ownEditor.apply()
    }

    private fun remove(key: String) {
        ownEditor.remove(key).apply()
    }

    fun updateConfigVer() {
        if (ownSP.getInt("ver", 0) < BuildConfig.COMPOSE_CONFIG_VERSION) {

        }
    }
}
