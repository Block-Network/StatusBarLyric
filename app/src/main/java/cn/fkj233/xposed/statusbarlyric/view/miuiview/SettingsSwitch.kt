package cn.fkj233.xposed.statusbarlyric.view.miuiview

import android.content.Context
import android.util.AttributeSet
import android.widget.Switch
import cn.fkj233.xposed.statusbarlyric.utils.ActivityOwnSP

class SettingsSwitch(context: Context, attributeSet: AttributeSet): Switch(context, attributeSet) {

    private val editor by lazy { ActivityOwnSP.ownSP.edit() }
    var customCheckedChangeListener: OnCheckedChangeListener? = null

    var key = ""
        set(value) {
            isChecked = ActivityOwnSP.ownSP.getBoolean(value, false)
            setOnCheckedChangeListener { a, b ->
                customCheckedChangeListener?.onCheckedChanged(a, b)
                editor.putBoolean(value, b)
                editor.apply()
            }
        }
}