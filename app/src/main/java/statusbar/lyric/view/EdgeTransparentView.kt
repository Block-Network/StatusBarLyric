package statusbar.lyric.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.view.View
import android.widget.FrameLayout
import statusbar.lyric.config.XposedOwnSP.config

@SuppressLint("ViewConstructor")
open class EdgeTransparentView(context: Context, private val drawSize: Float = 50f) : FrameLayout(context) {
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }
    private var mWidth = 0
    private var mHeight = 0


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initShader()
        mWidth = width
        mHeight = height
    }


    private fun initShader() {
        mPaint.setShader(LinearGradient(0f, 0f, 0f, drawSize, intArrayOf(Color.WHITE, Color.TRANSPARENT), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP))
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val drawChild = super.drawChild(canvas, child, drawingTime)
        val offset = (mHeight - mWidth) / 2f
        val saveCount = canvas.save()
        var start = false
        var end = false
        when (config.lyricBlurredEdgesType) {
            0 -> {
                start = true
                end = true
            }

            1 -> {
                start = true
            }

            2 -> {
                end = true
            }
        }
        if (start) {
            canvas.apply {
                rotate(270f, mWidth / 2f, mHeight / 2f)
                translate(0f, offset)
                drawRect(0 - offset, 0f, mWidth + offset, drawSize, mPaint)
                restoreToCount(saveCount)
            }
        }
        if (end) {
            canvas.apply {
                rotate(90f, mWidth / 2f, mHeight / 2f)
                translate(0f, offset)
                drawRect(0 - offset, 0f, mWidth + offset, drawSize, mPaint)
                restoreToCount(saveCount)
            }
        }
        val layerSave = canvas.saveLayer(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        canvas.restoreToCount(layerSave)
        return drawChild
    }


}