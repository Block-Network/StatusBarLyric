/*
 * Copyright (C) 2019 The Android Open Source Project
 *               2020 The exTHmUI Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package statusbar.lyric.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.LinearLayout
import android.widget.TextView
import statusbar.lyric.tools.Tools.isNotNull
import kotlin.math.abs

class LyricTextView(context: Context) : TextView(context) {
    private var isStop = true
    private var textLength = 0f
    private var viewWidth = 0f
    private var speed = 4f
    private var xx = 0f
    private var text: String? = null
    private val mPaint: Paint?
    private var mStartScrollRunnable = Runnable { startScroll() }
    private val invalidateRunnable = Runnable { invalidate() }

    private fun init() {
        xx = 0f
        textLength = getTextLength()
        viewWidth = width.toFloat()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(mStartScrollRunnable)
        super.onDetachedFromWindow()
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (!isStop)stopScroll()
        this.text = text.toString()
        init()
        postInvalidate()
        postDelayed(mStartScrollRunnable, startScrollDelay.toLong())
    }

    override fun setTextColor(color: Int) {
        if (mPaint.isNotNull()) mPaint!!.color = color
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val y = height / 2 + abs(mPaint!!.ascent() + mPaint.descent()) / 2
        canvas.drawText(text!!, xx, y, mPaint)
        invalidateAfter()
        if (!isStop) {
            if (viewWidth - xx + speed >= textLength) {
                xx = if (viewWidth > textLength) 0F else viewWidth - textLength
                stopScroll()
            } else {
                xx -= speed
            }
            invalidateAfter()
        }
    }

    private fun invalidateAfter() {
        removeCallbacks(invalidateRunnable)
        postDelayed(invalidateRunnable, invalidateDelay.toLong())
    }

    init {
        mPaint = paint
    }

    private fun startScroll() {
        init()
        isStop = false
        postInvalidate()
    }

    private fun stopScroll() {
        isStop = true
        removeCallbacks(mStartScrollRunnable)
        postInvalidate()
    }

    private fun getTextLength(): Float {
        return mPaint?.measureText(text) ?: 0f
    }

    fun setSpeed(speed: Float) {
        this.speed = speed
    }

    companion object {
        const val startScrollDelay = 500
        const val invalidateDelay = 10
    }
}