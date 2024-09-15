/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/577fkj/StatusBarLyric
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
 * <https://github.com/577fkj/StatusBarLyric/blob/main/LICENSE>.
 */

package statusbar.lyric.view

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.widget.TextSwitcher
import android.widget.TextView

open class LyricSwitchView(context: Context) : TextSwitcher(context) {

    val text: CharSequence get() = (currentView as TextView).text

    val paint: TextPaint
        get() = (currentView as TextView).paint


    init {
        setFactory {
            LyricTextView(context).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            }
        }
    }


    fun setAllView(unit: (LyricTextView) -> Unit) {
        IntRange(0, childCount - 1).forEach {
            unit(getChildAt(it) as LyricTextView)
        }
    }

    open fun setWidth(i: Int) {
        setAllView {
            val layoutTransition = LayoutTransition()
            this@LyricSwitchView.layoutTransition = layoutTransition
            it.layoutParams.width = i
        }
    }

    //    var textViewColor: Int
//        get() = (currentView as TextView).currentTextColor
//        set(value) {
//            setAllView { it.setTextColor(value) }
//        }
    fun setTextColor(i: Int) {
        setAllView { it.setTextColor(i) }
    }

    fun setLinearGradient(shader: Shader) {
        setAllView {
            it.paint.shader = shader
        }
    }

    override fun setBackground(background: Drawable?) {
        setAllView { it.background = background }
    }

    fun setSourceText(str: CharSequence) {
        setAllView { it.text = str }
    }

    fun setSpeed(f: Float) {
        setAllView { it.setSpeed(f) }
    }

    fun setLetterSpacings(letterSpacing: Float) {
        setAllView { it.letterSpacing = letterSpacing }
    }

    fun strokeWidth(i: Float) {
        setAllView {
            it.paint.style = Paint.Style.FILL_AND_STROKE
            it.paint.strokeWidth = i
        }

    }

    fun setTypeface(typeface: Typeface) {
        setAllView { it.typeface = typeface }
    }

    fun setTextSize(i: Int, f: Float) {
        setAllView { it.setTextSize(i, f) }
    }

    fun setMargins(start: Int, top: Int, end: Int, bottom: Int) {
        setAllView {
            val layoutParams = it.layoutParams as MarginLayoutParams
            layoutParams.setMargins(start, top, end, bottom)
            it.layoutParams = layoutParams
        }
    }

    fun setSingleLine(bool: Boolean) {
        setAllView { it.isSingleLine = bool }
    }

    fun setMaxLines(i: Int) {
        setAllView { it.maxLines = i }
    }
}