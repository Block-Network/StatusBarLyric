/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/577fkj/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/StatusBarLyric/blob/main/LICENSE>.
 */

package statusbar.lyric.tools

import android.annotation.SuppressLint
import android.content.*
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import de.robv.android.xposed.XSharedPreferences
import statusbar.lyric.BuildConfig
import java.io.DataOutputStream
import java.util.*
import java.util.regex.Pattern


object Tools {
    fun String.regexReplace(pattern: String, newString: String): String {
        val p = Pattern.compile("(?i)$pattern")
        val m = p.matcher(this)
        return m.replaceAll(newString)
    }

    fun goMainThread(delayed: Long = 0, callback: () -> Unit): Boolean {
        return Handler(Looper.getMainLooper()).postDelayed({
            callback()
        }, delayed * 1000)
    }

    fun Context.isLandscape() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    fun String.dispose() = this.regexReplace(" ", "").regexReplace("\n", "")

    fun getPref(key: String): XSharedPreferences? {
        val pref = XSharedPreferences(BuildConfig.APPLICATION_ID, key)
        return if (pref.file.canRead()) pref else null
    }

    @SuppressLint("WorldReadableFiles")
    fun getSP(context: Context, key: String?): SharedPreferences? {
        return context.createDeviceProtectedStorageContext().getSharedPreferences(key, Context.MODE_WORLD_READABLE)
    }


    fun shell(command: String, isSu: Boolean) {
        try {
            if (isSu) {
                val p = Runtime.getRuntime().exec("su")
                val outputStream = p.outputStream
                DataOutputStream(outputStream).apply {
                    writeBytes(command)
                    flush()
                    close()
                }
                outputStream.close()
            } else {
                Runtime.getRuntime().exec(command)
            }
        } catch (ignored: Throwable) {
        }
    }

    private fun animation(into: Boolean): AnimationSet {
        val alphaAnimation = (if (into) AlphaAnimation(0F, 1F) else AlphaAnimation(1F, 0F)).apply {
            duration = 300
        }
        return AnimationSet(true).apply {
            addAnimation(alphaAnimation)
        }
    }

    fun inAnimation(str: String?): Animation? {
        val translateAnimation: TranslateAnimation = when (str) {
            "Top" -> TranslateAnimation(0F, 0F, 100F, 0F)
            "Bottom" -> TranslateAnimation(0F, 0F, -100F, 0F)
            "Start" -> TranslateAnimation(100F, 0F, 0F, 0F)
            "End" -> TranslateAnimation(-100F, 0F, 0F, 0F)
            else -> return null
        }.apply {
            duration = 300
        }
        return animation(true).apply {
            addAnimation(translateAnimation)
        }
    }


    fun outAnimation(str: String?): Animation? {
        val translateAnimation: TranslateAnimation = when (str) {
            "Top" -> TranslateAnimation(0F, 0F, 0F, -100F)
            "Bottom" -> TranslateAnimation(0F, 0F, 0F, 100F)
            "Start" -> TranslateAnimation(0F, -100F, 0F, 0F)
            "End" -> TranslateAnimation(0F, 100F, 0F, 0F)
            else -> return null
        }.apply {
            duration = 300
        }
        return animation(false).apply {
            addAnimation(translateAnimation)
        }
    }


    inline fun <T> T?.isNotNull(callback: (T) -> Unit): Boolean {
        if (this != null) {
            callback(this)
            return true
        }
        return false
    }

    inline fun Boolean.isNot(callback: () -> Unit) {
        if (!this) {
            callback()
        }
    }

    inline fun Any?.isNull(callback: () -> Unit): Boolean {
        if (this == null) {
            callback()
            return true
        }
        return false
    }

    fun Any?.isNull() = this == null

    fun Any?.isNotNull() = this != null
}
