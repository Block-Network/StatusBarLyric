/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/Block-Network/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as
 * published by Block-Network contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/Block-Network/StatusBarLyric/blob/main/LICENSE>.
 */

package statusbar.lyric.tools

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
import androidx.core.view.isGone
import androidx.core.view.isVisible

object LyricViewTools {
    private var animaList: ArrayList<Int> = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val randomAnima: Int get() = animaList.random()

    fun getAlphaAnimation(into: Boolean, duration: Long = 250): AnimationSet {
        val alphaAnimation = (if (into) AlphaAnimation(0f, 1F) else AlphaAnimation(1F, 0f)).apply {
            this.duration = duration
        }
        return AnimationSet(true).apply {
            addAnimation(alphaAnimation)
        }
    }

    fun switchViewInAnima(int: Int?, interpolator: Int?, time: Int?): Animation? {
        val t = time?.toLong() ?: 500L
        val translateAnimation: Animation? = when (int) {
            1 -> TranslateAnimation(0f, 0f, 100f, 0f)
            2 -> TranslateAnimation(0f, 0f, -100f, 0f)
            3 -> TranslateAnimation(100f, 0f, 0f, 0f)
            4 -> TranslateAnimation(-100f, 0f, 0f, 0f)
            5 -> null
            6 -> ScaleAnimation(0f, 1f, 0f, 1f)
            7 -> ScaleAnimation(0f, 1f, 1f, 1f)
            8 -> ScaleAnimation(1f, 1f, 0f, 1f)
            9 -> ScaleAnimation(
                -1f, 1f, // X 方向从正常比例翻转到反向
                1f, 1f,  // Y 方向保持不变
                Animation.RELATIVE_TO_SELF, 0.5f, // X 轴中心点：视图的中间
                Animation.RELATIVE_TO_SELF, 0.5f  // Y 轴中心点：视图的中间
            )

            10 -> ScaleAnimation(
                1f, 1f, // X 方向保持不变
                -1f, 1f,  // Y 方向从正常比例翻转到反向
                Animation.RELATIVE_TO_SELF, 0.5f, // X 轴中心点：视图的中间
                Animation.RELATIVE_TO_SELF, 0.5f  // Y 轴中心点：视图的中间
            )

            else -> return null
        }?.apply {
            duration = t
        }
        return getAlphaAnimation(true, t).apply {
            translateAnimation?.let { addAnimation(it) }
            switchInterpolator(interpolator)
        }
    }


    fun switchViewOutAnima(str: Int?, time: Int?): Animation? {
        val t = time?.toLong() ?: 500L
        val translateAnimation: Animation? = when (str) {
            1 -> TranslateAnimation(0f, 0f, 0f, -100f)
            2 -> TranslateAnimation(0f, 0f, 0f, +100f)
            3 -> TranslateAnimation(0f, -100f, 0f, 0f)
            4 -> TranslateAnimation(0f, 0f + 100f, 0f, 0f)
            5 -> null
            6 -> ScaleAnimation(1f, 0f, 1f, 0f)
            7 -> ScaleAnimation(1f, 0f, 1f, 1f)
            8 -> ScaleAnimation(1f, 1f, 1f, 0f)
            9 -> ScaleAnimation(
                1f, -1f, // X 方向从正常比例翻转到反向
                1f, 1f,  // Y 方向保持不变
                Animation.RELATIVE_TO_SELF, 0.5f, // X 轴中心点：视图的中间
                Animation.RELATIVE_TO_SELF, 0.5f  // Y 轴中心点：视图的中间
            )

            10 -> ScaleAnimation(
                1f, 1f, // X 方向保持不变
                1f, -1f,  // Y 方向从正常比例翻转到反向
                Animation.RELATIVE_TO_SELF, 0.5f, // X 轴中心点：视图的中间
                Animation.RELATIVE_TO_SELF, 0.5f  // Y 轴中心点：视图的中间
            )

            else -> return null
        }?.apply {
            duration = t
        }
        return getAlphaAnimation(false, t).apply {
            translateAnimation?.let { addAnimation(it) }
        }
    }

    private fun Animation.switchInterpolator(int: Int?) {
        interpolator = when (int) {
            1 -> AccelerateInterpolator()
            2 -> DecelerateInterpolator()
            3 -> AccelerateDecelerateInterpolator()
            4 -> OvershootInterpolator()
            5 -> BounceInterpolator()
            else -> LinearInterpolator()
        }
    }

    private var alphaAnimation: AnimationSet? = null

    fun View.hideView(anim: Boolean = true) {
        if (isGone) return
        if (anim) {
            alphaAnimation = getAlphaAnimation(false, 0).apply {
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        visibility = View.GONE
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
            }
            startAnimation(alphaAnimation)
        } else {
            visibility = View.GONE
        }
    }

    fun View.cancelAnimation() {
        if (alphaAnimation != null)
            alphaAnimation!!.cancel()
    }

    fun View.showView() {
        if (isVisible) return
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
}