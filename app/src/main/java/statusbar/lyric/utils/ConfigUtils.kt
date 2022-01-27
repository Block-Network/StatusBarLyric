package statusbar.lyric.utils

import android.annotation.SuppressLint
import android.content.SharedPreferences
import de.robv.android.xposed.XSharedPreferences

class ConfigUtils {
    private var xSP: XSharedPreferences? = null
    private var SP: SharedPreferences? = null
    private var SPEditor: SharedPreferences.Editor? = null

    constructor(xSharedPreferences : XSharedPreferences?){
        xSP = xSharedPreferences
        SP = xSharedPreferences
    }

    @SuppressLint("CommitPrefEdits")
    constructor(sharedPreferences: SharedPreferences){
        SP = sharedPreferences
        SPEditor = sharedPreferences.edit()
    }

    fun update() {
        if (xSP == null) {
            xSP = Utils.getPref("Lyric_Config")
            SP = xSP
            return
        }
        xSP?.reload()
    }

    fun put(key: String?, any: Any) {
        when (any){
            is Int -> SPEditor?.putInt(key, any)
            is String -> SPEditor?.putString(key, any)
            is Boolean -> SPEditor?.putBoolean(key, any)
            is Float -> SPEditor?.putFloat(key, any)
        }
        SPEditor?.apply()
    }

    fun optInt(key: String, i: Int): Int {
        if (SP == null) {
            return i
        }
        return SP!!.getInt(key, i)
    }

    fun optBoolean(key: String, bool: Boolean): Boolean {
        if (SP == null) {
            return bool
        }
        return SP!!.getBoolean(key, bool)
    }

    fun optString(key: String, str: String): String {
        if (SP == null) {
            return str
        }
        return SP!!.getString(key, str).toString()
    }

    fun optFloat(key: String, f: Float): Float {
        if (SP == null) {
            return f
        }
        return SP!!.getFloat(key, f)
    }

    fun clearConfig() {
        SPEditor?.clear()?.apply()
    }
}