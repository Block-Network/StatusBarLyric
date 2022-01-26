package cn.fkj233.xposed.statusbarlyric.view.data

import android.widget.CompoundButton

data class Switch(
    val key: String,
    val onCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
)