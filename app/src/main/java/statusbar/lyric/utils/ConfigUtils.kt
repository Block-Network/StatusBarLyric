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
import android.content.SharedPreferences
import de.robv.android.xposed.XSharedPreferences
import statusbar.lyric.utils.Utils.isNull

class ConfigUtils {
    private var xSP: XSharedPreferences? = null
    private var mSP: SharedPreferences? = null
    private var mSPEditor: SharedPreferences.Editor? = null

    constructor(xSharedPreferences: XSharedPreferences?) {
        xSP = xSharedPreferences
        mSP = xSharedPreferences
    }

    @SuppressLint("CommitPrefEdits")
    constructor(sharedPreferences: SharedPreferences) {
        mSP = sharedPreferences
        mSPEditor = sharedPreferences.edit()
    }

    fun update() {
        if (xSP.isNull()) {
            xSP = Utils.getPref("Lyric_Config")
            mSP = xSP
            return
        }
        xSP?.reload()
    }

    fun put(key: String?, any: Any) {
        when (any) {
            is Int -> mSPEditor?.putInt(key, any)
            is String -> mSPEditor?.putString(key, any)
            is Boolean -> mSPEditor?.putBoolean(key, any)
            is Float -> mSPEditor?.putFloat(key, any)
        }
        mSPEditor?.apply()
    }

    fun optInt(key: String, i: Int): Int {
        if (mSP.isNull()) {
            return i
        }
        return mSP!!.getInt(key, i)
    }

    fun optBoolean(key: String, bool: Boolean): Boolean {
        if (mSP.isNull()) {
            return bool
        }
        return mSP!!.getBoolean(key, bool)
    }

    fun optString(key: String, str: String): String {
        if (mSP.isNull()) {
            return str
        }
        return mSP!!.getString(key, str).toString()
    }

    fun optFloat(key: String, f: Float): Float {
        if (mSP.isNull()) {
            return f
        }
        return mSP!!.getFloat(key, f)
    }

    fun clearConfig() {
        mSPEditor?.clear()?.apply()
    }
}