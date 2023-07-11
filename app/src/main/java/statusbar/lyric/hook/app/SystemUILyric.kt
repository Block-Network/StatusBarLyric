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


import android.annotation.SuppressLint
import android.app.AndroidAppHelper
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.lyric.getter.api.data.DataType
import cn.lyric.getter.api.data.LyricData
import cn.lyric.getter.api.tools.Tools.base64ToDrawable
import cn.lyric.getter.api.tools.Tools.receptionLyric
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import de.robv.android.xposed.XC_MethodHook
import statusbar.lyric.config.XposedOwnSP.config
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.tools.LogTools
import statusbar.lyric.tools.Tools
import statusbar.lyric.tools.Tools.goMainThread
import statusbar.lyric.tools.Tools.isLandscape
import statusbar.lyric.tools.Tools.isNotNull
import statusbar.lyric.tools.Tools.isTargetView
import statusbar.lyric.tools.Tools.regexReplace
import statusbar.lyric.view.EdgeTransparentView
import statusbar.lyric.view.LyricSwitchView
import java.io.File
import kotlin.math.min
import kotlin.math.roundToInt


class SystemUILyric : BaseHook() {
    private var isScreenLock: Boolean = false
    private lateinit var hook: XC_MethodHook.Unhook
    private var lastColor: Int = 0
    private var lastLyric: String = ""
    private var lastBase64Icon: String = ""
    private var iconSwitch: Boolean = true
    private var isShow: Boolean = false
    val context: Context by lazy { AndroidAppHelper.currentApplication() }

    private val displayMetrics: DisplayMetrics by lazy { context.resources.displayMetrics }

    private val displayWidth: Int by lazy { displayMetrics.widthPixels }
    private val displayHeight: Int by lazy { displayMetrics.heightPixels }


    private lateinit var clockView: TextView
    private lateinit var targetView: ViewGroup
    private val lyricView: LyricSwitchView by lazy {
        LyricSwitchView(context).apply {
            setTypeface(clockView.typeface)
            layoutParams = clockView.layoutParams
            setSingleLine(true)
            setMaxLines(1)
        }
    }

