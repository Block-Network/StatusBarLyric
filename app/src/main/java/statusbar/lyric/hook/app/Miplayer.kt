package statusbar.lyric.hook.app

import android.content.Context
import statusbar.lyric.hook.BaseHook

class Miplayer : BaseHook() {
    lateinit var context: Context

    override fun hook() {
        super.hook()
        QQLite().hook("MiPlayer")
    }
}