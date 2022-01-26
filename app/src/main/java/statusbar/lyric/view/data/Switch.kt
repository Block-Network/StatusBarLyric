package statusbar.lyric.view.data

import android.widget.CompoundButton

data class Switch(
    val key: String,
    val onCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
)