    private val iconView: ImageView by lazy { ImageView(context) }
    private val lyricLayout: LinearLayout by lazy {
        LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
            addView(iconView)
            addView(if (config.lyricBlurredEdges) {
                EdgeTransparentView(context, config.lyricBlurredEdgesRadius.toFloat()).apply {
                    addView(lyricView)
                }
            } else {
                lyricView
            })
            visibility = View.GONE
        }
    }

    private lateinit var mNotificationIconArea: View

    //////////////////////////////Hook//////////////////////////////////////
    override fun init() {
        LogTools.xp("Init")
        loadClassOrNull(config.textViewClassName).isNotNull {
            hook = TextView::class.java.methodFinder().filterByName("setText").first().createHook {
                after { hookParam ->
                    (hookParam.thisObject as View).isTargetView { view ->
                        LogTools.xp("Lyric Init")
                        clockView = view
                        targetView = (clockView.parent as LinearLayout)
                        lyricInit()
                        hook.unhook()
                    }
                }
            }
            if (config.limitVisibilityChange) {
                it.methodFinder().filterByName("setVisibility").first().createHook {
                    before { hookParam ->
                        if (isShow) {
                            when (hookParam.args[0]) {
                                View.VISIBLE -> hookParam.args[0] = View.GONE
                            }
                        }
                    }
                }
            }
        }

        loadClassOrNull("com.android.systemui.statusbar.phone.NotificationIconAreaController").isNotNull {
            it.methodFinder().filterByName("onDarkChanged").first().createHook {
                after { hookParam ->
                    if (!(this@SystemUILyric::clockView.isInitialized && this@SystemUILyric::targetView.isInitialized)) return@after
                    changeColor(hookParam.args[2] as Int)
                }
            }
            it.constructorFinder().first().createHook {
                after { hookParam ->
                    hookParam.thisObject.objectHelper {
                        mNotificationIconArea = this.getObjectOrNullAs<View>("mNotificationIconArea")!!
                    }
                }
            }
        }
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag", "MissingPermission")
    private fun lyricInit() {
        goMainThread(1) {
            if (config.viewIndex == 0) {
                targetView.addView(lyricLayout, 0)
            } else {
                targetView.addView(lyricLayout)
            }
        }
        receptionLyric(context) {
            if (!(this::clockView.isInitialized && this::targetView.isInitialized)) return@receptionLyric
            if (it.type == DataType.UPDATE) {
                val lyric = it.lyric.regexReplace(config.regexReplace, "")
                if (lyric.isNotEmpty()) {
                    changeLyric(lyric)
                    changeIcon(it)
                }
            } else if (it.type == DataType.STOP) {
                hideLyric()
            }
            LogTools.xp(it.toString())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(UpdateConfig(), IntentFilter("updateConfig"), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(UpdateConfig(), IntentFilter("updateConfig"))
        }
        if (config.hideLyricWhenLockScreen) {
            val screenLockFilter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(ScreenLockReceiver(), screenLockFilter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(ScreenLockReceiver(), screenLockFilter)
            }

        }
        changeConfig(1)
    }

    private fun changeLyric(lyric: String) {
        if (lastLyric == lyric || isScreenLock) return
        lastLyric = lyric
        isShow = true
        LogTools.xp("lyric:$lyric")
        goMainThread {
            if (lyricLayout.visibility != View.VISIBLE) lyricLayout.visibility = View.VISIBLE
            if (config.hideTime && clockView.visibility != View.GONE) clockView.visibility = View.GONE
            if (this::mNotificationIconArea.isInitialized && config.hideNotificationIcon && mNotificationIconArea.visibility != View.GONE) mNotificationIconArea.visibility = View.GONE
            lyricView.apply {
                if (config.animation == "Random") {
                    val effect = arrayListOf("Top", "Bottom", "Start", "End").random()
                    inAnimation = Tools.inAnimation(effect)
                    outAnimation = Tools.outAnimation(effect)
                }
                setText(lyric)
                width = getLyricWidth(lyricView.paint, lyric)
            }
        }
    }

    private fun changeIcon(it: LyricData) {
        if (!iconSwitch) return
        val customIcon = it.customIcon && it.base64Icon.isNotEmpty()
        goMainThread {
            iconView.setImageBitmap(if (customIcon) {
                if (it.base64Icon == lastBase64Icon) return@goMainThread
                lastBase64Icon = it.base64Icon
                base64ToDrawable(it.base64Icon)
            } else {
                val baseIcon = config.getDefaultIcon(it.packageName, config.forceTheIconToBeDisplayed)
                if (baseIcon == lastBase64Icon) return@goMainThread
                lastBase64Icon = baseIcon
                base64ToDrawable(baseIcon)
            })
            LogTools.xp("Change Icon")
        }
    }

    private fun hideLyric() {
        isShow = false
        LogTools.xp("Hide Lyric")
        goMainThread {
            if (lyricLayout.visibility != View.GONE) lyricLayout.visibility = View.GONE
            if (config.hideTime && clockView.visibility != View.VISIBLE) clockView.visibility = View.VISIBLE
            if (this::mNotificationIconArea.isInitialized && config.hideNotificationIcon && mNotificationIconArea.visibility != View.VISIBLE) mNotificationIconArea.visibility = View.VISIBLE
            lyricView.apply {
                setText("")
                width = 0
            }
        }
    }

    private fun changeColor(color: Int) {
        LogTools.xp("Change Color")
        if (lastColor == color) return
        lastColor = color
        goMainThread {
            if (config.lyricColor.isEmpty()) lyricView.setTextColor(color)
            if (config.iconColor.isEmpty()) iconView.setColorFilter(color)
        }
    }

    private fun changeConfig(delay: Long = 0L) {
        LogTools.xp("Change Config")
        config.update()
        goMainThread(delay) {
            lyricView.apply {
                setTextSize(TypedValue.COMPLEX_UNIT_SHIFT, if (config.lyricSize == 0) clockView.textSize else config.lyricSize.toFloat())
                setMargins(config.lyricLeftMargins, config.lyricTopMargins, 0, 0)
                if (config.lyricColor.isEmpty()) {
                    setTextColor(clockView.textColors.defaultColor)
                } else {
                    setTextColor(Color.parseColor(config.lyricColor))
                }
                setLetterSpacings(config.lyricLetterSpacing / 100f)
                strokeWidth(config.lyricStrokeWidth / 100f)
                setSpeed(config.lyricSpeed.toFloat())
                val animation = config.animation
                if (animation != "Random") {
                    inAnimation = Tools.inAnimation(animation)
                    outAnimation = Tools.outAnimation(animation)
                }
                runCatching {
                    val file = File("${context.filesDir.path}/font")
                    if (file.exists() && file.canRead()) {
                        setTypeface(Typeface.createFromFile(file))
                    }
                }
            }
            if (!config.iconSwitch) {
                if (iconView.visibility != View.GONE) {
                    iconView.visibility = View.GONE
                }
                iconSwitch = false
            } else {
                if (iconView.visibility != View.VISIBLE) {
                    iconView.visibility = View.VISIBLE
                }
                iconSwitch = true
                iconView.apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT).apply { setMargins(config.iconLeftMargins, config.iconTopMargins, 0, 0) }.apply {
                        if (config.iconSize == 0) {
                            width = clockView.height / 2
                            height = clockView.height / 2
                        } else {
                            width = config.iconSize
                            height = config.iconSize
                        }
                    }
                    if (config.iconColor.isEmpty()) {
                        setColorFilter(clockView.textColors.defaultColor)
                    } else {
                        setColorFilter(Color.parseColor(config.iconColor))
                    }
                }
            }
            if (this::mNotificationIconArea.isInitialized) mNotificationIconArea.visibility = if (config.hideNotificationIcon) View.GONE else View.VISIBLE
        }
    }

    private fun getLyricWidth(paint: Paint, text: String): Int {
        LogTools.xp("Get Lyric Width")
        return if (config.lyricWidth == 0) {
            min(paint.measureText(text).toInt() + 6, targetView.width)
        } else {
            if (config.fixedLyricWidth) {
                scaleWidth()
            } else {
                min(paint.measureText(text).toInt() + 6, scaleWidth())
            }

        }
    }

    private fun scaleWidth(): Int {
        LogTools.xp("Scale Width")
        return (config.lyricWidth / 100.0 * if (context.isLandscape()) {
            displayHeight
        } else {
            displayWidth
        }).roundToInt()
    }

    override val name: String get() = this::class.java.simpleName


    inner class UpdateConfig : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("type")) {
                "normal" -> {
                    if (!(this@SystemUILyric::clockView.isInitialized && this@SystemUILyric::targetView.isInitialized)) return
                    changeConfig()
                }

                "change_font" -> {}
                "reset_font" -> {}
            }
        }

    }

    inner class ScreenLockReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isScreenLock = intent.action == Intent.ACTION_SCREEN_OFF
            if (isScreenLock) {
                hideLyric()
            }
        }

    }
}
