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

package statusbar.lyric.hook.module

import android.annotation.SuppressLint
import android.app.AndroidAppHelper
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.lyric.getter.api.data.ExtraData
import cn.lyric.getter.api.data.LyricData
import cn.lyric.getter.api.listener.LyricListener
import cn.lyric.getter.api.listener.LyricReceiver
import cn.lyric.getter.api.tools.Tools.base64ToDrawable
import cn.lyric.getter.api.tools.Tools.registerLyricListener
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import com.github.kyuubiran.ezxhelper.HookFactory
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP.config
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.tools.BlurTools.cornerRadius
import statusbar.lyric.tools.BlurTools.setBackgroundBlur
import statusbar.lyric.tools.LogTools.log
import statusbar.lyric.tools.LyricViewTools
import statusbar.lyric.tools.LyricViewTools.hideView
import statusbar.lyric.tools.LyricViewTools.iconColorAnima
import statusbar.lyric.tools.LyricViewTools.randomAnima
import statusbar.lyric.tools.LyricViewTools.showView
import statusbar.lyric.tools.LyricViewTools.textColorAnima
import statusbar.lyric.tools.SystemMediaSessionListener
import statusbar.lyric.tools.Tools.goMainThread
import statusbar.lyric.tools.Tools.isHyperOS
import statusbar.lyric.tools.Tools.isLandscape
import statusbar.lyric.tools.Tools.isMiui
import statusbar.lyric.tools.Tools.isNot
import statusbar.lyric.tools.Tools.isNotNull
import statusbar.lyric.tools.Tools.isNull
import statusbar.lyric.tools.Tools.isPad
import statusbar.lyric.tools.Tools.isTargetView
import statusbar.lyric.tools.Tools.observableChange
import statusbar.lyric.tools.Tools.shell
import statusbar.lyric.tools.Tools.togglePrompts
import statusbar.lyric.view.LyricSwitchView
import statusbar.lyric.view.TitleDialog
import java.io.File
import kotlin.math.abs
import kotlin.math.min

class SystemUILyric : BaseHook() {

    private lateinit var hook: XC_MethodHook.Unhook
    val context: Context by lazy { AndroidAppHelper.currentApplication() }

    private var lastColor: Int by observableChange(Color.WHITE) { oldValue, newValue ->
        goMainThread {
            if (config.lyricColor.isEmpty() && config.lyricGradientColor.isEmpty()) {
                when (config.lyricColorScheme) {
                    0 -> lyricView.setTextColor(newValue)
                    1 -> lyricView.textColorAnima(newValue)
                }
            }
            if (config.iconColor.isEmpty()) {
                when (config.lyricColorScheme) {
                    0 -> iconView.setColorFilter(newValue, PorterDuff.Mode.SRC_IN)
                    1 -> iconView.iconColorAnima(oldValue, newValue)
                }
            }
        }
        "Change Color".log()
    }
    private var lastLyric: String = ""
    private var title: String by observableChange("") { _, newValue ->
        if (!config.titleShowWithSameLyric && lastLyric == newValue) return@observableChange
        if (!isPlaying) return@observableChange
        goMainThread {
            titleDialog.apply {
                if (newValue.isEmpty()) {
                    hideTitle()
                } else {
                    showTitle(newValue.trim())
                }
            }
        }
    }
    private var lastBase64Icon: String by observableChange("") { _, newValue ->
        goMainThread {
            base64ToDrawable(newValue).isNotNull {
                iconView.showView()
                iconView.setImageBitmap(it)
            }.isNot {
                iconView.hideView()
            }
            "Change Icon".log()
        }
    }
    private var canLoad: Boolean = true
    private var isScreenLock: Boolean = false
    private var iconSwitch: Boolean = config.iconSwitch
    private var isPlaying: Boolean = false
    private var isStop: Boolean = false
    private var isHiding: Boolean = false
    private var isRandomAnima: Boolean = false
    private var isFullScreenMode: Boolean = false
    val isReally by lazy { this@SystemUILyric::clockView.isInitialized }

