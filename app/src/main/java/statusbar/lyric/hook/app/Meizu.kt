package statusbar.lyric.hook.app

import statusbar.lyric.hook.BaseHook
import statusbar.lyric.hook.MeiZuStatusBarLyric

class Meizu : BaseHook() {

    override fun hook() {
        super.hook()
        val isQQLite = QQLite().hook("Meizu")
        if (!isQQLite) {
            MeiZuStatusBarLyric.guiseFlyme(true)
        }
    }
}