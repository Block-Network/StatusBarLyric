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
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.EzXHelper
import de.robv.android.xposed.XSharedPreferences
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP
import statusbar.lyric.tools.LogTools.log
import java.io.DataOutputStream
import java.util.*
import java.util.regex.Pattern
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty


@SuppressLint("StaticFieldLeak")
object Tools {
    private var index: Int = 0

    val isMIUI by lazy { isPresent("android.provider.MiuiSettings") }
    val togglePrompts: Boolean
        get() {
            arrayOf("com.lge.adaptive.JavaImageUtil").forEach {
                if (isPresent(it)) return true
                if (isMIUI) return true
            }
            return false
        }

    private fun isPresent(name: String): Boolean {
        return try {
            Objects.requireNonNull(Thread.currentThread().contextClassLoader).loadClass(name)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    @SuppressLint("PrivateApi")
    fun getSystemProperties(context: Context, key: String): String {
        var ret: String
        try {
            val cl = context.classLoader
            val systemProperties = cl.loadClass("android.os.SystemProperties")
            //参数类型
            val paramTypes: Array<Class<*>?> = arrayOfNulls(1)
            paramTypes[0] = String::class.java
            val get = systemProperties.getMethod("get", *paramTypes)
            //参数
            val params = arrayOfNulls<Any>(1)
            params[0] = key
            ret = get.invoke(systemProperties, *params) as String
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            ret = ""
        }
        return ret
    }

    fun <T> observableChange(initialValue: T, onChange: (oldValue: T, newValue: T) -> Unit): ReadWriteProperty<Any?, T> {
        return Delegates.observable(initialValue) { _, oldVal, newVal ->
            if (oldVal != newVal) {
                onChange(oldVal, newVal)
            }
        }
    }

    fun View.isTargetView(): Boolean {
            val textViewClassName = XposedOwnSP.config.textViewClassName
            val textViewId = XposedOwnSP.config.textViewId
            val parentViewClassName = XposedOwnSP.config.parentViewClassName
            val parentViewId = XposedOwnSP.config.parentViewId
            val textSize = XposedOwnSP.config.textSize
            if (textViewClassName.isEmpty() || parentViewClassName.isEmpty() || textViewId == 0 || parentViewId == 0|| textSize == 0f) {
                EzXHelper.moduleRes.getString(R.string.load_class_empty).log()
                return false
            }
            if (this is TextView) {
                if (this::class.java.name == textViewClassName) {
                    if (this.id == textViewId) {
                    if (this.textSize == textSize) {
                        if (this.parent is LinearLayout) {
                            val parentView = (this.parent as LinearLayout)
                            if (parentView::class.java.name == parentViewClassName) {
                                if (parentViewId == parentView.id) {
                                    if (index == XposedOwnSP.config.index) {
                                        return true
                                    } else {
                                        index += 1
                                    }
                                }
                            }
                        }
                        }
                    }
                }
            }
        return false
    }


    private fun String.regexReplace(pattern: String, newString: String): String {
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
        return try {
            val pref = XSharedPreferences(BuildConfig.APPLICATION_ID, key)
            if (pref.file.canRead()) pref else null
        } catch (e: Throwable) {
            e.log()
            null
        }
    }

    @SuppressLint("WorldReadableFiles")
    fun getSP(context: Context, key: String): SharedPreferences? {
        @Suppress("DEPRECATION") return context.createDeviceProtectedStorageContext().getSharedPreferences(key, Context.MODE_WORLD_READABLE)
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
