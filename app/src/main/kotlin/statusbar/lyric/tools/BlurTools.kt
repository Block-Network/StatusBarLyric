package statusbar.lyric.tools

import android.view.View

object BlurTools {
    private val setBackgroundBlur by lazy {
        View::class.java.getDeclaredMethod(
            "setBackgroundBlur",
            Integer.TYPE,
            FloatArray::class.java,
            Array<IntArray>::class.java
        )
    }

    fun View.setBackgroundBlur(
        blurRadius: Int,
        cornerRadius: FloatArray,
        blendModes: Array<IntArray>
    ) {
        setBackgroundBlur.invoke(this, blurRadius, cornerRadius, blendModes)
    }

    fun cornerRadius(radius: Float) = floatArrayOf(radius, radius, radius, radius)

}
