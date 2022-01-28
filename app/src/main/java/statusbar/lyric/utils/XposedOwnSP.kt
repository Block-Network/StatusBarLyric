package statusbar.lyric.utils

import statusbar.lyric.config.Config
import statusbar.lyric.config.IconConfig

object XposedOwnSP {
    @JvmStatic
    val config: Config by lazy { Config(Utils.getPref("Lyric_Config")) }
    @JvmStatic
    val iconConfig: IconConfig by lazy { IconConfig(Utils.getPref("Icon_Config")) }
}