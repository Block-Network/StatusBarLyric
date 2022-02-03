package cn.fkj233.ui.activity.view

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.data.LayoutPair
import cn.fkj233.ui.activity.data.MIUIPopupData
import cn.fkj233.ui.activity.dp2px

class TextWithSpinnerV(private val textV: TextV, val spinner: Spinner, private val dataBindingRecv: DataBinding.Binding.Recv? = null): BaseView() {

    override fun getType(): BaseView = this

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return LinearContainerV(
            LinearContainerV.HORIZONTAL,
            arrayOf(
                LayoutPair(textV.create(context, callBacks), LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)),
                LayoutPair(spinner.create(context, callBacks), LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.gravity = Gravity.CENTER_VERTICAL
                    it.setMargins(0, dp2px(context, 7f), dp2px(context, 10f), 0)
                })
            ),
            descendantFocusability = LinearContainerV.FOCUS_BLOCK_DESCENDANTS,
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        ).create(context, callBacks).also {
            dataBindingRecv?.setView(it)
        }
    }
}