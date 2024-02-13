package statusbar.lyric.view

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
import android.widget.ImageView
import android.widget.LinearLayout
import cn.fkj233.ui.activity.dp2px
import com.github.kyuubiran.ezxhelper.EzXHelper
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP.config
import statusbar.lyric.tools.LyricViewTools

@SuppressLint("InternalInsetResource", "DiscouragedApi")
class TitleDialog(context: Context) : Dialog(context) {

    private val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    private val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
    private val h2 = statusBarHeight / 2
    private val maxWidth = context.resources.displayMetrics.widthPixels / 2 - 80 - statusBarHeight / 2
    var showIng: Boolean = false
    var hiding: Boolean = false
    private var isStop: Boolean = false
    private val baseGravity = when (config.titleGravity) {
        0 -> Gravity.START
        1 -> Gravity.CENTER
        2 -> Gravity.END
        else -> Gravity.START
    }

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        viewYAnimate(false)
    }

    private var textView: LyricTextView = LyricTextView(context).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        if (baseGravity != Gravity.CENTER) {
            maxWidth = this@TitleDialog.maxWidth
        }
        maxLines = 1
    }
    private val iconView: ImageView by lazy {
        ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                width = dp2px(context, 15f)
                height = dp2px(context, 15f)
                setMargins(0, 0, 15, 0)
            }
            setImageDrawable(EzXHelper.moduleRes.getDrawable(R.drawable.ic_song, null))
        }
    }
    private var content: LinearLayout = LinearLayout(context).apply {
        addView(iconView)
        addView(textView)
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        background = GradientDrawable().apply {
            cornerRadius = config.titleBackgroundRadius.toFloat()
            setColor(Color.parseColor(config.titleColorAndTransparency))
            setStroke(config.titleBackgroundStrokeWidth, Color.parseColor(config.titleBackgroundStrokeColorAndTransparency))
        }
        setPadding(40, 5, 40, 5)
    }

    private var root: LinearLayout = LinearLayout(context).apply {
        addView(content)
        elevation = 10f
        gravity = Gravity.CENTER
        visibility = View.GONE
        setPadding(h2, 0, h2, statusBarHeight + 20)
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
                gravity = baseGravity or Gravity.TOP
                flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                format = PixelFormat.TRANSLUCENT
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }
            attributes = params
        }
        show()
    }

    fun delayedHide() {
        if (DELAY_DURATION == 0L) return
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, DELAY_DURATION)
    }

    fun showTitle(title: String) {
        setTitle(title)
        if (root.visibility == View.VISIBLE) {
            delayedHide()
            return
        }
        if (showIng) {
            handler.removeCallbacks(runnable)
            return
        }
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
//            val w = textView.paint.measureText(title).toInt()
//            layoutParams.width = if (w > this@TitleDialog.maxWidth) this@TitleDialog.maxWidth else w
        }
    }

    private fun viewYAnimate(into: Boolean) {
        if (into) {
            root.visibility = View.VISIBLE
        }
        val alphaAnimation = createAllAnimation(into)
        root.startAnimation(alphaAnimation)
    }


    private fun createAllAnimation(into: Boolean): AnimationSet {
        return LyricViewTools.getAlphaAnimation(into, ALPHA_ANIMATION_DURATION).apply {
            fillAfter = true
            val fromY = if (into) h2.toFloat() else statusBarHeight.toFloat()
            val toY = if (into) statusBarHeight.toFloat() else h2.toFloat()
            val translateAnimation = TranslateAnimation(0f, 0f, fromY, toY).apply {
                duration = ANIMATION_DURATION
            }
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
                        delayedHide()
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
        private const val ANIMATION_DURATION: Long = 600L
        private const val ALPHA_ANIMATION_DURATION: Long = 500L
        private val DELAY_DURATION: Long = config.titleDelayDuration.toLong()
    }
}