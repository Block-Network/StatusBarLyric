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
import android.view.Choreographer
import android.widget.TextView
import statusbar.lyric.config.XposedOwnSP.config

class LyricTextView(context: Context) : TextView(context), Choreographer.FrameCallback {
    private var isScrolling = false
    private var textLength = 0f
    private var viewWidth = 0f
    private var scrollSpeed = 4f
    private var currentX = 0f
    private val lyricStartMargins = config.lyricStartMargins
    private val lyricEndMargins = config.lyricEndMargins
    private val startScrollRunnable = Runnable { Choreographer.getInstance().postFrameCallback(this) }

    init {
        paint.style = Paint.Style.FILL_AND_STROKE
    }

    override fun onDetachedFromWindow() {
        stopScroll()
        super.onDetachedFromWindow()
    }

    override fun setText(text: CharSequence, type: BufferType) {
        stopScroll()
        currentX = 0f
        textLength = getTextLength(text)
        super.setText(text, type)
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
        val realLyricWidth = viewWidth
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

    override fun doFrame(frameTimeNanos: Long) {
        if (isScrolling) {
            updateScrollPosition()
            postInvalidate()
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    private fun startScroll() {
        isScrolling = true
        postDelayed(startScrollRunnable, 1000)
    }

    private fun stopScroll() {
        isScrolling = false
        removeCallbacks(startScrollRunnable)
        Choreographer.getInstance().removeFrameCallback(this)
    }

    private fun getTextLength(text: CharSequence): Float {
        return paint.measureText(text.toString())
    }

    fun setScrollSpeed(speed: Float) {
        this.scrollSpeed = speed
    }

    fun maxViewWidth(float: Float) {
        viewWidth = float
    }
}