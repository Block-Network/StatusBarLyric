package statusbar.lyric.hook.app

import android.app.Activity
import android.content.Context
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.ktx.findClassOrNull
import statusbar.lyric.utils.ktx.hookAfterMethod
import android.app.AndroidAppHelper
import android.view.View
import android.view.ViewGroup
import statusbar.lyric.utils.ktx.hookBeforeMethod
import java.util.ArrayList

class LXMusic: BaseHook() {

    private val context: Context
        get() = AndroidAppHelper.currentApplication()

    override fun hook() {
        val lyricModuleClass = "cn.toside.music.mobile.lyric.LyricModule".findClassOrNull() ?: run {
            LogUtils.e("Can't find class LyricModule!")
            return
        }
        val lyricField = lyricModuleClass.declaredFields.firstOrNull { it.name == "lyric" } ?: run {
            LogUtils.e("Can't find field lyric!")
            return
        }
        val lyricViewField = lyricField.type.declaredFields.firstOrNull { it.type.superclass == Activity::class.java } ?: run {
            LogUtils.e("Can't find field lyricView!")
            return
        }
        val lyricMethod = lyricViewField.type.declaredMethods.firstOrNull {
            it.parameterCount == 2 && it.parameterTypes[0] == String::class.java && it.parameterTypes[1] == ArrayList::class.java
        } ?: run {
            LogUtils.e("Can't find method setLyric!")
            return
        }
        lyricMethod.hookAfterMethod {
            val lyric = it.args[0] as String
            runCatching { Utils.sendLyric(context, lyric, null, true, "cn.toside.music.mobile") }
        }
        "android.view.WindowManagerImpl".hookBeforeMethod("addView", View::class.java, ViewGroup.LayoutParams::class.java) {
            (it.args[0] as View).visibility = View.GONE
        }
    }
}