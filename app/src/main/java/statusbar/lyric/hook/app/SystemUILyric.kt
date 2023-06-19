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

package statusbar.lyric.hook.app


import android.app.AndroidAppHelper
import android.content.Context
import android.graphics.Paint
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.lyric.getter.api.data.DataType
import cn.lyric.getter.api.tools.Tools.receptionLyric
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import de.robv.android.xposed.XC_MethodHook
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP.config
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.tools.LogTools
import statusbar.lyric.tools.Tools.goMainThread
import statusbar.lyric.tools.Tools.isNot
import statusbar.lyric.tools.Tools.isNotNull
import statusbar.lyric.view.LyricSwitchView
import kotlin.math.min


class SystemUILyric : BaseHook() {
    private var isHook = false

    val context: Context by lazy { AndroidAppHelper.currentApplication() }

    private val displayMetrics: DisplayMetrics by lazy { context.resources.displayMetrics }

    private val displayWidth: Int by lazy { displayMetrics.widthPixels }
    private val displayHeight: Int by lazy { displayMetrics.heightPixels }

    private lateinit var clockView: TextView
    private val clockViewParent: LinearLayout by lazy {
        (clockView.parent as LinearLayout).apply {
            gravity = Gravity.CENTER
            orientation = LinearLayout.HORIZONTAL
        }
    }
    private val lyricView: LyricSwitchView by lazy {
        LyricSwitchView(context).apply {
            layoutParams = clockView.layoutParams
            setSingleLine(true)
            setMaxLines(1)
            setTextSize(TypedValue.COMPLEX_UNIT_SHIFT, if (config.lyricSize == 0) clockView.textSize else config.lyricSize.toFloat())
            setMargins(config.lyricLeft, config.lyricTop, 0, 0)
        }
    }
    private val iconView: ImageView by lazy {
        ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 7, config.iconTop, 0) }
        }
    }
    private val lyricLayout: LinearLayout by lazy {
        LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
            addView(iconView)
            addView(lyricView)
        }
    }

    override fun init() {
        val className = config.`class`
        if (className.isEmpty()) {
            LogTools.xp(moduleRes.getString(R.string.LoadClassEmpty))
            return
        }
        loadClass("com.android.systemui.SystemUIApplication").methodFinder().first { name == "onCreate" }.createHook {
            after {
                LogTools.xp(moduleRes.getString(R.string.LoadClass).format(className))
                loadClassOrNull(className).isNotNull {
                    LogTools.xp(moduleRes.getString(R.string.LoadClassSucceed).format(className))
                    it.constructorFinder().first().createHook {
                        after {
                            runCatching {
                                lyricInit(it)
                            }
                        }
                    }
                }.isNot {
                    LogTools.xp(moduleRes.getString(R.string.LoadClassFailed))
                }
            }
        }
        loadClass("com.android.systemui.statusbar.phone.DarkIconDispatcherImpl").methodFinder().first { name == "applyIconTint" }.createHook {
            after {
                if (!isHook) return@after
                it.thisObject.objectHelper {
                    val color = getObjectOrNullAs<Int>("mIconTint") ?: 0
                    setColor(color)
                }

            }
        }
    }

    private fun lyricInit(it: XC_MethodHook.MethodHookParam) {
        if (isHook) return
        isHook = true
        clockView = (it.thisObject as TextView)
        goMainThread(1) {
            clockViewParent.addView(lyricLayout, 0)
        }
        receptionLyric(context) {
            if (it.type == DataType.UPDATE) {
                val lyric = it.lyric
                updateLyric(lyric)
            } else if (it.type == DataType.STOP) {
                hideLyric()
            }
        }
    }

    private fun updateLyric(lyric: String) {
        goMainThread {
            if (lyricLayout.visibility != View.VISIBLE) lyricLayout.visibility = View.VISIBLE
            if (clockView.visibility != View.GONE) clockView.visibility = View.GONE
            val lyricWidth = getLyricWidth(lyricView.paint, lyric)
            lyricView.setText(lyric)
            lyricView.width = lyricWidth
        }
    }

    private fun hideLyric() {
        goMainThread {
            if (lyricLayout.visibility != View.GONE) lyricLayout.visibility = View.GONE
            if (clockView.visibility != View.VISIBLE) clockView.visibility = View.VISIBLE
            lyricView.setText("")
            lyricView.width = 0
        }
    }

    private fun setColor(color: Int) {
        goMainThread {
            lyricView.setTextColor(color)
        }
    }

    private fun getLyricWidth(paint: Paint, text: String): Int {
        return min(paint.measureText(text).toInt() + 6, clockViewParent.width)
    }

    override val name: String get() = this::class.java.simpleName
}
