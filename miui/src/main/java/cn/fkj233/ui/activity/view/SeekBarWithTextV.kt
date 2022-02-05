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
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.OwnSP
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.data.LayoutPair
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.sp2px

class SeekBarWithTextV(val key: String = "", val min: Int, val max: Int, val defaultProgress: Int = 0, val divide: Int = 1, private val dataBindingRecv: DataBinding.Binding.Recv? = null, private val dataBindingSend: DataBinding.Binding.Send? = null, val callBacks: ((Int, TextView) -> Unit)? = null): BaseView() {

    override var outside = true

    override fun getType(): BaseView = this

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        val minText = TextV(min.toString(), textSize = sp2px(context, 4.5f)).create(context, callBacks)
        val maxText = TextV(max.toString(), textSize = sp2px(context, 4.5f)).create(context, callBacks)
        val mutableText = TextV("", textSize = sp2px(context, 4.5f)).create(context, callBacks)
        val seekBar = SeekBar(context).also { view ->
            view.thumb = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                view.maxHeight = dp2px(context, 35f)
                view.minHeight = dp2px(context, 35f)
            }
            view.isIndeterminate = false
            view.progressDrawable = context.getDrawable(R.drawable.seekbar_progress_drawable)
            view.indeterminateDrawable = context.getDrawable(R.color.colorAccent)
            view.min = min
            view.max = max
            view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            if (OwnSP.ownSP.all.containsKey(key)) {
                OwnSP.ownSP.getInt(key, defaultProgress).let {
                    view.progress = it
                    (mutableText as TextView).text = it.toString()
                }
            } else {
                (mutableText as TextView).text = defaultProgress.toString()
                    OwnSP.ownSP.edit().run {
                    putInt(key, defaultProgress)
                    apply()
                }
            }
            view.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    callBacks?.let { it() }
                    dataBindingSend?.send(p1)
                    OwnSP.ownSP.edit().run {
                        (mutableText as TextView).text = p1.toString()
                        putInt(key, p1 / divide)
                        apply()
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }
        return LinearContainerV(
            LinearContainerV.VERTICAL,
            arrayOf(
                LayoutPair(seekBar, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)),
                LayoutPair(LinearContainerV(LinearContainerV.HORIZONTAL, arrayOf(LayoutPair(minText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)), LayoutPair(mutableText.also { it.textAlignment = TextView.TEXT_ALIGNMENT_CENTER }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)), LayoutPair(maxText.also { it.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)))).create(context, callBacks).also { it.setPadding(
                    dp2px(context, 25f), 0, dp2px(context, 25f), 0) }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
            )
        ).create(context, callBacks).also {
            dataBindingRecv?.setView(it)
        }
    }
}