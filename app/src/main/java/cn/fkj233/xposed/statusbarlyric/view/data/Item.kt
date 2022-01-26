package cn.fkj233.xposed.statusbarlyric.view.data

data class Item(
    val text: Text? = null,
    val switch: Switch? = null,
    val seekBar: SeekBar? = null,
    val spinner: Spinner? = null,
    val line: Boolean = false
)