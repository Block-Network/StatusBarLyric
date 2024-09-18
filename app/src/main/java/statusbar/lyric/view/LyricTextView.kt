package statusbar.lyric.view

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import statusbar.lyric.config.XposedOwnSP.config
import java.lang.ref.WeakReference

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
    private val weakReference = WeakReference(this)
    private val startScrollRunnable = Runnable { weakReference.get()?.startScroll() }
    private val handler = Handler(Looper.getMainLooper())
    private val invalidateRunnable = Runnable { invalidate() }

    override fun onDetachedFromWindow() {
        stopScroll()
        super.onDetachedFromWindow()
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        textLength = getTextLength()
        currentX = 0f
        postDelayed(startScrollRunnable, START_SCROLL_DELAY)
    }

    override fun setTextColor(color: Int) {
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val y = (height - (paint.descent() + paint.ascent())) / 2
        text?.let { canvas.drawText(it.toString(), currentX, y, paint) }
        if (isScrolling) {
            updateScrollPosition()
            scheduleInvalidate()
        }
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

    private fun scheduleInvalidate() {
        handler.removeCallbacks(invalidateRunnable)
        handler.postDelayed(invalidateRunnable, INVALIDATE_DELAY)
    }

    private fun startScroll() {
        if (!isScrolling) {
            isScrolling = true
        }
    }

    private fun stopScroll() {
        if (isScrolling) {
            isScrolling = false
            removeCallbacks(startScrollRunnable)
            handler.removeCallbacks(invalidateRunnable)
        }
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

    companion object {
        const val START_SCROLL_DELAY = 1000L
        const val INVALIDATE_DELAY = 16L // Approximately 60 FPS
    }
}