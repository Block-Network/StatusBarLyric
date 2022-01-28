package cn.fkj233.ui.miui.view


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.fkj233.miui.R
import cn.fkj233.ui.miui.data.Padding
import cn.fkj233.ui.miui.dp2px
import cn.fkj233.ui.miui.sp2px

class TextV(val text: String? = null, val resId: Int? = null, val textSize: Float? = null, val padding: Padding? = null, val onClickListener: (() -> Unit)? = null): BaseView() {

    private var myView: View? = null

    fun getMyView(context: Context): View = myView ?: innerCreate(context)

    override fun getType(): BaseView {
        return this
    }

    private fun innerCreate(context: Context): View {
        return TextView(context).also { view ->
            view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.weight = 1f }
            text?.let { view.text = it }
            resId?.let { view.setText(it) }
            if (textSize == null) {
                view.textSize = sp2px(context, 7.0f)
            } else {
                view.textSize = textSize
            }
            view.setPadding(0, dp2px(context, 15f), dp2px(context, 5f), dp2px(context, 15f))
            padding?.let { view.setPadding(it.left, it.top, it.right, it.bottom) }
            myView = view
        }
    }

    override fun create(context: Context): View {
        return getMyView(context)
    }
}