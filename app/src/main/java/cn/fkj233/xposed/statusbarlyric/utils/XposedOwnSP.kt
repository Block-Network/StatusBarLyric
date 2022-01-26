package cn.fkj233.xposed.statusbarlyric.utils

import cn.fkj233.xposed.statusbarlyric.config.ApiListConfig
import cn.fkj233.xposed.statusbarlyric.config.Config
import cn.fkj233.xposed.statusbarlyric.config.IconConfig

object XposedOwnSP {
    @JvmStatic
    val config: Config by lazy { Config(Utils.getPref("Lyric_Config")) }
    @JvmStatic
    val iconConfig: IconConfig by lazy { IconConfig(Utils.getPref("Icon_Config")) }
    @JvmStatic
    val apiList: ApiListConfig by lazy { ApiListConfig(Utils.getPref("AppList_Config")) }
}