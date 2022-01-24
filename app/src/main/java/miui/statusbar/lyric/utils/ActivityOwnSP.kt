package miui.statusbar.lyric.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import miui.statusbar.lyric.config.Config

@SuppressLint("StaticFieldLeak")
object ActivityOwnSP {
    lateinit var activity: Activity
    val ownSP by lazy { Utils.getSP(activity, "Lyric_Config")!! }
    val ownSPConfig by lazy { Config(ownSP) }
    private val ownEditor by lazy { ownSP.edit() }

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

    fun remove(key: String) {
        ownEditor.remove(key)
        ownEditor.apply()
    }

    fun clear() {
        ownEditor.clear()
        ownEditor.apply()
    }
}
