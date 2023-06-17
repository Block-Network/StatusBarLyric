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

package statusbar.lyric.tools

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import de.robv.android.xposed.XposedBridge

object LogTools {
    private const val maxLength = 4000
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    const val TAG = "StatusBarLyric"

    @JvmStatic
    fun toast(context: Context?, msg: String) {
        handler.post { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
    }

    @JvmStatic
    fun log(obj: Any?, toXposed: Boolean = false, toLogd: Boolean = false) {
        val content = if (obj is Throwable) Log.getStackTraceString(obj) else obj.toString()
        if (content.length > maxLength) {
            val chunkCount = content.length / maxLength
            for (i in 0..chunkCount) {
                val max = 4000 * (i + 1)
                if (max >= content.length) {
                    if (toXposed) XposedBridge.log("$TAG: " + content.substring(maxLength * i))
                    if (toLogd) Log.d(TAG, content.substring(maxLength * i))
                } else {
                    if (toXposed) XposedBridge.log("$TAG: " + content.substring(maxLength * i, max))
                    if (toLogd) Log.d(TAG, content.substring(maxLength * i, max))
                }
            }
        } else {
            if (toXposed) XposedBridge.log("$TAG: $content")
            if (toLogd) Log.d(TAG, content)
        }
    }

    fun e(obj: Any?) {
        log(obj, toXposed = true)
    }

    fun d(obj: Any?) {
        log(obj, toLogd = true)
    }

}
