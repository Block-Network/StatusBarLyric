package cn.fkj233.xposed.statusbarlyric.service

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import cn.fkj233.xposed.statusbarlyric.R
import cn.fkj233.xposed.statusbarlyric.config.Config
import cn.fkj233.xposed.statusbarlyric.utils.Utils

class QuickTitleService: TileService() {
    private lateinit var tile: Tile

    override fun onClick() {
        super.onClick()
        val config: Config? = Utils.getSP(baseContext, "Lyric_Config")?.let { Config(it) }
        config?.setLyricService(!config.getLyricService())
        config?.let { set(it) }
    }

    fun set(config: Config) {
        tile.icon = Icon.createWithResource(this, R.drawable.title_icon)
        tile.label = getString(R.string.QuickTitle)
        tile.contentDescription = getString(R.string.QuickTitle)
        tile.state = if (config.getLyricService()) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        val config: Config? = Utils.getSP(baseContext, "Lyric_Config")?.let { Config(it) }
        tile = qsTile
        config?.let { set(it) }
    }
}

