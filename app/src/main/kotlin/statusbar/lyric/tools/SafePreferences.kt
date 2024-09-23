package statusbar.lyric.tools

import android.content.SharedPreferences

class SafeSharedPreferences {
    var mSP: SharedPreferences? = null

    fun setSP(sp: SharedPreferences) {
        mSP = sp
    }

    fun containsKey(key: String, defValue: Boolean = false): Boolean {
        return if (mSP == null) {
            defValue
        } else {
            mSP!!.all.containsKey(key)
        }
    }

    fun putAny(key: String, any: Any) {
        if (mSP == null) return
        mSP!!.edit().apply {
            when (any) {
                is Boolean -> putBoolean(key, any)
                is String -> putString(key, any)
                is Int -> putInt(key, any)
                is Float -> putFloat(key, any)
                is Long -> putLong(key, any)
            }
            apply()
        }
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return if (mSP == null) {
            defValue
        } else {
            mSP!!.getBoolean(key, defValue)
        }
    }

    fun getInt(key: String, defValue: Int): Int {
        return if (mSP == null) {
            defValue
        } else {
            mSP!!.getInt(key, defValue)
        }
    }

    fun getFloat(key: String, defValue: Float): Float {
        return if (mSP == null) {
            defValue
        } else {
            mSP!!.getFloat(key, defValue)
        }
    }

    fun getLong(key: String, defValue: Long): Long {
        return if (mSP == null) {
            defValue
        } else {
            mSP!!.getLong(key, defValue)
        }
    }

    fun getString(key: String, defValue: String): String {
        return if (mSP == null) {
            defValue
        } else {
            mSP!!.getString(key, defValue) ?: defValue
        }
    }
}