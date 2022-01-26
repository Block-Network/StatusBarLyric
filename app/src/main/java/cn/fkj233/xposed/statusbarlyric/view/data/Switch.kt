package cn.fkj233.xposed.statusbarlyric.view.data

import android.widget.CompoundButton

data class Switch(
    val key: String,
    val defValue: Boolean = false,
    val onCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
)