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

package statusbar.lyric.service

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import statusbar.lyric.R
import statusbar.lyric.config.Config
import statusbar.lyric.tools.Tools

class QuickTitleService : TileService() {
    lateinit var config: Config
    override fun onCreate() {
        super.onCreate()
        config = Tools.getSP(baseContext, "Config")?.let { Config(it) }!!
    }

    private lateinit var tile: Tile

    override fun onClick() {
        super.onClick()
        config.masterSwitch = !config.masterSwitch
        set(config)
    }

    fun set(config: Config) {
        tile.apply {
            icon = Icon.createWithResource(baseContext, R.drawable.ic_notification)
            label = getString(R.string.QuickTitle)
            contentDescription = getString(R.string.QuickTitle)
            state = if (config.masterSwitch) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        }.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        tile = qsTile
        set(config)
    }
}

