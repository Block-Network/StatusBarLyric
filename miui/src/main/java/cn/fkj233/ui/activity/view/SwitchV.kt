package cn.fkj233.ui.activity.view


import android.content.Context
import android.view.View
import android.widget.Switch
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.OwnSP
import cn.fkj233.ui.activity.data.DataBinding

class SwitchV(val key: String, private val defValue: Boolean = false, private val dataBindingRecv: DataBinding.Binding.Recv? = null, private val dataBindingSend: DataBinding.Binding.Send? = null, private val customOnCheckedChangeListener: ((Boolean) -> Unit)? = null): BaseView() {

    override fun getType(): BaseView = this

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return Switch(context).also {
            dataBindingRecv?.let { dataBinding ->
                dataBinding.setView(it)
            }
            it.background = null
            it.setThumbResource(R.drawable.switch_thumb)
            it.setTrackResource(R.drawable.switch_track)
            it.isChecked = OwnSP.ownSP.getBoolean(key, defValue)
            it.setOnCheckedChangeListener { _, b ->
                dataBindingSend?.let { send ->
                    send.send(b)
                }
                callBacks?.let { it1 -> it1() }
                customOnCheckedChangeListener?.let { it(b) }
                OwnSP.ownSP.edit().run {
                    putBoolean(key, b)
                    apply()
                }
            }
        }
    }
}