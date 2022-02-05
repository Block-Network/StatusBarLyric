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
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.data.MIUIPopupData
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.sp2px


class MIUIPopup(private val context: Context, view: View, private val currentValue: String, private val dataBacks: (String) -> Unit, private val arrayList: ArrayList<MIUIPopupData>): ListPopupWindow(context) {

    /**
     * 创建背景颜色
     *
     * @param color       填充色
     * @param strokeColor 线条颜色
     * @param strokeWidth 线条宽度  单位px
     * @param radius      角度  px,长度为4,分别表示左上,右上,右下,左下的角度
     */
    fun createRectangleDrawable(color: Int, strokeColor: Int = 0, strokeWidth: Int, radius: FloatArray?): GradientDrawable {
        return try {
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(color)
                if (strokeColor != 0) setStroke(strokeWidth, strokeColor)
                if (radius != null && radius.size == 4) {
                    cornerRadii = floatArrayOf(
                        radius[0], radius[0], radius[1],
                        radius[1], radius[2], radius[2],
                        radius[3], radius[3]
                    )
                }
            }
        } catch (e: Exception) {
            GradientDrawable()
        }
    }

    /**
     * 创建点击颜色
     *
     * @param pressedDrawable 点击颜色
     * @param normalDrawable  正常颜色
     */
    fun createStateListDrawable(pressedDrawable: GradientDrawable, normalDrawable: GradientDrawable): StateListDrawable {
        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_focused), pressedDrawable)
            addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
            addState(intArrayOf(-android.R.attr.state_focused), normalDrawable)
        }
    }


    init {
        setBackgroundDrawable(context.getDrawable(R.drawable.miui_rounded_corners_pop))
        width = dp2px(context, 150F)
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isModal = true
        anchorView = view

        setAdapter(object: BaseAdapter() {
            override fun getCount(): Int = arrayList.size

            override fun getItem(p0: Int): Any = arrayList[p0]

            override fun getItemId(p0: Int): Long = p0.toLong()

            override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
                val thisText = arrayList[p0].getName()
                return p1
                    ?: LinearLayout(context).apply {
                        var radius = floatArrayOf(0f, 0f, 0f, 0f)
                        val radiusFloat = dp2px(context, 20f).toFloat()
                        when (p0) {
                            0 -> {
                                radius = floatArrayOf(radiusFloat, radiusFloat, 0f, 0f)
                            }
                            arrayList.size - 1 -> {
                                radius = floatArrayOf(0f, 0f, radiusFloat, radiusFloat)
                            }
                        }
                        val pressedDrawable = createRectangleDrawable(context.getColor(if (currentValue == thisText) R.color.popup_select_click else R.color.popup_background_click), 0, 0, radius)
                        val normalDrawable = createRectangleDrawable(context.getColor(if (currentValue == thisText) R.color.popup_select else R.color.popup_background), 0, 0, radius)
                        background = createStateListDrawable(pressedDrawable, normalDrawable)
                        addView(TextView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                            descendantFocusability = LinearContainerV.FOCUS_BLOCK_DESCENDANTS
                            setTextColor(context.getColor(R.color.whiteText))
                            textSize = sp2px(context, 6f)
                            setPadding(dp2px(context, 25f), dp2px(context, 25f), 0, dp2px(context, 25f))
                            isSingleLine = true
                            text = thisText
                        })
                        addView(ImageView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                                it.gravity = Gravity.CENTER_VERTICAL
                                it.setMargins(0, dp2px(context, 2f), dp2px(context, 25f), 0)
                            }
                            background = context.getDrawable(R.drawable.ic_popup_select)
                            if (currentValue != thisText) visibility = View.GONE
                        })
                        setOnClickListener {
                            (it as ViewGroup).apply {
                                for (i in 0 until childCount) {
                                    val mView = getChildAt(i)
                                    if (mView is TextView) {
                                        dataBacks(mView.text.toString())
                                        break
                                    }
                                }
                            }
                            arrayList[p0].getCallBacks()()
                            dismiss()
                        }
                    }
            }
        })
    }
}