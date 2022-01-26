package cn.fkj233.xposed.statusbarlyric.view.data

import android.widget.TextView

data class SeekBar(
    val min: Int? = null,
    val max: Int? = null,
    val progress: Int? = null,
    val callBacks: ((Int, TextView) -> Unit)? = null
)