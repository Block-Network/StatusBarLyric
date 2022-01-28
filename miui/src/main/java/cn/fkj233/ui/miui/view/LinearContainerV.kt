package cn.fkj233.ui.miui.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import cn.fkj233.ui.miui.data.LayoutPair

class LinearContainerV(val orientation: Int, val pairs: Array<LayoutPair>): BaseView() {
    companion object {
        const val VERTICAL = LinearLayout.VERTICAL
        const val HORIZONTAL = LinearLayout.HORIZONTAL
    }

    override fun getType(): BaseView {
        return this
    }

    override fun create(context: Context): View {
        return LinearLayout(context).also {
            it.orientation = orientation
            for (pair in pairs) {
                it.addView(pair.view, pair.layoutParams)
            }
        }
    }
}