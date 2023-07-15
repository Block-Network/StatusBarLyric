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

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.widget.TextSwitcher
import android.widget.TextView

class LyricSwitchView(context: Context) : TextSwitcher(context) {
    private val lyricTextView: LyricTextView = LyricTextView(context)
    private val lyricTextView2: LyricTextView = LyricTextView(context)
    val viewArray: Array<TextView> = arrayOf(lyricTextView, lyricTextView2)

    val text: CharSequence get() = (currentView as TextView).text

    val paint: TextPaint
        get() = (currentView as TextView).paint

    init {
        addView(lyricTextView)
        addView(lyricTextView2)
    }

    fun setWidth(i: Int) {
        viewArray.forEach { view -> view.width = i }
    }

    fun setTextColor(i: Int) {
        viewArray.forEach { view -> view.setTextColor(i) }
    }


    fun setSourceText(str: CharSequence) {
        viewArray.forEach { view -> view.text = str }
    }

    fun setSpeed(f: Float) {
        lyricTextView.setSpeed(f)
        lyricTextView2.setSpeed(f)
    }

    fun setLetterSpacings(letterSpacing: Float) {
        viewArray.forEach { view -> view.letterSpacing = letterSpacing }
    }

    fun strokeWidth(i: Float) {
        viewArray.forEach { view ->
            view.paint.style = Paint.Style.FILL_AND_STROKE
            view.paint.strokeWidth = i
        }
    }

    fun setTypeface(typeface: Typeface) {
        viewArray.forEach { view -> view.typeface = typeface }
    }

    fun setTextSize(i: Int, f: Float) {
        viewArray.forEach { view -> view.setTextSize(i, f) }
    }

    fun setMargins(start: Int, top: Int, end: Int, bottom: Int) {
        viewArray.forEach { view: TextView ->
            val layoutParams = view.layoutParams as MarginLayoutParams
            layoutParams.setMargins(start, top, end, bottom)
            view.layoutParams = layoutParams
        }
    }

    fun setSingleLine(bool: Boolean) {
        viewArray.forEach { view -> view.isSingleLine = bool }
    }

    fun setMaxLines(i: Int) {
        viewArray.forEach { view -> view.maxLines = i }
    }
}