    private var themeMode: Int by observableChange(0) { oldValue, _ ->
        if (oldValue == 0) return@observableChange
        "onConfigurationChanged".log()
        canLoad = true
        hideLyric()
    }
    private var theoreticalWidth: Int = 0
    private lateinit var point: Point


    private val displayMetrics: DisplayMetrics by lazy { context.resources.displayMetrics }
    private val displayWidth: Int by lazy { displayMetrics.widthPixels }
    private val displayHeight: Int by lazy { displayMetrics.heightPixels }


    private lateinit var clockView: TextView
    private lateinit var targetView: ViewGroup
    private lateinit var mNotificationIconArea: View
    private lateinit var mCarrierLabel: View
    private lateinit var mPadClockView: View
    private val lyricView: LyricSwitchView by lazy {
        object : LyricSwitchView(context) {
            override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
                super.onSizeChanged(w, h, oldw, oldh)
                if (config.lyricGradientColor.isNotEmpty()) {
                    config.lyricGradientColor.trim().split(",").map { Color.parseColor(it.trim()) }.let { colors ->
                        if (colors.isEmpty()) {
                            setTextColor(Color.WHITE)
                        } else if (colors.size < 2) {
                            setTextColor(colors[0])
                        } else {
                            val textShader = LinearGradient(0f, 0f, width.toFloat(), 0f, colors.toIntArray(), null, Shader.TileMode.CLAMP)
                            setLinearGradient(textShader)
                        }
                    }
                }
            }
        }.apply {
            setTypeface(clockView.typeface)
            setSingleLine(true)
            setMaxLines(1)
        }
    }
    private val iconView: ImageView by lazy {
        ImageView(context).apply {
            visibility = View.GONE
        }
    }
    private val lyricLayout: LinearLayout by lazy {
        LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
            addView(iconView)
            addView(lyricView)
            visibility = View.GONE
        }
    }
    private lateinit var mMiuiNetworkSpeedView: TextView
    private val titleDialog by lazy {
        TitleDialog(context)
    }

    //////////////////////////////Hook//////////////////////////////////////
    @SuppressLint("DiscouragedApi")
    override fun init() {
        "Init Hook".log()
        loadClassOrNull(config.textViewClassName).isNotNull {
            hook = TextView::class.java.methodFinder().filterByName("onDraw").first().createHook {
                after { hookParam ->
                    val view = (hookParam.thisObject as View)
                    if (view.isTargetView()) {
                        if (!canLoad) return@after
                        "Lyric Init".log()
                        clockView = view as TextView
                        targetView = (clockView.parent as LinearLayout).apply {
                            gravity = Gravity.CENTER
                        }
                        canLoad = false
                        lyricInit()
                        if (!togglePrompts) hook.unhook()
                    }
                }
            }
        }.isNot {
            moduleRes.getString(R.string.load_class_empty).log()
            return
        }
        if (config.limitVisibilityChange) {
            moduleRes.getString(R.string.limit_visibility_change).log()
            View::class.java.methodFinder().filterByName("setVisibility").first().createHook {
                before { hookParam ->
                    if (isPlaying && !isHiding) {
                        if (hookParam.args[0] == View.VISIBLE) {
                            val view = hookParam.thisObject as View
                            if (view.isTargetView() || (this@SystemUILyric::mNotificationIconArea.isInitialized && mNotificationIconArea == view) || (this@SystemUILyric::mCarrierLabel.isInitialized && mCarrierLabel == view) || (this@SystemUILyric::mMiuiNetworkSpeedView.isInitialized && mMiuiNetworkSpeedView == view) || (this@SystemUILyric::mPadClockView.isInitialized && mPadClockView == view)) {
                                hookParam.args[0] = View.GONE
                            }
                        }
                    }
                }
            }
        }
        "${moduleRes.getString(R.string.lyric_color_scheme)}:${config.lyricColorScheme}".log()
        when (config.lyricColorScheme) {
            0 -> {
                loadClassOrNull("com.android.systemui.statusbar.phone.DarkIconDispatcherImpl").isNotNull {
                    it.methodFinder().filterByName("applyDarkIntensity").first().createHook {
                        after { hookParam ->
                            if (!isPlaying) return@after
                            val mIconTint = hookParam.thisObject.objectHelper().getObjectOrNullAs<Int>("mIconTint")
                            lastColor = mIconTint ?: Color.BLACK
                        }
                    }
                }
            }

            1 -> {
                loadClassOrNull("com.android.systemui.statusbar.phone.NotificationIconAreaController").isNotNull {
                    it.methodFinder().filterByName("onDarkChanged").first().createHook {
                        after { hookParam ->
                            if (!isPlaying) return@after
                            val isDark = (hookParam.args[1] as Float) == 1f
                            lastColor = if (isDark) Color.BLACK else Color.WHITE
                        }
                    }
                }
            }
        }
        if (config.hideNotificationIcon) {
            moduleRes.getString(R.string.hide_notification_icon).log()
            fun HookFactory.hideNoticeIcon() {
                after { hookParam ->
                    val clazz = hookParam.thisObject::class.java
                    if (clazz.simpleName == "NotificationIconAreaController") {
                        hookParam.thisObject.objectHelper {
                            mNotificationIconArea = this.getObjectOrNullAs<View>("mNotificationIconArea")!!
                        }
                    } else {
                        mNotificationIconArea = clazz.superclass.getField("mNotificationIconArea").get(hookParam.thisObject) as View
                    }
                }
            }
            loadClassOrNull("com.android.systemui.statusbar.phone.NotificationIconAreaController").isNotNull {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.constructorFinder().first().createHook {
                        hideNoticeIcon()
                    }
                } else {
                    it.methodFinder().filterByName("initializeNotificationAreaViews").first().createHook {
                        hideNoticeIcon()
                    }
                }
            }
        }
        if (config.hideCarrier && Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            moduleRes.getString(R.string.hide_carrier).log()
            loadClassOrNull("com.android.systemui.statusbar.phone.KeyguardStatusBarView").isNotNull {
                it.methodFinder().filterByName("onFinishInflate").first().createHook {
                    after { hookParam ->
                        val clazz = hookParam.thisObject::class.java
                        if (clazz.simpleName == "KeyguardStatusBarView") {
                            hookParam.thisObject.objectHelper {
                                mCarrierLabel = this.getObjectOrNullAs<View>("mCarrierLabel")!!
                            }
                        } else {
                            mCarrierLabel = clazz.superclass.getField("mCarrierLabel").get(hookParam.thisObject) as View
                        }
                    }
                }
            }
        }
        if (config.clickStatusBarToHideLyric || config.slideStatusBarCutSongs) {
            loadClassOrNull("com.android.systemui.statusbar.phone.PhoneStatusBarView").isNotNull {
                it.methodFinder().filterByName("onTouchEvent").first().createHook {
                    before { hookParam ->
                        val motionEvent = hookParam.args[0] as MotionEvent
                        when (motionEvent.action) {
                            MotionEvent.ACTION_DOWN -> {
                                point = Point(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
                            }

                            MotionEvent.ACTION_MOVE -> {
                            }

                            MotionEvent.ACTION_UP -> {
                                val isMove = abs(point.y - motionEvent.rawY.toInt()) > 50 || abs(point.x - motionEvent.rawX.toInt()) > 50
                                val isLongChick = motionEvent.eventTime - motionEvent.downTime > 500
                                when (isMove) {
                                    true -> {
                                        if (config.slideStatusBarCutSongs && isPlaying) {
                                            if (abs(point.y - motionEvent.rawY.toInt()) <= config.slideStatusBarCutSongsYRadius) {
                                                val i = point.x - motionEvent.rawX.toInt()
                                                if (abs(i) > config.slideStatusBarCutSongsXRadius) {
                                                    moduleRes.getString(R.string.slide_status_bar_cut_songs).log()
                                                    if (i > 0) {
                                                        shell("input keyevent 87", false)
                                                    } else {
                                                        shell("input keyevent 88", false)
                                                    }
                                                    hookParam.result = true
                                                }
                                            }
                                        }
                                    }

                                    false -> {
                                        when (isLongChick) {
                                            true -> {
                                                if (config.longClickStatusBarStop) {
                                                    moduleRes.getString(R.string.long_click_status_bar_stop).log()
                                                    shell("input keyevent 85", false)
                                                    hookParam.result = true
                                                }
                                            }

                                            false -> {
                                                if (config.clickStatusBarToHideLyric) {
                                                    val isClick = motionEvent.eventTime - motionEvent.downTime < 200
                                                    if (isClick && isPlaying) {
                                                        moduleRes.getString(R.string.click_status_bar_to_hide_lyric).log()
                                                        isHiding.log()
                                                        if (isHiding && lastLyric.isNotEmpty()) {
                                                            isHiding = false
                                                            hookParam.result = true
                                                            changeLyric(lastLyric, 0)
                                                        } else {
                                                            val x = motionEvent.x.toInt()
                                                            val y = motionEvent.y.toInt()
                                                            val left = lyricLayout.left
                                                            val top = lyricLayout.top
                                                            val right = lyricLayout.right
                                                            val bottom = lyricLayout.bottom
                                                            if (x in left..right && y in top..bottom) {
                                                                isHiding = true
                                                                hookParam.result = true
                                                                hideLyric()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        loadClassOrNull("com.android.systemui.statusbar.phone.CentralSurfacesImpl").isNotNull {
            it.methodFinder().filterByName("barMode").first().createHook {
                after { hook ->
                    val mode = hook.result as Int
                    val mIsFullscreen = XposedHelpers.getObjectField(hook.thisObject, "mIsFullscreen") as Boolean
                    if (mIsFullscreen && mode == 0) {
                        isFullScreenMode = true
                        if (isPlaying)
                            hideLyric()
                    } else if ((!mIsFullscreen && mode == 0) || (mIsFullscreen && mode == 1))
                        isFullScreenMode = false
                    "statusBar state: $mode, fullscreen: $mIsFullscreen, in fullscreen mode: $isFullScreenMode".log()
                }
            }
        }
        SystemUISpecial()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag", "MissingPermission")
    private fun lyricInit() {
        val firstLoad = lyricLayout.parent.isNull()
        goMainThread(1) {
            runCatching { (lyricLayout.parent as ViewGroup).removeView(lyricLayout) }
            if (config.viewLocation == 0) {
                targetView.addView(lyricLayout, 0)
            } else {
                targetView.addView(lyricLayout)
            }
            if (isHyperOS && config.mHyperOSTexture) {
                val blurRadio = config.mHyperOSTextureRadio
                val cornerRadius = cornerRadius(config.mHyperOSTextureCorner.toFloat())
                val blendModes = arrayOf(
                    intArrayOf(106, Color.parseColor(config.mHyperOSTextureBgColor)), intArrayOf(3, Color.parseColor(config.mHyperOSTextureBgColor))
                )
                lyricLayout.setBackgroundBlur(blurRadio, cornerRadius, blendModes)
            }
            if (config.lyricWidth == 0) {
                lyricView.setMaxLyricViewWidth(targetView.width.toFloat() - if (config.iconSwitch) config.iconStartMargins.toFloat() + iconView.width else 0f)
            } else {
                var width = scaleWidth().toFloat() + config.lyricEndMargins + config.lyricStartMargins
                if (width > targetView.width) {
                    width = targetView.width.toFloat()
                }
                lyricView.setMaxLyricViewWidth(width)
            }
            themeMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)
            if (config.titleSwitch) {
                object : SystemMediaSessionListener(context) {
                    override fun onTitleChanged(title: String) {
                        super.onTitleChanged(title)
                        "title: $title".log()
                        this@SystemUILyric.title = title
                    }
                }
            }
        }

        if (!firstLoad) return
        val lyricReceiver = LyricReceiver(object : LyricListener() {
            override fun onStop(lyricData: LyricData) {
                if (!isReally) return
                if (isHiding) isHiding = false
                lastLyric = ""
                hideLyric()
            }

            override fun onUpdate(lyricData: LyricData) {
                if (!isReally) return
                val lyric = lyricData.lyric
                lastLyric = lyric
                if (isHiding) return
                changeIcon(lyricData.extraData)
                changeLyric(lastLyric, lyricData.extraData.delay)
            }
        })
        registerLyricListener(context, BuildConfig.API_VERSION, lyricReceiver)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(UpdateConfig(), IntentFilter("updateConfig"), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(UpdateConfig(), IntentFilter("updateConfig"))
        }
        if (config.hideLyricWhenLockScreen) {
            val screenLockFilter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_USER_PRESENT)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(ScreenLockReceiver(), screenLockFilter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(ScreenLockReceiver(), screenLockFilter)
            }
        }
        changeConfig(1)
    }

    private fun changeLyric(lyric: String, delay: Int) {
        if (isHiding || isScreenLock || isFullScreenMode) return
        "lyric:$lyric".log()
        isStop = false
        isPlaying = true
        goMainThread {
            if (config.lyricColor.isEmpty()) lastColor = clockView.currentTextColor
            lyricLayout.showView()
            if (config.hideTime) clockView.hideView()
            if (this::mNotificationIconArea.isInitialized) mNotificationIconArea.hideView()
            if (this::mCarrierLabel.isInitialized) mCarrierLabel.hideView()
            if (this::mMiuiNetworkSpeedView.isInitialized) mMiuiNetworkSpeedView.hideView()
            if (this::mPadClockView.isInitialized) mPadClockView.hideView()
            lyricView.apply {
                width = getLyricWidth(getPaint(), lyric)
                val i = width - theoreticalWidth
                if (config.dynamicLyricSpeed && delay == 0) {
                    if (i > 0) {
                        val proportion = i * 1.0 / displayWidth
                        "proportion:$proportion".log()
                        val speed = 2 * proportion + 0.4
                        "speed:$speed".log()
                        setScrollSpeed(speed.toFloat())
                    }
                }
                if (delay > 0) {
                    if (i > 0) {
                        val d = delay * 1000.0 / 16.0
                        setScrollSpeed(((i / d).toFloat()))
                    }
                }
                if (isRandomAnima) {
                    val animation = randomAnima
                    val interpolator = config.lyricInterpolator
                    val duration = config.animationDuration
                    inAnimation = LyricViewTools.switchViewInAnima(animation, interpolator, duration)
                    outAnimation = LyricViewTools.switchViewOutAnima(animation, duration)
                }
                setText(lyric)
            }
        }
    }

    private fun changeIcon(it: ExtraData) {
        if (!iconSwitch) return
        if (config.changeAllIcons.isNotEmpty()) {
            lastBase64Icon = config.changeAllIcons
        } else {
            val customIcon = it.customIcon && it.base64Icon.isNotEmpty()
            lastBase64Icon = if (customIcon) {
                it.base64Icon
            } else {
                config.getDefaultIcon(it.packageName)
            }
        }
        if (config.lyricWidth == 0) {
            lyricView.setMaxLyricViewWidth(targetView.width.toFloat() - if (config.iconSwitch) config.iconStartMargins.toFloat() + iconView.width else 0f)
        }
    }

    private fun hideLyric(anim: Boolean = true) {
        if (isStop) return
        if (!isHiding && isPlaying && anim) {
            isPlaying = false
            isStop = true
        }
        "isPlaying:$isPlaying".log()
        "Hide Lyric".log()
        goMainThread {
            lyricLayout.hideView(anim)
            clockView.showView()
            if (config.titleSwitch) titleDialog.hideTitle()
            if (this::mNotificationIconArea.isInitialized) mNotificationIconArea.showView()
            if (this::mCarrierLabel.isInitialized) mCarrierLabel.showView()
            if (this::mMiuiNetworkSpeedView.isInitialized) mMiuiNetworkSpeedView.showView()
            if (this::mPadClockView.isInitialized) mPadClockView.showView()
        }
    }

    private fun changeConfig(delay: Long = 0L) {
        "Change Config".log()
        config.update()
        goMainThread(delay) {
            lyricView.apply {
                setTextSize(TypedValue.COMPLEX_UNIT_SHIFT, if (config.lyricSize == 0) clockView.textSize else config.lyricSize.toFloat())
                setPadding(config.lyricStartMargins, config.lyricTopMargins, config.lyricEndMargins, config.lyricBottomMargins)
                if (config.lyricGradientColor.isEmpty()) {
                    if (config.lyricColor.isEmpty()) {
                        when (config.lyricColorScheme) {
                            0 -> setTextColor(clockView.currentTextColor)
                            1 -> textColorAnima(clockView.currentTextColor)
                        }
                    } else {
                        setTextColor(Color.parseColor(config.lyricColor))
                    }
                }
                if (config.lyricWidth == 0) {
                    lyricView.setMaxLyricViewWidth(targetView.width.toFloat() - if (config.iconSwitch) config.iconStartMargins.toFloat() + iconView.width else 0f)
                } else {
                    var width = scaleWidth().toFloat() + config.lyricEndMargins + config.lyricStartMargins
                    if (width > targetView.width) {
                        width = targetView.width.toFloat()
                    }
                    lyricView.setMaxLyricViewWidth(width)
                }
                setLetterSpacings(config.lyricLetterSpacing / 100f)
                strokeWidth(config.lyricStrokeWidth / 100f)
                if (!config.dynamicLyricSpeed) setScrollSpeed(config.lyricSpeed.toFloat())
                if (config.lyricBackgroundColor.isNotEmpty()) {
                    if (config.lyricBackgroundColor.split(",").size < 2) {
                        if (config.lyricBackgroundRadius != 0) {
                            setBackgroundColor(Color.TRANSPARENT)
                            background = GradientDrawable().apply {
                                cornerRadius = config.lyricBackgroundRadius.toFloat()
                                setColor(Color.parseColor(config.lyricBackgroundColor))
                            }
                        } else {
                            setBackgroundColor(Color.parseColor(config.lyricBackgroundColor))
                        }
                    } else {
                        config.lyricBackgroundColor.trim().split(",").map { Color.parseColor(it.trim()) }.let { colors ->
                            val gradientDrawable = GradientDrawable(
                                GradientDrawable.Orientation.LEFT_RIGHT, colors.toIntArray()
                            ).apply {
                                if (config.lyricBackgroundRadius != 0) cornerRadius = config.lyricBackgroundRadius.toFloat()
                            }
                            background = gradientDrawable
                        }
                    }
                }

                val animation = config.lyricAnimation
                isRandomAnima = animation == 9
                if (!isRandomAnima) {
                    val interpolator = config.lyricInterpolator
                    val duration = config.animationDuration
                    inAnimation = LyricViewTools.switchViewInAnima(animation, interpolator, duration)
                    outAnimation = LyricViewTools.switchViewOutAnima(animation, duration)
                }
                runCatching {
                    val file = File("${context.filesDir.path}/font")
                    if (file.exists() && file.canRead()) {
                        setTypeface(Typeface.createFromFile(file))
                    }
                }
            }
            if (!config.iconSwitch) {
                iconView.hideView()
                iconSwitch = false
            } else {
                iconView.showView()
                iconSwitch = true
                iconView.apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                        setMargins(config.iconStartMargins, config.iconTopMargins, 0, config.iconBottomMargins)
                        if (config.iconSize == 0) {
                            width = clockView.height / 2
                            height = clockView.height / 2
                        } else {
                            width = config.iconSize
                            height = config.iconSize
                        }
                    }
                    if (config.iconColor.isEmpty()) {
                        when (config.lyricColorScheme) {
                            0 -> setColorFilter(clockView.currentTextColor, PorterDuff.Mode.SRC_IN)
                            1 -> iconColorAnima(lastColor, clockView.currentTextColor)
                        }
                    } else {
                        when (config.lyricColorScheme) {
                            0 -> setColorFilter(Color.parseColor(config.iconColor), PorterDuff.Mode.SRC_IN)
                            1 -> iconColorAnima(lastColor, Color.parseColor(config.iconColor))
                        }
                    }
                    if (config.iconBgColor.isEmpty()) {
                        setBackgroundColor(Color.TRANSPARENT)
                    } else {
                        setBackgroundColor(Color.parseColor(config.iconBgColor))
                    }
                }
            }
        }
    }

    private fun getLyricWidth(paint: Paint, text: String): Int {
        "Get Lyric Width".log()
        return if (config.lyricWidth == 0) {
            theoreticalWidth = min(paint.measureText(text).toInt(), targetView.width)
            theoreticalWidth
        } else {
            if (config.fixedLyricWidth) {
                scaleWidth()
            } else {
                min(paint.measureText(text).toInt(), scaleWidth())
            }
        }
    }

    private fun scaleWidth(): Int {
        "Scale Width".log()
        return (config.lyricWidth / 100f * if (context.isLandscape()) displayHeight else displayWidth).toInt()
    }

    private fun Class<*>.hasMethod(methodName: String): Boolean {
        val methods = declaredMethods
        for (method in methods) {
            if (method.name == methodName) return true
        }
        return false
    }

    inner class SystemUISpecial {
        init {
            if (isMiui) {
                for (i in 0..10) {
                    val clazz = loadClassOrNull("com.android.keyguard.wallpaper.MiuiKeyguardWallPaperManager\$$i")
                    if (clazz.isNotNull()) {
                        if (clazz!!.hasMethod("onWallpaperChanged")) {
                            clazz.methodFinder().filterByName("onWallpaperChanged").first().createHook {
                                after {
                                    if (this@SystemUILyric::clockView.isInitialized) {
                                        "onWallpaperChanged".log()
                                        canLoad = true
                                        hideLyric()
                                    }
                                }
                            }
                        }
                        break
                    }
                }
            }

            if (togglePrompts) {
                loadClassOrNull("com.android.systemui.SystemUIApplication").isNotNull { clazz ->
                    clazz.methodFinder().filterByName("onConfigurationChanged").first().createHook {
                        after { hookParam ->
                            "onConfigurationChanged".log()
                            val newConfig = hookParam.args[0] as Configuration
                            themeMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
                        }
                    }
                    if (isMiui && config.mMiuiPadOptimize) {
                        clazz.methodFinder().filterByName("onCreate").first().createHook {
                            after {
                                if (isPad) {
                                    loadClassOrNull("com.android.systemui.statusbar.phone.MiuiCollapsedStatusBarFragment").isNotNull {
                                        if (it.hasMethod("initMiuiViewsOnViewCreated")) {
                                            it.methodFinder().filterByName("initMiuiViewsOnViewCreated").first()
                                        } else {
                                            it.methodFinder().filterByName("onViewCreated").first()
                                        }.let { method ->
                                            method.createHook {
                                                after { hookParam ->
                                                    hookParam.thisObject.objectHelper {
                                                        mPadClockView = this.getObjectOrNullAs<View>("mPadClockView")!!
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isMiui && config.mMiuiHideNetworkSpeed) {
                moduleRes.getString(R.string.miui_hide_network_speed).log()
                loadClassOrNull("com.android.systemui.statusbar.views.NetworkSpeedView").isNotNull {
                    it.constructorFinder().first().createHook {
                        after { hookParam ->
                            mMiuiNetworkSpeedView = hookParam.thisObject as TextView
                        }
                    }
                    it.methodFinder().filterByName("setVisibilityByController").first().createHook {
                        before { hookParam ->
                            if (isPlaying) hookParam.args[0] = false
                        }
                    }
                }
            }
        }
    }

    inner class UpdateConfig : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("type")) {
                "normal" -> {
                    if (!isReally) return
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
                hideLyric(false)
            } else {
                if (lastLyric.isNotEmpty()) {
                    changeLyric(lastLyric, 0)
                    lastColor = clockView.currentTextColor
                }
            }
        }
    }
}
