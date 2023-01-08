package statusbar.lyric.utils.amutils

class Lyric {
    private var lyricInfoMap: MutableMap<Int, LyricInfo> = HashMap()
    private var points: MutableList<Int> = ArrayList()

    fun addInfo(begin: Int, end: Int, str: String?) {
        points.add(begin)
        lyricInfoMap[begin] = LyricInfo(begin, end, str!!)
    }

    fun getLyricByPosition(pos: Long): LyricInfo? {
        if (points.size == 0) {
            return null
        }
        var bPos = 0
        var i = 0
        val len = points.size
        while (i < len) {
            if (pos > points[i]) {
                bPos = points[i]
            }
            i++
        }
        return lyricInfoMap[bPos]
    }

    fun clean() {
        points.clear()
        lyricInfoMap.clear()
    }
}