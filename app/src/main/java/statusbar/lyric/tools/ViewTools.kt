package statusbar.lyric.tools

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation

object ViewTools {
    private fun getAlphaAnimation(into: Boolean, duration: Long = 300): AnimationSet {
        val alphaAnimation = (if (into) AlphaAnimation(0F, 1F) else AlphaAnimation(1F, 0F)).apply {
            this.duration = duration
        }
        return AnimationSet(true).apply {
            addAnimation(alphaAnimation)
        }
    }

    fun switchViewInAnima(str: String?): Animation? {
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
            duration = 300
        }
        return getAlphaAnimation(true).apply {
            addAnimation(translateAnimation)
        }
    }


    fun switchViewOutAnima(str: String?): Animation? {
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
            duration = 300
        }
        return getAlphaAnimation(false).apply {
            addAnimation(translateAnimation)
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
        val alphaAnimation = getAlphaAnimation(true, 400).apply {
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
}