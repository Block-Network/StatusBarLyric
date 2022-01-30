package cn.fkj233.ui.activity.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.dp2px

class LineV(private val dataBinding: DataBinding? = null): BaseView() {

    override fun getType(): BaseView {
        return this
    }

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return View(context).also {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(context, 0.8f))
            layoutParams.setMargins(0, dp2px(context, 2f), 0, dp2px(context, 2f))
            it.layoutParams = layoutParams
            it.setBackgroundColor(context.resources.getColor(R.color.line, null))
            dataBinding?.add(dataBinding.Recv(it))
        }
    }

}