package statusbar.lyric.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.OverScroller
import android.widget.TextView
import statusbar.lyric.config.XposedOwnSP.config
import kotlin.math.roundToInt

open class LyricTextView(context: Context) : TextView(context) {
    private var viewWidth = 0f
    private var iconWidth = 0f
    private var scrollSpeed = 4f
    private var textLength = 0f
    private val iconSwitch = config.iconSwitch
    private val lyricStartMargins = config.lyricStartMargins
    private val lyricEndMargins = config.lyricEndMargins
    private val iconStartMargins = config.iconStartMargins
    private val handler = Handler(Looper.getMainLooper())
    private var scrollRunnable: Runnable? = null
    private val scroller: OverScroller = OverScroller(context)

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        postInvalidate()
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        scrollRunnable?.let { handler.removeCallbacks(it) }
        scrollX = 0
        getTextLength()

        val realTextLength = textLength + lyricEndMargins + lyricStartMargins
        val realLyricWidth = viewWidth - if (iconSwitch) iconWidth + iconStartMargins else 0f

        if (text.isNullOrEmpty() || realTextLength <= realLyricWidth) return

        scrollRunnable = Runnable {
            val duration = (realTextLength * scrollSpeed).toInt()
            scroller.startScroll(scrollX, scrollY, -(realLyricWidth - realTextLength).roundToInt(), 0, duration)
            invalidate()
        }
        handler.postDelayed(scrollRunnable!!, 1000)

    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            postInvalidate()
        }
    }

    private fun getTextLength() {
        textLength = text?.let { paint.measureText(it.toString()) } ?: 0f
    }

    fun setScrollSpeed(speed: Float) {
        this.scrollSpeed = speed
    }

    fun maxViewWidth(float: Float) {
        viewWidth = float
    }

    fun iconWidth(width: Float) {
        if (config.iconSwitch) iconWidth = width
    }
}