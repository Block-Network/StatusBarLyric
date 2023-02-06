package statusbar.lyric.hook.app

import statusbar.lyric.hook.BaseHook
import statusbar.lyric.hook.MeiZuStatusBarLyric
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.ktx.findClass
import statusbar.lyric.utils.ktx.isNull

class Meizu : BaseHook() {
    private val songInfo = "com.tencent.qqmusic.core.song.SongInfo".findClass()


    override fun hook() {
        super.hook()
        LogUtils.e(songInfo.isNull())
        if (songInfo.isNull()) {
            MeiZuStatusBarLyric.guiseFlyme(true)
        } else {
            QQLite().hook("Meizu")
        }
    }
}