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
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.data.LayoutPair

class LinearContainerV(private val orientation: Int, private val pairs: Array<LayoutPair>, val descendantFocusability: Int? = null, private val dataBindingRecv: DataBinding.Binding.Recv? = null, private val click: ((View) -> Unit)? = null, val layoutParams: ViewGroup.LayoutParams? = null): BaseView() {
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
            dataBindingRecv?.setView(it)
        }
    }
}