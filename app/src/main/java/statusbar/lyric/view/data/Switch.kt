package statusbar.lyric.view.data

import android.widget.CompoundButton

data class Switch(
    val key: String,
    val defValue: Boolean = false,
    val onCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
)