package statusbar.lyric.view.data

data class Item(
    val text: Text? = null,
    val switch: Switch? = null,
    val seekBar: SeekBar? = null,
    val spinner: Spinner? = null,
    val author: Author? = null,
    val line: Boolean = false
)