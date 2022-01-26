package cn.fkj233.xposed.statusbarlyric.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import de.robv.android.xposed.XposedBridge
import cn.fkj233.xposed.statusbarlyric.utils.XposedOwnSP.config

object LogUtils {
    private const val maxLength = 4000
    private val handler by lazy{ Handler(Looper.getMainLooper()) }
    private const val TAG = "StatusBarLyric"

    @JvmStatic
    fun toast(context: Context?, msg: String) {
        handler.post { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
    }

    @JvmStatic
    fun log(obj: Any?, toXposed: Boolean = false, toLogd: Boolean = false) {
        if (!config.getDebug()) return
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
