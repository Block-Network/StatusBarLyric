package cn.fkj233.ui.activity.view


import android.content.Context
import android.view.View
import android.widget.Switch
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.OwnSP
import cn.fkj233.ui.activity.data.DataBinding

class SwitchV(val key: String, private val defValue: Boolean = false, private val dataBinding: DataBinding? = null, private val send: Boolean = false, private val customOnCheckedChangeListener: ((Boolean) -> Unit)? = null): BaseView() {

    override fun getType(): BaseView = this

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return Switch(context).also {
            dataBinding?.let { dataBinding ->
                if (!send) {
                    dataBinding.add(dataBinding.Recv(it))
                }
            }
            it.background = null
            it.setThumbResource(R.drawable.switch_thumb)
            it.setTrackResource(R.drawable.switch_track)
            it.isChecked = OwnSP.ownSP.getBoolean(key, defValue)
            it.setOnCheckedChangeListener { _, b ->
                dataBinding?.let {
                    if (send) {
                        dataBinding.send(b)
                    }
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