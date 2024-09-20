package statusbar.lyric.hook

abstract class BaseHook {
    var isInit: Boolean = false
    abstract fun init()
}