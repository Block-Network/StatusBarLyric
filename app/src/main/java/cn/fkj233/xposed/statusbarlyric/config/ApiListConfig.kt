package cn.fkj233.xposed.statusbarlyric.config

import android.annotation.SuppressLint
import android.content.SharedPreferences
import de.robv.android.xposed.XSharedPreferences
import cn.fkj233.xposed.statusbarlyric.utils.ConfigUtils


@SuppressLint("LongLogTag")
class ApiListConfig {
    var config: ConfigUtils

    constructor(xSharedPreferences: XSharedPreferences?) {
        config = ConfigUtils(xSharedPreferences)
    }

    constructor(sharedPreferences: SharedPreferences?) {
        config = ConfigUtils(sharedPreferences!!)
    }

    fun hasEnable(packName: String): Boolean? {
        return config.optBoolean(packName, false)
    }

    fun setEnable(packName: String?, isEnable: Boolean) {
        config.put(packName, isEnable)
    }

    fun clear() {
        config.clearConfig()
    }
}