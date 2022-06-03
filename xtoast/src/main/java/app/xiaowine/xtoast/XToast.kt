package app.xiaowine.xtoast

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

@Suppress("DEPRECATION") object XToast {
    private var lastToast: Toast? = null
    const val LENGTH_SHORT = 0
    private const val LENGTH_LONG = 1

    @SuppressLint("ShowToast")
    fun makeText(context: Context, message: CharSequence, duration: Int = LENGTH_LONG, currentTypeface: Typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL), textSize: Int = 16, textColor: Int = R.color.textColor, toastIcon: Drawable? = null, allowQueue: Boolean = true, toastGravity: Int = -1, xOffset: Int = -1, yOffset: Int = -1, isRTL: Boolean = false): Toast {
        val currentToast = Toast.makeText(context, "", duration)
        val toastLayout = LayoutInflater.from(context).inflate(R.layout.toast_layout, null)
        val toastRoot = toastLayout.findViewById<LinearLayout>(R.id.toast_root)
        val toastIcons = toastLayout.findViewById<ImageView>(R.id.toast_icon)
        val toastTextView = toastLayout.findViewById<TextView>(R.id.toast_text)
        val toastDrawable = context.resources.getDrawable(R.drawable.toast_frame)
        toastDrawable.setTint(context.resources.getColor(R.color.backgroundColor))
        toastLayout.background = toastDrawable
//        toastLayout.setBackgroundColor(R.color.backgroundColor)
        toastIcons.background = toastIcon
        if (toastIcon == null) {
            toastIcons.visibility = View.GONE
        }
        if (isRTL && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            toastRoot.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        toastTextView.text = message
        toastTextView.setTextColor(context.resources.getColor(textColor))
        toastTextView.typeface = currentTypeface
        toastTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        currentToast.view = toastLayout
        toastLayout.elevation = 15f
        if (!allowQueue) {
            if (lastToast != null) {
                lastToast!!.cancel()
            }
            lastToast = currentToast
        }

        // Make sure to use default values for non-specified ones.
        currentToast.setGravity(if (toastGravity == -1) currentToast.gravity else toastGravity, if (xOffset == -1) currentToast.xOffset else xOffset, if (yOffset == -1) currentToast.yOffset else yOffset)
        return currentToast
    }
}