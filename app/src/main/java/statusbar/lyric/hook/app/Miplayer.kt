package statusbar.lyric.hook.app

import android.content.Context
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.ktx.findClass
import statusbar.lyric.utils.ktx.hookAfterConstructor
import statusbar.lyric.utils.ktx.hookBeforeMethod
import statusbar.lyric.utils.ktx.setReturnConstant
import statusbar.lyric.utils.ktx.isNull

class Miplayer : BaseHook() {
    private val songInfo = "com.tencent.qqmusic.core.song.SongInfo".findClass()
    lateinit var context: Context

    override fun hook() {
        super.hook()
        "com.tencent.qqmusiccommon.util.music.RemoteLyricController".setReturnConstant("BluetoothA2DPConnected", result = true)
        "com.tencent.qqmusiccommon.util.music.RemoteControlManager".hookAfterConstructor(Context::class.java) {
            context = it.args[0] as Context
        }
        "com.tencent.qqmusiccommon.util.music.RemoteControlManager".hookBeforeMethod("updataMetaData", songInfo, String::class.java) {
            val lyric = if (it.args[1].isNull()) return@hookBeforeMethod else it.args[1].toString()
            it.args[1] = null // 去除妙播显示歌词
            LogUtils.e("小米音乐: $lyric")
            Utils.sendLyric(context, lyric, "MiPlayer")
        }
    }
}