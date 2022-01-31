package cn.fkj233.ui.activity.view


import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.data.Padding
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.sp2px

class TextV(val text: String? = null, private val resId: Int? = null, val textSize: Float? = null, private val textColor: Int? = null, private val padding: Padding? = null, private val dataBindingRecv: DataBinding.Binding.Recv? = null, val onClickListener: (() -> Unit)? = null): BaseView() {

    override fun getType(): BaseView = this

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return TextView(context).also { view ->
            view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text?.let { view.text = it }
            resId?.let { view.setText(it) }
            if (textSize == null)
                view.textSize = sp2px(context, 6f)
            else
                view.textSize = textSize
            textColor?.let { view.setTextColor(it) }
            if (textColor != null)
                view.setTextColor(textColor)
            view.paint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            view.setPadding(0, dp2px(context, 20f), dp2px(context, 5f), dp2px(context, 20f))
            padding?.let { view.setPadding(it.left, it.top, it.right, it.bottom) }
            dataBindingRecv?.setView(view)
        }
    }
}