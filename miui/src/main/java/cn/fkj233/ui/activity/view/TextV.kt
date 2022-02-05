/*
 * BlockMIUI
 * Copyright (C) 2022 fkj@fkj233.cn
 * https://github.com/577fkj/BlockMIUI
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/BlockMIUI/blob/main/LICENSE>.
 */

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