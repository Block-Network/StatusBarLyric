package cn.fkj233.ui.miui.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import cn.fkj233.miui.R
import cn.fkj233.ui.miui.dp2px

class LineV: BaseView() {

    override fun getType(): BaseView {
        return this
    }

    override fun create(context: Context): View {
        return View(context).also {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(context, 0.8f))
            layoutParams.setMargins(0, dp2px(context, 18f), 0, dp2px(context, 18f))
            it.layoutParams = layoutParams
            it.setBackgroundColor(context.resources.getColor(R.color.line, null))
        }
    }

}