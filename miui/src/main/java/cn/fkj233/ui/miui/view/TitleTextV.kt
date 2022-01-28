package cn.fkj233.ui.miui.view

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import cn.fkj233.ui.miui.sp2px

class TitleTextV(val text: String? = null, val resId: Int? = null, val onClickListener: (() -> Unit)? = null): BaseView() {

    override fun getType(): BaseView {
        return this
    }

    override fun create(context: Context): View {
        return TextV(text, resId, sp2px(context,4.5f), onClickListener = onClickListener).create(context).also {
            (it as TextView).setTextColor(Color.parseColor("#9399b3"))
        }
    }

}