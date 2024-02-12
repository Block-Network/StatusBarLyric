package statusbar.lyric.hook.app

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.BounceInterpolator
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import statusbar.lyric.tools.LyeicViewTools
import statusbar.lyric.view.LyricTextView

@SuppressLint("InternalInsetResource", "DiscouragedApi")
class TitleDialog(context: Context) : Dialog(context) {

    private val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    private val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
    private val h2 = statusBarHeight / 2
    private val maxWidth = context.resources.displayMetrics.widthPixels / 2 - 80 - statusBarHeight / 2
    var showIng: Boolean = false
    var hiding: Boolean = false
    private var isStop: Boolean = false

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        viewYAnimate(false)
    }

    private var textView: LyricTextView = LyricTextView(context).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        maxLines = 1
    }
    private var content: LinearLayout = LinearLayout(context).apply {
        addView(textView)
        gravity = Gravity.CENTER
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        background = GradientDrawable().apply {
            cornerRadius = 50f
            setColor(Color.BLACK)
        }
        setPadding(40, 5, 40, 5)
    }

    private var root: LinearLayout = LinearLayout(context).apply {
        addView(content)
        gravity = Gravity.CENTER
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        setPadding(0, 0, 0, statusBarHeight + 20)
        setOnClickListener {
            if (!hiding) {
                handler.removeCallbacks(runnable)
                viewYAnimate(false)
            }
        }
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(root)
        window?.apply {
            setBackgroundDrawable(null)
            val params = attributes
            params.apply {
                gravity = Gravity.START or Gravity.TOP
                flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                format = PixelFormat.TRANSLUCENT
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                x = h2
            }
            attributes = params
        }
        show()
    }


    fun showTitle(title: String) {
        setTitle(title)
        if (showIng) return
        viewYAnimate(true)
    }

    fun hideTitle() {
        if (isStop) return
        isStop = true
        if (hiding) return
        viewYAnimate(false)
    }

    fun setTitle(title: String) {
        textView.apply {
            text = title
            val w = textView.paint.measureText(title).toInt()
            layoutParams.width = if (w > this@TitleDialog.maxWidth) this@TitleDialog.maxWidth else w
        }
    }

    private fun viewYAnimate(into: Boolean) {
        if (into) {
            root.visibility = View.VISIBLE
        }
        val translateAnimation = createTranslateAnimation(into)
        val alphaAnimation = createAllAnimation(into, translateAnimation)
        root.startAnimation(alphaAnimation)
    }

    private fun createTranslateAnimation(into: Boolean): TranslateAnimation {
        val fromY = if (into) h2.toFloat() else statusBarHeight.toFloat()
        val toY = if (into) statusBarHeight.toFloat() else h2.toFloat()
        return TranslateAnimation(0f, 0f, fromY, toY).apply {
            duration = ANIMATION_DURATION
        }
    }

    private fun createAllAnimation(into: Boolean, translateAnimation: TranslateAnimation): AnimationSet {
        return LyeicViewTools.getAlphaAnimation(into, ALPHA_ANIMATION_DURATION).apply {
            fillAfter = true
            addAnimation(translateAnimation)
            interpolator = if (into) {
                BounceInterpolator()
            } else {
                AccelerateInterpolator(1.5f)
            }
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    if (into) {
                        showIng = true
                        handler.removeCallbacks(runnable)
                        handler.postDelayed(runnable, DELAY_DURATION)
                    } else {
                        hiding = true
                    }
                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (into) {
                        showIng = false
                        root.visibility = View.VISIBLE
                    } else {
                        hiding = false
                        root.visibility = View.GONE
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
        }
    }

    companion object {
        private const val ANIMATION_DURATION = 600L
        private const val ALPHA_ANIMATION_DURATION = 500L
        private const val DELAY_DURATION = 3000L
    }
}