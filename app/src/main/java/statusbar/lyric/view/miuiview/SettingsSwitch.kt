package statusbar.lyric.view.miuiview

import android.content.Context
import android.util.AttributeSet
import android.widget.Switch
import statusbar.lyric.utils.ActivityOwnSP

class SettingsSwitch(context: Context, attributeSet: AttributeSet): Switch(context, attributeSet) {

    private val editor by lazy { ActivityOwnSP.ownSP.edit() }
    var customCheckedChangeListener: OnCheckedChangeListener? = null

    var defValue = false
    var key = ""
        set(value) {
            isChecked = ActivityOwnSP.ownSP.getBoolean(value, defValue)
            setOnCheckedChangeListener { a, b ->
                customCheckedChangeListener?.onCheckedChanged(a, b)
                editor.putBoolean(value, b)
                editor.apply()
            }
        }
}