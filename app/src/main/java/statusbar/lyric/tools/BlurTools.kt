package statusbar.lyric.tools

import android.view.View

object BlurTools {
    val blurRadio = 25
    val cornerRadius = cornerRadius(32f)
    val blendModes = arrayOf(intArrayOf(0x8f616060.toInt(), 18), intArrayOf(0xa3f2f2f2.toInt(), 3))

    private val setBackgroundBlur by lazy {
        View::class.java.getDeclaredMethod("setBackgroundBlur", Integer.TYPE, FloatArray::class.java, Array<IntArray>::class.java)
    }

    fun View.setBackgroundBlur(blurRadius: Int, cornerRadius: FloatArray, blendModes: Array<IntArray>) {
        setBackgroundBlur.invoke(this, blurRadius, cornerRadius, blendModes)
    }

    fun cornerRadius(radius: Float) = floatArrayOf(radius, radius, radius, radius)

}
