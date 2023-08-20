package statusbar.lyric.tools

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import statusbar.lyric.view.LyricSwitchView

object ViewTools {
    private fun getAlphaAnimation(into: Boolean, duration: Long = 300): AnimationSet {
        val alphaAnimation = (if (into) AlphaAnimation(0F, 1F) else AlphaAnimation(1F, 0F)).apply {
            this.duration = duration
        }
        return AnimationSet(true).apply {
            addAnimation(alphaAnimation)
        }
    }

    fun switchViewInAnima(str: String?, interpolator: String?, time: Int?): Animation? {
        val t = time?.toLong() ?: 500L
        val translateAnimation: Animation = when (str) {
            "Top" -> TranslateAnimation(0F, 0F, 100F, 0F)
            "Bottom" -> TranslateAnimation(0F, 0F, -100F, 0F)
            "Start" -> TranslateAnimation(100F, 0F, 0F, 0F)
            "End" -> TranslateAnimation(-100F, 0F, 0F, 0F)
            "ScaleXY" -> ScaleAnimation(0f, 1f, 0f, 1f)
            "ScaleX" -> ScaleAnimation(0f, 1f, 1f, 1f)
            "ScaleY" -> ScaleAnimation(1f, 1f, 0f, 1f)
            else -> return null
        }.apply {
            duration = t
        }
        return getAlphaAnimation(true, t).apply {
            addAnimation(translateAnimation)
            switchInterpolator(interpolator)
        }
    }


    fun switchViewOutAnima(str: String?, time: Int?): Animation? {
        val t = time?.toLong() ?: 500L
        val translateAnimation: Animation = when (str) {
            "Top" -> TranslateAnimation(0F, 0F, 0F, -100F)
            "Bottom" -> TranslateAnimation(0F, 0F, 0F, 100F)
            "Start" -> TranslateAnimation(0F, -100F, 0F, 0F)
            "End" -> TranslateAnimation(0F, 100F, 0F, 0F)
            "ScaleXY" -> ScaleAnimation(1f, 0f, 1f, 0f)
            "ScaleX" -> ScaleAnimation(1f, 0f, 1f, 1f)
            "ScaleY" -> ScaleAnimation(1f, 1f, 1f, 0f)
            else -> return null
        }.apply {
            duration = t
        }
        return getAlphaAnimation(false, t).apply {
            addAnimation(translateAnimation)
        }
    }

    private fun Animation.switchInterpolator(str: String?) {
        interpolator = when (str) {
            "Linear" -> LinearInterpolator()
            "Accelerate" -> AccelerateInterpolator()
            "Decelerate" -> DecelerateInterpolator()
            "Accelerate&Decelerate" -> AccelerateDecelerateInterpolator()
            "Overshoot" -> OvershootInterpolator()
            "Bounce" -> BounceInterpolator()
            else -> LinearInterpolator()
        }
    }

    fun View.hideView() {
        if (visibility == View.GONE) return
        val alphaAnimation = getAlphaAnimation(false).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        startAnimation(alphaAnimation)
    }

    fun View.showView() {
        if (visibility == View.VISIBLE) return
        val alphaAnimation = getAlphaAnimation(true).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        startAnimation(alphaAnimation)
    }

    private fun ObjectAnimator.colorAnimator() {
        this.apply {
            setEvaluator(ArgbEvaluator())
            duration = 200L
        }.start()
    }

    @SuppressLint("Recycle")
    fun LyricSwitchView.textColorAnima(color: Int) {
        this.viewArray.forEach {
            ObjectAnimator.ofInt(it, "textColor", it.currentTextColor, color).colorAnimator()
        }
    }

    @SuppressLint("Recycle")
    fun ImageView.iconColorAnima(startColor: Int, endColor: Int) {
        ObjectAnimator.ofInt(this, "colorFilter", startColor, endColor).colorAnimator()
    }
}