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

@SuppressLint("ViewConstructor")
open class EdgeTransparentView(context: Context, private val drawSize: Float = 50f) : FrameLayout(context) {
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mWidth = 0
    private var mHeight = 0


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initShader()
        mWidth = width
        mHeight = height
    }

    //渐变颜色
    init {
        mPaint.style = Paint.Style.FILL
        mPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_OUT))
    }

    private fun initShader() {
        mPaint.setShader(LinearGradient(0f, 0f, 0f, drawSize, intArrayOf(Color.WHITE, Color.TRANSPARENT), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP))
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val layerSave = canvas.saveLayer(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        val drawChild = super.drawChild(canvas, child, drawingTime)
        val offset = (mHeight - mWidth) / 2f

        val saveCount = canvas.save()
        canvas.rotate(270f, mWidth / 2f, mHeight / 2f)
        canvas.translate(0f, offset)
        canvas.drawRect(0 - offset, 0f, mWidth + offset, drawSize, mPaint)
        canvas.restoreToCount(saveCount)

        canvas.rotate(90f, mWidth / 2f, mHeight / 2f)
        canvas.translate(0f, offset)
        canvas.drawRect(0 - offset, 0f, mWidth + offset, drawSize, mPaint)
        canvas.restoreToCount(saveCount)

        canvas.restoreToCount(layerSave)
        return drawChild
    }


}