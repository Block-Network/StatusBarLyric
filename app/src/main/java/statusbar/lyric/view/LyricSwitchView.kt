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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import statusbar.lyric.utils.ktx.callMethod

@SuppressLint("ViewConstructor")
class LyricSwitchView(context: Context, private var hasMeizu: Boolean): TextSwitcher(context) {
    private val lyricTextView: LyricTextView = LyricTextView(context)
    private val lyricTextView2: LyricTextView = LyricTextView(context)
    private val autoMarqueeTextView: AutoMarqueeTextView = AutoMarqueeTextView(context)
    private val autoMarqueeTextView2: AutoMarqueeTextView = AutoMarqueeTextView(context)
    private val viewArray: ArrayList<TextView> = arrayListOf()

    val text: CharSequence
        get() = (currentView as TextView).text

    val paint: TextPaint
        get() = (currentView as TextView).paint

    init {
        lyricTextView.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        lyricTextView2.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        autoMarqueeTextView.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        autoMarqueeTextView2.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        autoMarqueeTextView.ellipsize = TextUtils.TruncateAt.MARQUEE
        autoMarqueeTextView2.ellipsize = TextUtils.TruncateAt.MARQUEE
        if (hasMeizu) {
            addView(lyricTextView)
            addView(lyricTextView2)
            viewArray.add(lyricTextView)
            viewArray.add(lyricTextView2)
        } else {
            addView(autoMarqueeTextView)
            addView(autoMarqueeTextView2)
            viewArray.add(autoMarqueeTextView)
            viewArray.add(autoMarqueeTextView2)
        }
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
        if (hasMeizu) {
            lyricTextView.setSpeed(f)
            lyricTextView2.setSpeed(f)
        }
    }

    fun setLetterSpacings(letterSpacing: Float) {
        viewArray.forEach { view -> view.letterSpacing = letterSpacing }
    }

    fun setHeight(i: Int) {
        viewArray.forEach { view -> view.height = i }
    }

    fun setTypeface(typeface: Typeface) {
        viewArray.forEach { view -> view.typeface = typeface }
    }

    fun setTextSize(i: Int, f: Float) {
        viewArray.forEach { view -> view.setTextSize(i, f) }
    }

    fun setMargins(i: Int, i1: Int, i2: Int, i3: Int) {
        viewArray.forEach { view -> view.layoutParams.callMethod("setMargins", i, i1, i2, i3) }
    }

    fun setMarqueeRepeatLimit(i: Int) {
        if (!hasMeizu) {
            autoMarqueeTextView.marqueeRepeatLimit = i
            autoMarqueeTextView2.marqueeRepeatLimit = i
        }
    }

    fun setSingleLine(bool: Boolean) {
        viewArray.forEach { view -> view.isSingleLine = bool }
    }

    fun setMaxLines(i: Int) {
        viewArray.forEach { view -> view.maxLines = i }
    }
}