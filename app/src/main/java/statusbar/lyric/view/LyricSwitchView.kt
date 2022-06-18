package statusbar.lyric.view/*
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



import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import statusbar.lyric.utils.ktx.callMethod

@SuppressLint("ViewConstructor")
class LyricSwitchView(context: Context, private var hasMeizu: Boolean) : TextSwitcher(context) {
    private val viewArray: ArrayList<TextView> = arrayListOf()

    val text: CharSequence
        get() = (currentView as TextView).text

    val paint: TextPaint
        get() = (currentView as TextView).paint

    init {
        if (hasMeizu) {
            val lyricTextView = LyricTextView(context)
            val lyricTextView2 = LyricTextView(context)
            lyricTextView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lyricTextView2.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            addView(lyricTextView)
            addView(lyricTextView2)
            viewArray.add(lyricTextView)
            viewArray.add(lyricTextView2)
        } else {
            val autoMarqueeTextView = AutoMarqueeTextView(context)
            val autoMarqueeTextView2 = AutoMarqueeTextView(context)
            autoMarqueeTextView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            autoMarqueeTextView2.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            autoMarqueeTextView.ellipsize = TextUtils.TruncateAt.MARQUEE
            autoMarqueeTextView2.ellipsize = TextUtils.TruncateAt.MARQUEE
            autoMarqueeTextView.gravity = Gravity.CENTER_HORIZONTAL
            autoMarqueeTextView2.gravity = Gravity.CENTER_HORIZONTAL
            addView(autoMarqueeTextView)
            addView(autoMarqueeTextView2)
            viewArray.add(autoMarqueeTextView)
            viewArray.add(autoMarqueeTextView2)
        }
    }

    override fun setText(text: CharSequence) {
        val t = nextView as TextView
        t.text = text
        showNext()
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
            viewArray.forEach { view ->
                (view as LyricTextView).setSpeed(f)
            }
//            lyricTextView.setSpeed(f)
//            lyricTextView2.setSpeed(f)
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
            viewArray.forEach { view ->
                (view as AutoMarqueeTextView).marqueeRepeatLimit = i
            }
//            autoMarqueeTextView.marqueeRepeatLimit = i
//            autoMarqueeTextView2.marqueeRepeatLimit = i
        }
    }

    fun setSingleLine(bool: Boolean) {
        viewArray.forEach { view -> view.isSingleLine = bool }
    }

    fun setMaxLines(i: Int) {
        viewArray.forEach { view -> view.maxLines = i }
    }
}