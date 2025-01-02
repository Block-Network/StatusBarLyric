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
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.widget.TextSwitcher

open class LyricSwitchView(context: Context) : TextSwitcher(context) {

    init {
        initialize()
    }

    private fun initialize() {
        setFactory {
            LyricTextView(context).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            }
        }
    }

    fun getPaint(): TextPaint {
        return (getChildAt(0) as LyricTextView).paint
    }

    fun applyToAllViews(action: (LyricTextView) -> Unit) {
        for (i in 0 until childCount) {
            action(getChildAt(i) as LyricTextView)
        }
    }

    fun setWidth(width: Int) {
        applyToAllViews {
            it.width = width
        }
    }

    fun setTextColor(color: Int) {
        applyToAllViews { it.setTextColor(color) }
    }

    fun setLinearGradient(shader: Shader) {
        applyToAllViews { it.setLinearGradient(shader) }
    }

    override fun setBackground(background: Drawable?) {
        applyToAllViews { it.background = background }
    }

    fun setScrollSpeed(speed: Float) {
        applyToAllViews { it.setScrollSpeed(speed) }
    }

    fun setLetterSpacings(letterSpacing: Float) {
        applyToAllViews { it.letterSpacing = letterSpacing }
    }

    fun strokeWidth(width: Float) {
        applyToAllViews { it.setStrokeWidth(width) }
    }

    fun setTypeface(typeface: Typeface) {
        applyToAllViews { it.typeface = typeface }
    }

    fun setTextSize(unit: Int, size: Float) {
        applyToAllViews { it.setTextSize(unit, size) }
    }

    fun setMargins(start: Int, top: Int, end: Int, bottom: Int) {
        applyToAllViews {
            val layoutParams = it.layoutParams as MarginLayoutParams
            layoutParams.setMargins(start, top, end, bottom)
            it.layoutParams = layoutParams
        }
    }

    fun setSingleLine(singleLine: Boolean) {
        applyToAllViews { it.isSingleLine = singleLine }
    }

    fun setMaxLines(maxLines: Int) {
        applyToAllViews { it.maxLines = maxLines }
    }
}