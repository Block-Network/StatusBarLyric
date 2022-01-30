package cn.fkj233.ui.activity.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.data.LayoutPair

class LinearContainerV(private val orientation: Int, private val pairs: Array<LayoutPair>, val descendantFocusability: Int? = null, private val dataBinding: DataBinding? = null, private val click: ((View) -> Unit)? = null, val layoutParams: ViewGroup.LayoutParams? = null): BaseView() {
    companion object {
        const val VERTICAL = LinearLayout.VERTICAL
        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val FOCUS_BLOCK_DESCENDANTS = ViewGroup.FOCUS_BLOCK_DESCENDANTS
    }

    override fun getType(): BaseView {
        return this
    }

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return LinearLayout(context).also {
            it.orientation = orientation
            layoutParams?.let { it1 -> it.layoutParams = it1 }
            descendantFocusability?.let { it1 -> it.descendantFocusability = it1 }
            click?.let { it1 -> it.setOnClickListener { it2 -> it1(it2) } }
            for (pair in pairs) {
                it.addView(pair.view, pair.layoutParams)
            }
            dataBinding?.add(dataBinding.Recv(it))
        }
    }
}