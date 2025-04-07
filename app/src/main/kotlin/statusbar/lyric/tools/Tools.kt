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
import android.content.BroadcastReceiver
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.ArrayMap
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.github.kyuubiran.ezxhelper.EzXHelper
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import statusbar.lyric.BuildConfig
import statusbar.lyric.MainActivity.Companion.context
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP
import statusbar.lyric.tools.ActivityTools.isHook
import statusbar.lyric.tools.LogTools.log
import java.io.DataOutputStream
import java.lang.reflect.Field
import java.util.Locale
import java.util.Objects
import java.util.regex.Pattern
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

@SuppressLint("StaticFieldLeak")
object Tools {

    private var index: Int = 0

    val buildTime: String =
        SimpleDateFormat("yyyy/M/d H:m:s", Locale.CHINA).format(BuildConfig.BUILD_TIME)

    val isPad by lazy { getSystemProperties("ro.build.characteristics") == "tablet" }

    val getPhoneName by lazy {
        val xiaomiMarketName = getSystemProperties("ro.product.marketname")
        val vivoMarketName = getSystemProperties("ro.vivo.market.name")
        when {
            bigTextOne(Build.BRAND) == "Vivo" -> bigTextOne(vivoMarketName)
            xiaomiMarketName.isNotEmpty() -> bigTextOne(xiaomiMarketName)
            else -> "${bigTextOne(Build.BRAND)} ${Build.MODEL}"
        }
    }

    fun bigTextOne(st: String): String {
        val formattedBrand = st.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        return formattedBrand
    }

    fun dp2px(context: Context, dpValue: Float): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            context.resources.displayMetrics
        ).toInt()

    internal fun isPresent(name: String): Boolean {
        return try {
            Objects.requireNonNull(Thread.currentThread().contextClassLoader).loadClass(name)
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }

    @SuppressLint("PrivateApi")
    fun getSystemProperties(key: String): String {
        val ret: String = try {
            Class.forName("android.os.SystemProperties")
                .getDeclaredMethod("get", String::class.java).invoke(null, key) as String
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (_: Exception) {
            ""
        }
        return ret
    }

    fun <T> observableChange(
        initialValue: T, onChange: (oldValue: T, newValue: T) -> Unit
    ): ReadWriteProperty<Any?, T> {
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
        if (textViewClassName.isEmpty() || parentViewClassName.isEmpty() || textViewId == 0 || parentViewId == 0 || textSize == 0f) {
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

    fun Context.isLandscape() =
        resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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

    fun getSP(context: Context, key: String): SharedPreferences? {
        @Suppress("DEPRECATION", "WorldReadableFiles")
        return context.createDeviceProtectedStorageContext()
            .getSharedPreferences(
                key, if (isHook()) Context.MODE_WORLD_READABLE else Context.MODE_PRIVATE
            )
    }

    fun shell(command: String, isSu: Boolean) {
        try {
            if (isSu) {
                try {
                    val p = Runtime.getRuntime().exec("su")
                    val outputStream = p.outputStream
                    DataOutputStream(outputStream).apply {
                        writeBytes(command)
                        flush()
                        close()
                    }
                    outputStream.close()
                } catch (_: Exception) {
                    // Su shell command failed
                    Handler(Looper.getMainLooper()).post {
                        Toast
                            .makeText(context, "Root permissions required!!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Runtime.getRuntime().exec(command)
            }
        } catch (_: Throwable) {
            // Shell command failed
        }
    }

    fun checkBroadcastReceiverState(
        context: Context,
        broadcastReceiver: BroadcastReceiver?
    ): Boolean {
        context.isNull { return false }
        broadcastReceiver.isNull { return false }

        val contextImpl: Context = context.getSuperObjectField("mBase") as Context
        contextImpl.getObjectField("mPackageInfo").isNotNull {
            it.getObjectField("mReceivers").isNotNull {
                (it as ArrayMap<*, *>)[context].isNotNull {
                    (it as ArrayMap<*, *>)[broadcastReceiver].isNotNull {
                        return true
                    }
                }
            }
        }
        return false
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

    inline fun <T> T?.ifNotNull(callback: (T) -> Any?): Any? {
        if (this != null) {
            return callback(this)
        }
        return null
    }

    fun Any.getObjectField(fieldName: String): Any? {
        return XposedHelpers.getObjectField(this, fieldName)
    }

    fun Any.getSuperObjectField(fieldName: String): Any? {
        var clazz: Class<*>? = this.javaClass
        var field: Field? = null

        do {
            try {
                field = clazz?.getDeclaredField(fieldName)
                break
            } catch (_: Throwable) {
            }

            clazz = clazz?.superclass
            if (clazz == null) break
        } while (true)

        field.isNotNull {
            it.isAccessible = true
            return it.get(this)
        }
        return null
    }

    fun Any?.existField(fieldName: String): Boolean {
        if (this == null) return false
        return XposedHelpers.findFieldIfExists(this.javaClass, fieldName) != null
    }

    fun Any?.existMethod(methodName: String): Boolean {
        return this?.javaClass?.declaredMethods?.any { it.name == methodName } == true
    }

    fun Any.getObjectFieldIfExist(fieldName: String): Any? {
        return try {
            XposedHelpers.getObjectField(this, fieldName)
        } catch (_: Throwable) {
            null
        }
    }

    fun Any.setObjectField(fieldName: String, value: Any?) {
        XposedHelpers.setObjectField(this, fieldName, value)
    }

    fun Any.callMethod(methodName: String, vararg args: Any): Any? {
        return XposedHelpers.callMethod(this, methodName, *args)
    }

    fun Any?.isNull() = this == null

    fun Any?.isNotNull() = this != null
}
