package cn.fkj233.ui.activity.view

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.sp2px

class TitleTextV(val text: String? = null, private val resId: Int? = null, private val dataBinding: DataBinding? = null, private val onClickListener: (() -> Unit)? = null): BaseView() {

    override fun getType(): BaseView {
        return this
    }

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return TextV(text, resId, sp2px(context,4.5f), onClickListener = onClickListener).create(context, callBacks).also {
            (it as TextView).setTextColor(Color.parseColor("#9399b3"))
            dataBinding?.add(dataBinding.Recv(it))
        }
    }

}