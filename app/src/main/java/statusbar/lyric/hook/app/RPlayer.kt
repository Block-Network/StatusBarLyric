package statusbar.lyric.hook.app

import android.content.Context
import android.content.Intent
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.ktx.findClassOrNull
import statusbar.lyric.utils.ktx.hookAfterMethod

class RPlayer: BaseHook() {

    override fun hook() {
        if ("com.stub.StubApp".findClassOrNull() == null) {
            LogUtils.e("Can't find 360 Entry!")
            return
        }
        "com.stub.StubApp".hookAfterMethod("attachBaseContext", Context::class.java) {
            LogUtils.d("hooking 360 Entry")
            val context = it.args[0] as Context
            val classLoader = context.classLoader
            val apiClass = "StatusBarLyric.API.StatusBarLyric".findClassOrNull(classLoader) ?: run {
                LogUtils.e("Can't find API class!")
                return@hookAfterMethod
            }
            apiClass.hookAfterMethod("hasEnable") {
                it.result = true
            }
            apiClass.hookAfterMethod("sendLyric", Context::class.java, String::class.java, String::class.java, String::class.java, Boolean::class.javaPrimitiveType) {
                LogUtils.e("API: " + it.args[1])
                Utils.sendLyric(it.args[0] as Context, it.args[1] as String, it.args[2] as String, it.args[4] as Boolean, it.args[3] as String)
            }
            apiClass.hookAfterMethod("stopLyric", Context::class.java) {
                (it.args[0] as Context).sendBroadcast(Intent().apply {
                    action = "Lyric_Server"
                    putExtra("Lyric_Type", "app_stop")
                })
            }
        }
    }
}