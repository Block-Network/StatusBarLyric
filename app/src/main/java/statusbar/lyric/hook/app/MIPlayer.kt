package statusbar.lyric.hook.app

import android.content.Context
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.ktx.findClass
import statusbar.lyric.utils.ktx.hookAfterConstructor
import statusbar.lyric.utils.ktx.hookBeforeMethod
import statusbar.lyric.utils.ktx.setReturnConstant

class MIPlayer(val lpparam: LoadPackageParam): BaseHook(lpparam) {
    private val songInfo = "com.tencent.qqmusic.core.song.SongInfo".findClass(lpparam.classLoader)
    lateinit var context: Context

    override fun hook() {
        "com.tencent.qqmusiccommon.util.music.RemoteLyricController".setReturnConstant("BluetoothA2DPConnected", classLoader = lpparam.classLoader, result = true)
        "com.tencent.qqmusiccommon.util.music.RemoteControlManager".hookAfterConstructor(Context::class.java, classLoader = lpparam.classLoader) {
            context = it.args[0] as Context
        }
        "com.tencent.qqmusiccommon.util.music.RemoteControlManager".hookBeforeMethod("updataMetaData", songInfo, String::class.java, classLoader = lpparam.classLoader) {
            val lyric = if (it.args[1] == null) return@hookBeforeMethod else it.args[1].toString()
            it.args[1] = null // 去除妙播显示歌词
            LogUtils.e("小米音乐: $lyric")
            Utils.sendLyric(context, lyric, "MIPlayer")
        }
    }
}