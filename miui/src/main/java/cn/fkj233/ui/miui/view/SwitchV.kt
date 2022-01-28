package cn.fkj233.ui.miui.view


import android.content.Context
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import cn.fkj233.miui.R
import cn.fkj233.ui.miui.OwnSP

class SwitchV(val key: String, val customOnCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null): BaseView() {

    override fun getType(): BaseView {
        return this
    }

    override fun create(context: Context): View {
        return Switch(context).also {
            it.background = null
            it.setThumbResource(R.drawable.switch_thumb)
            it.setTrackResource(R.drawable.switch_track)
            it.isChecked = OwnSP.ownSP.getBoolean(key, false)
            it.setOnCheckedChangeListener { compoundButton, b ->
                customOnCheckedChangeListener?.onCheckedChanged(compoundButton, b)
                OwnSP.ownSP.edit().run {
                    putBoolean(key, b)
                    apply()
                }
            }
        }
    }
}