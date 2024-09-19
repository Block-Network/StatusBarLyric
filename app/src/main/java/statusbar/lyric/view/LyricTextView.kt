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
import android.graphics.Paint
import android.graphics.Shader
import android.widget.TextView
import statusbar.lyric.config.XposedOwnSP.config

class LyricTextView(context: Context) : TextView(context) {
    private var isScrolling = false
    private var textLength = 0f
    private var viewWidth = 0f
    private var iconWidth = 0f
    private var scrollSpeed = 4f
    private var currentX = 0f
    private val iconSwitch = config.iconSwitch
    private val lyricStartMargins = config.lyricStartMargins
    private val lyricEndMargins = config.lyricEndMargins
    private val iconStartMargins = config.iconStartMargins
    private val startScrollRunnable = Runnable { updateScrollPosition() }

    init {
        paint.style = Paint.Style.FILL_AND_STROKE
    }

    override fun onDetachedFromWindow() {
        stopScroll()
        super.onDetachedFromWindow()
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        textLength = getTextLength()
        currentX = 0f
        startScroll()
    }

    override fun setTextColor(color: Int) {
        paint.color = color
        postInvalidate()
    }

    fun setLinearGradient(shader: Shader) {
        paint.shader = shader
        postInvalidate()
    }

    fun setStrokeWidth(width: Float) {
        paint.strokeWidth = width
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val y = (height - (paint.descent() + paint.ascent())) / 2
        text?.let { canvas.drawText(it.toString(), currentX, y, paint) }
    }

    private fun updateScrollPosition() {
        val realTextLength = textLength + lyricEndMargins + lyricStartMargins
        val realLyricWidth = viewWidth - if (iconSwitch) iconWidth + iconStartMargins else 0f
        if (realTextLength <= realLyricWidth) {
            currentX = 0f
            stopScroll()
        } else if (realLyricWidth - currentX >= realTextLength) {
            currentX = realLyricWidth - realTextLength
            stopScroll()
        } else {
            currentX -= scrollSpeed
        }
    }

    override fun computeScroll() {
        if (isScrolling) {
            postDelayed(startScrollRunnable, 1000)
            postInvalidate()
        }
    }

    private fun startScroll() {
        isScrolling = true
    }

    private fun stopScroll() {
        isScrolling = false
        removeCallbacks(startScrollRunnable)
    }

    private fun getTextLength(): Float {
        return text?.let { paint.measureText(it.toString()) } ?: 0f
    }

    fun setScrollSpeed(speed: Float) {
        this.scrollSpeed = speed
    }

    fun maxViewWidth(float: Float) {
        viewWidth = float
    }

    fun iconWidth(width: Float) {
        if (config.iconSwitch) {
            iconWidth = width
        }
    }
}