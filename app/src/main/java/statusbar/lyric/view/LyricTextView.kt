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
import android.graphics.Canvas
import android.view.ViewGroup
import android.widget.TextView
import statusbar.lyric.config.XposedOwnSP.config

open class LyricTextView(context: Context) : TextView(context) {
    private var isScrolling = false
    private var textLength = 0f
    private var viewWidth = 0f
    private var scrollSpeed = 4f
    private var currentX = 0f
    private var displayText: String? = null
    private val startScrollRunnable = Runnable { startScroll() }
    private val invalidateRunnable = Runnable { invalidate() }

    init {
        initialize()
    }

    private fun initialize() {
        currentX = 0f
        textLength = getTextLength()
        viewWidth = width.toFloat()
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(startScrollRunnable)
        super.onDetachedFromWindow()
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (isScrolling) stopScroll()
        displayText = text.toString()
        initialize()
        postInvalidate()
        postDelayed(startScrollRunnable, START_SCROLL_DELAY)
    }

    override fun setTextColor(color: Int) {
        paint.color = color
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val y = (height - (paint.descent() + paint.ascent())) / 2
        canvas.drawText(displayText!!, currentX, y, paint)
        if (isScrolling) updateScrollPosition()
        invalidateAfter()
    }

    private fun updateScrollPosition() {
        val currentViewWidth = viewWidth - config.lyricStartMargins - config.lyricEndMargins
        if (viewWidth - config.lyricEndMargins - currentX == textLength) {
            currentX = 0f
            stopScroll()
        } else if (currentViewWidth - currentX >= textLength) {
            currentX = if (currentViewWidth > textLength) 0f else currentViewWidth - textLength
            stopScroll()
        } else {
            currentX -= scrollSpeed
        }
    }

    private fun invalidateAfter() {
        removeCallbacks(invalidateRunnable)
        postDelayed(invalidateRunnable, INVALIDATE_DELAY.toLong())
    }

    private fun startScroll() {
        initialize()
        isScrolling = true
        postInvalidate()
    }

    private fun stopScroll() {
        isScrolling = false
        removeCallbacks(startScrollRunnable)
        postInvalidate()
    }

    private fun getTextLength(): Float {
        return displayText?.let { paint.measureText(it) } ?: 0f
    }

    fun setScrollSpeed(speed: Float) {
        this.scrollSpeed = speed
    }

    fun setMargins(start: Int, top: Int, end: Int, bottom: Int) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(start, top, end, bottom)
        layoutParams = params
    }

    companion object {
        const val START_SCROLL_DELAY = 500L
        const val INVALIDATE_DELAY = 10
    }
}