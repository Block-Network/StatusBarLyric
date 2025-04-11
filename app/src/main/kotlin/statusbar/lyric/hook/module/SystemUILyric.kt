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

package statusbar.lyric.hook.module

import android.annotation.SuppressLint
import android.app.AndroidAppHelper
import android.app.Application
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
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.core.util.Consumer
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import com.github.kyuubiran.ezxhelper.HookFactory
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.hchen.superlyricapi.ISuperLyric
import com.hchen.superlyricapi.SuperLyricData
import com.hchen.superlyricapi.SuperLyricTool
import com.hchen.superlyricapi.SuperLyricTool.base64ToBitmap
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP.config
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.tools.BlurTools.cornerRadius
import statusbar.lyric.tools.BlurTools.setBackgroundBlur
import statusbar.lyric.tools.LogTools.log
import statusbar.lyric.tools.LyricViewTools
import statusbar.lyric.tools.LyricViewTools.cancelAnimation
import statusbar.lyric.tools.LyricViewTools.hideView
import statusbar.lyric.tools.LyricViewTools.randomAnima
import statusbar.lyric.tools.LyricViewTools.showView
import statusbar.lyric.tools.Tools.callMethod
import statusbar.lyric.tools.Tools.existField
import statusbar.lyric.tools.Tools.existMethod
import statusbar.lyric.tools.Tools.getObjectField
import statusbar.lyric.tools.Tools.getObjectFieldIfExist
import statusbar.lyric.tools.Tools.goMainThread
import statusbar.lyric.tools.Tools.ifNotNull
import statusbar.lyric.tools.Tools.isLandscape
import statusbar.lyric.tools.Tools.isNot
import statusbar.lyric.tools.Tools.isNotNull
import statusbar.lyric.tools.Tools.isPad
import statusbar.lyric.tools.Tools.isTargetView
import statusbar.lyric.tools.Tools.observableChange
import statusbar.lyric.tools.Tools.setObjectField
import statusbar.lyric.tools.Tools.shell
import statusbar.lyric.tools.XiaomiUtils.isHyperOS
import statusbar.lyric.tools.XiaomiUtils.isXiaomi
import statusbar.lyric.view.LyricSwitchView
import statusbar.lyric.view.TitleDialog
import java.io.File
import kotlin.math.abs
import kotlin.math.min

class SystemUILyric : BaseHook() {
    val context: Context by lazy { AndroidAppHelper.currentApplication() }

    private var lastColor: Int by observableChange(Color.WHITE) { oldValue, newValue ->
        if (oldValue == newValue) return@observableChange
        "Changing Color: $newValue".log()
        goMainThread {
            if (config.lyricColor.isEmpty() && config.lyricGradientColor.isEmpty()) {
                lyricView.setTextColor(newValue)
            }
            if (config.iconColor.isEmpty()) {
                iconView.setColorFilter(newValue, PorterDuff.Mode.SRC_IN)
            }
        }
    }
    var lastLyric: String = ""
    private var title: String by observableChange("") { _, newValue ->
        if (!config.titleShowWithSameLyric && lastLyric == newValue) return@observableChange
        // if (!isPlaying) return@observableChange
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
            base64ToBitmap(newValue).isNotNull {
                iconView.showView()
                iconView.setImageBitmap(it)
            }.isNot {
                iconView.hideView()
            }
            "Changing Icon".log()
        }
    }
    private var canLoad: Boolean = true
    private var isScreenLocked: Boolean = false
    private var iconSwitch: Boolean = config.iconSwitch

    @Volatile
    var isMusicPlaying: Boolean = false

    @Volatile
    var isHiding: Boolean = false
    private var isClickHiding: Boolean = false
    private var isRandomAnima: Boolean = false
    private var autoHideController: Any? = null
    val isReady: Boolean get() = this@SystemUILyric::clockView.isInitialized

    private var theoreticalWidth: Int = 0
    private lateinit var point: Point


    private val displayMetrics: DisplayMetrics by lazy { context.resources.displayMetrics }
    private val displayWidth: Int by lazy { displayMetrics.widthPixels }
    private val displayHeight: Int by lazy { displayMetrics.heightPixels }


    private lateinit var clockView: TextView
    private lateinit var targetView: ViewGroup
    private lateinit var notificationIconArea: View
    private lateinit var carrierLabel: View
    private lateinit var padClockView: View
    private val lyricView: LyricSwitchView by lazy {
        object : LyricSwitchView(context) {
            override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
                super.onSizeChanged(w, h, oldw, oldh)
                if (config.lyricGradientColor.isNotEmpty()) {
                    config.lyricGradientColor.trim().split(",").map { it.trim().toColorInt() }
                        .let { colors ->
                            if (colors.isEmpty()) {
                                setTextColor(Color.WHITE)
                            } else if (colors.size < 2) {
                                setTextColor(colors[0])
                            } else {
                                val textShader = LinearGradient(
                                    0f, 0f, width.toFloat(),
                                    0f, colors.toIntArray(), null, Shader.TileMode.CLAMP
                                )
                                setLinearGradient(textShader)
                            }
                        }
                }
            }
        }.apply {
            if (!isReady) return@apply
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
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            addView(iconView)
            addView(lyricView)
            visibility = View.GONE
        }
    }
    private lateinit var miuiNetworkSpeedView: TextView
    private val titleDialog by lazy {
        TitleDialog(context)
    }

    //////////////////////////////Hook//////////////////////////////////////
    private var defaultDisplay: Any? = null
    private var centralSurfacesImpl: Any? = null
    private var notificationBigTime: View? = null
    private var statusBatteryContainer: View? = null

    @SuppressLint("DiscouragedApi", "NewApi")
    override fun init() {
        "Initializing Hook".log()
        Application::class.java.methodFinder().filterByName("attach").first().createHook {
            after { hook ->
                registerSuperLyric(hook.args[0] as Context)
            }
        }

        loadClassOrNull(config.textViewClassName).isNotNull {
            TextView::class.java.methodFinder().filterByName("onAttachedToWindow").first().createHook {
                after { hookParam ->
                    val view = (hookParam.thisObject as View)
                    if (view.isTargetView()) {
                        if (!canLoad) return@after
                        clockView = view as TextView
                        targetView = (clockView.parent as LinearLayout).apply {
                            gravity = Gravity.CENTER
                        }
                        canLoad = false
                        lyricInit()
                        // if (!togglePrompts) hook.unhook()
                    }
                }
            }

            View::class.java.methodFinder().filterByName("onDetachedFromWindow").first()
                .createHook {
                    after { hookParam ->
                        val view = (hookParam.thisObject as View)
                        if (view.isTargetView()) {
                            "Running onDetachedFromWindow".log()
                            canLoad = true
                            updateLyricState(showLyric = false)
                        }
                    }
                }

            View::class.java.methodFinder().filterByName("setVisibility").first()
                .createHook {
                    before { param ->
                        val view = param.thisObject as View
                        if (statusBatteryContainer != null) {
                            if (statusBatteryContainer != view) return@before
                            if (!isMusicPlaying) return@before

                            val visibility = param.args[0] == View.VISIBLE
                            if (visibility) {
                                updateLyricState(lastLyric)
                            } else {
                                updateLyricState(showLyric = false)
                            }
                        } else {
                            val idName = runCatching { view.resources.getResourceEntryName(view.id) }.getOrNull()
                            if (idName != null && idName == "system_icons") {
                                statusBatteryContainer = view;
                            }
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
                    if (isMusicPlaying && !isHiding) {
                        if (hookParam.args[0] == View.VISIBLE) {
                            val view = hookParam.thisObject as View
                            if (
                                (isReady && clockView == view && config.hideTime) ||
                                (this@SystemUILyric::notificationIconArea.isInitialized && notificationIconArea == view && config.hideNotificationIcon) ||
                                (this@SystemUILyric::carrierLabel.isInitialized && carrierLabel == view && config.hideCarrier) ||
                                (this@SystemUILyric::miuiNetworkSpeedView.isInitialized && miuiNetworkSpeedView == view && config.mMiuiHideNetworkSpeed) ||
                                (this@SystemUILyric::padClockView.isInitialized && padClockView == view && config.hideTime)
                            ) {
                                hookParam.args[0] = View.GONE
                            }
                        }
                    }
                }
            }
        }

        // 状态栏图标颜色更改
        loadClassOrNull("com.android.systemui.statusbar.phone.DarkIconDispatcherImpl").isNotNull {
            it.methodFinder().filterByName("applyDarkIntensity").first().createHook {
                after { hookParam ->
                    if (!isMusicPlaying) return@after

                    val mIconTint = hookParam.thisObject.objectHelper().getObjectOrNullAs<Int>("mIconTint")
                    lastColor = mIconTint ?: Color.BLACK
                }
            }
        }

        if (config.hideNotificationIcon) {
            moduleRes.getString(R.string.hide_notification_icon).log()
            fun HookFactory.hideNoticeIcon(mode: Int) {
                after { hookParam ->
                    val clazz = hookParam.thisObject::class.java
                    val name = if (mode == 0) "NotificationIconAreaController" else "CollapsedStatusBarFragment"
                    val method = if (mode == 0) "mNotificationIconArea" else "mNotificationIconAreaInner"
                    if (clazz.simpleName == name) {
                        hookParam.thisObject.objectHelper {
                            notificationIconArea = this.getObjectOrNullAs<View>(method)!!
                        }
                    } else {
                        notificationIconArea = clazz.superclass.getField(method).get(hookParam.thisObject) as View
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                loadClassOrNull("com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment").isNotNull {
                    it.methodFinder().filterByName("onViewCreated").first().createHook {
                        hideNoticeIcon(1)
                    }
                }
            } else {
                loadClassOrNull("com.android.systemui.statusbar.phone.NotificationIconAreaController").isNotNull {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.constructorFinder().first().createHook {
                            hideNoticeIcon(0)
                        }
                    } else {
                        it.methodFinder().filterByName("initializeNotificationAreaViews").first()
                            .createHook {
                                hideNoticeIcon(0)
                            }
                    }
                }
            }
        }

        if (config.hideCarrier && Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            moduleRes.getString(R.string.hide_carrier).log()
            loadClassOrNull("com.android.systemui.statusbar.phone.KeyguardStatusBarView").isNotNull {
                it.methodFinder().filterByName("onFinishInflate").firstOrNull().ifNotNull { method ->
                    method.createHook {
                        after { hookParam ->
                            kotlin.runCatching {
                                val clazz = hookParam.thisObject::class.java
                                if (clazz.simpleName == "KeyguardStatusBarView") {
                                    hookParam.thisObject.objectHelper {
                                        carrierLabel = this.getObjectOrNullAs<View>("mCarrierLabel")!!
                                    }
                                } else {
                                    carrierLabel = clazz.superclass.getField("mCarrierLabel").get(hookParam.thisObject) as View
                                }
                            }.onFailure { throwable -> "Hook carrier error: $throwable".log() }
                        }
                    }
                }
            }
        }

        // 触摸监听
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
                                    if (config.slideStatusBarCutSongs) {
                                        if (isMusicPlaying) {
                                            if (isHiding) return@before

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
                                }

                                false -> {
                                    when (isLongChick) {
                                        true -> {
                                            if (config.longClickStatusBarStop) {
                                                if (isHiding) return@before

                                                moduleRes.getString(R.string.long_click_status_bar_stop).log()
                                                shell("input keyevent 85", false)
                                                hookParam.result = true
                                            }
                                        }

                                        false -> {
                                            if (config.clickStatusBarToHideLyric || FocusNotifyController.isOS2FocusNotifyShowing) {
                                                if (!isMusicPlaying) return@before
                                                if (FocusNotifyController.isOS1FocusNotifyShowing) return@before

                                                moduleRes.getString(R.string.click_status_bar_to_hide_lyric).log()
                                                if (isHiding) {
                                                    if (FocusNotifyController.canControlFocusNotify()) {
                                                        if (!FocusNotifyController.isHideFocusNotify && FocusNotifyController.shouldOpenFocusNotify(motionEvent)) {
                                                            "Should open focus notify".log()
                                                            return@before
                                                        }
                                                    }
                                                    isClickHiding = false
                                                    hookParam.result = true
                                                    updateLyricState(lastLyric)
                                                    autoHideStatusBarInFullScreenModeIfNeed()
                                                } else {
                                                    val x = motionEvent.x.toInt()
                                                    val y = motionEvent.y.toInt()
                                                    val left = lyricLayout.left
                                                    val top = lyricLayout.top
                                                    val right = lyricLayout.right
                                                    val bottom = lyricLayout.bottom
                                                    if (x in left..right && y in top..bottom) {
                                                        isClickHiding = true
                                                        hookParam.result = true
                                                        updateLyricState(showLyric = false)
                                                        autoHideStatusBarInFullScreenModeIfNeed()
                                                    }
                                                    "Change to hide LyricView: $isHiding".log()
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

        // 屏幕状态
        loadClassOrNull("com.android.systemui.statusbar.phone.CentralSurfacesImpl").isNotNull {
            it.constructorFinder().firstOrNull().ifNotNull { constructor ->
                constructor.createHook {
                    after { hook ->
                        centralSurfacesImpl = hook.thisObject
                        autoHideController = hook.thisObject.getObjectField("mAutoHideController")
                        val mStatusBarModeRepository = hook.thisObject.getObjectFieldIfExist("mStatusBarModeRepository")
                        defaultDisplay = mStatusBarModeRepository?.getObjectFieldIfExist("defaultDisplay")
                    }
                }
            }
        }

        FocusNotifyController.init(this)

        SystemUISpecial()
    }

    private var statusBarShowing: Boolean = true

    // 适合考虑状态的更新
    fun updateLyricState(lyric: String = "", showLyric: Boolean = true, showFocus: Boolean = true, delay: Int = 0) {
        if (isInFullScreenMode() &&
            (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                || context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
        ) {
            if (statusBarShowing && isMusicPlaying && showLyric && canShowLyric()) {
                showLyric(lyric, delay)
                FocusNotifyController.hideFocusNotifyIfNeed()
                "StatusBar state is showing".log()
            } else {
                hideLyric()
                if (showFocus)
                    FocusNotifyController.showFocusNotifyIfNeed()
                if (!statusBarShowing) "StatusBar state is hiding".log()
            }
        } else {
            if (showLyric && canShowLyric()) {
                showLyric(lyric, delay)
                FocusNotifyController.hideFocusNotifyIfNeed()
            } else {
                hideLyric()
                if (showFocus)
                    FocusNotifyController.showFocusNotifyIfNeed()
            }
        }
    }

    private fun canShowLyric(): Boolean {
        // 不存在焦点通知和不是手动隐藏时可以显示歌词
        return !FocusNotifyController.isOS1FocusNotifyShowing && (!isClickHiding || !FocusNotifyController.isOS2FocusNotifyShowing)
    }

    private fun isInFullScreenMode(): Boolean {
        var isInFullScreenMode = false

        if (centralSurfacesImpl.existField("mIsFullscreen")) {
            isInFullScreenMode = centralSurfacesImpl?.getObjectField("mIsFullscreen") as Boolean
            statusBarShowing = centralSurfacesImpl?.getObjectField("mTransientShown") as Boolean
        } else if (defaultDisplay.existField("isInFullscreenMode")) {
            val isInFullscreenMode = defaultDisplay?.getObjectField("isInFullscreenMode")
            isInFullScreenMode = isInFullscreenMode?.getObjectField("$\$delegate_0")?.callMethod("getValue") as Boolean

            val isTransientShown = defaultDisplay?.getObjectField("isTransientShown")
            statusBarShowing = isTransientShown?.getObjectField("$\$delegate_0")?.callMethod("getValue") as Boolean
        }

        return isInFullScreenMode
    }

    private fun autoHideStatusBarInFullScreenModeIfNeed() {
        if (autoHideController == null) return
        if (!isInFullScreenMode()) return

        autoHideController!!.callMethod("touchAutoHide")
    }

    private fun lyricInit() {
        // val firstLoad = lyricLayout.parent.isNull()
        goMainThread(1) {
            "LyricView init".log()
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
                    intArrayOf(106, config.mHyperOSTextureBgColor.toColorInt()),
                    intArrayOf(3, config.mHyperOSTextureBgColor.toColorInt())
                )
                lyricLayout.setBackgroundBlur(blurRadio, cornerRadius, blendModes)
            }
        }

        // if (!firstLoad) return
        updateConfig(1)
    }

    private var lastArtist: String = ""
    private var lastAlbum: String = ""
    private var updateConfig: UpdateConfig = UpdateConfig()
    private var screenLockReceiver: ScreenLockReceiver = ScreenLockReceiver()
    private var playingApp: String = ""
    private val timeoutRestore: Int = 0
    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == timeoutRestore && isMusicPlaying && config.timeoutRestore) {
                updateLyricState(showLyric = false)
                playingApp = ""
                lastLyric = ""
                "Timeout restore".log()
            }
        }
    }
    private var lastRunnable: Runnable? = null
    private val showTitleConsumer: Consumer<SuperLyricData> = object : Consumer<SuperLyricData> {
        override fun accept(value: SuperLyricData) {
            if (!isMusicPlaying) return
            if (playingApp != value.packageName) return

            this@SystemUILyric.title = value.title
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerSuperLyric(context: Context) {
        SuperLyricTool.registerSuperLyric(context, object : ISuperLyric.Stub() {
            override fun onStop(data: SuperLyricData?) {
                if (data == null) return
                if (!isReady) return
                if (data.playbackState?.state == 6) return
                if (playingApp.isNotEmpty()) {
                    if ((data.packageName.ifNotNull { playingApp != data.packageName }
                            ?: false) as Boolean)
                        return
                }

                isMusicPlaying = false
                lastLyric = ""
                playingApp = ""
                if (lastRunnable != null)
                    handler.removeCallbacks(lastRunnable!!)

                updateLyricState(showLyric = false)
                if (handler.hasMessages(timeoutRestore)) {
                    handler.removeMessages(timeoutRestore)
                }
            }

            override fun onSuperLyric(data: SuperLyricData?) {
                if (data == null) return
                if (!isReady) return

                playingApp = data.packageName
                if (data.isExistMediaMetadata) {
                    if (config.titleSwitch) {
                        if (lastArtist != data.artist || lastAlbum != data.album) {
                            lastArtist = data.artist
                            lastAlbum = data.album

                            if (lastRunnable != null)
                                handler.removeCallbacks(lastRunnable!!)
                            lastRunnable = Runnable { showTitleConsumer.accept(data) }

                            ("Title: " + data.title + ", Artist: " + lastArtist + ", Album: " + lastAlbum).log()
                        }
                    }
                }
                if (data.lyric.isEmpty()) return
                isMusicPlaying = true
                lastLyric = data.lyric
                if (lastRunnable != null)
                    handler.postDelayed(lastRunnable!!, 800)

                changeIcon(data)
                updateLyricState(lastLyric, delay = data.delay)
                if (handler.hasMessages(timeoutRestore)) {
                    handler.removeMessages(timeoutRestore)
                    handler.sendEmptyMessageDelayed(timeoutRestore, 10000L)
                } else handler.sendEmptyMessageDelayed(timeoutRestore, 10000L)
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                updateConfig,
                IntentFilter("updateConfig"),
                Context.RECEIVER_EXPORTED
            )
        } else {
            context.registerReceiver(updateConfig, IntentFilter("updateConfig"))
        }

        if (config.hideLyricWhenLockScreen) {
            val screenLockFilter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_USER_PRESENT)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(
                    screenLockReceiver,
                    screenLockFilter,
                    Context.RECEIVER_EXPORTED
                )
            } else {
                context.registerReceiver(screenLockReceiver, screenLockFilter)
            }
        }

        "Register SuperLyric".log()
    }

    // 适用于直接显示歌词，不需要考虑其他类似焦点通知的状态
    private fun showLyric(lyric: String, delay: Int = 0) {
        if (!isReady || !isMusicPlaying || lyric.isEmpty() || isScreenLocked)
            return

        "Showing LyricView".log()
        goMainThread {
            isHiding = false
            lastColor = clockView.currentTextColor
            lyricLayout.cancelAnimation()
            lyricLayout.showView()
            if (config.hideTime) {
                clockView.hideView()
                if (this::padClockView.isInitialized) padClockView.hideView()
            }
            if (this::notificationIconArea.isInitialized && config.hideNotificationIcon) notificationIconArea.hideView()
            if (this::carrierLabel.isInitialized && config.hideCarrier) carrierLabel.hideView()
            if (this::miuiNetworkSpeedView.isInitialized && config.mMiuiHideNetworkSpeed) miuiNetworkSpeedView.hideView()

            lyricView.apply {
                width = getLyricWidth(getPaint(), lyric)
                val i = width - theoreticalWidth
                if (config.dynamicLyricSpeed && delay == 0) {
                    if (i > 0) {
                        val proportion = i * 1.0 / displayWidth
                        "Proportion: $proportion".log()
                        val speed = 2 * proportion + 0.4
                        "Speed: $speed".log()
                        setScrollSpeed(speed.toFloat())
                    }
                }
                if (delay > 0) {
                    if (i > 0) {
                        val delayInSeconds = delay / 1000.0
                        val framesCount = delayInSeconds * 60
                        setScrollSpeed(((i / framesCount).toFloat()))
                    }
                }
                if (isRandomAnima) {
                    val animation = randomAnima
                    val interpolator = config.lyricInterpolator
                    val duration = config.animationDuration
                    inAnimation =
                        LyricViewTools.switchViewInAnima(animation, interpolator, duration)
                    outAnimation = LyricViewTools.switchViewOutAnima(animation, duration)
                }
                setText(lyric)
            }
        }
    }

    private fun changeIcon(it: SuperLyricData) {
        if (!iconSwitch) return
        if (!isMusicPlaying) return

        lastBase64Icon = config.changeAllIcons.ifEmpty {
            if (it.base64Icon != "") {
                it.base64Icon
            } else {
                config.getDefaultIcon(it.packageName)
            }
        }
    }

    // 适用于不考虑状态的隐藏
    private fun hideLyric(anim: Boolean = true) {
        if (!isReady) return
        if (isHiding) return
        isHiding = true

        "Hiding LyricView".log()
        goMainThread {
            lyricLayout.hideView(anim)
            clockView.showView()
            notificationBigTime?.visibility = View.VISIBLE
            if (config.titleSwitch) titleDialog.hideTitle()
            if (this::notificationIconArea.isInitialized) notificationIconArea.showView()
            if (this::carrierLabel.isInitialized) carrierLabel.showView()
            if (this::miuiNetworkSpeedView.isInitialized) miuiNetworkSpeedView.showView()
            if (this::padClockView.isInitialized) padClockView.showView()
        }
    }

    private fun updateConfig(delay: Long = 0L) {
        "Updating Config".log()
        config.update()
        goMainThread(delay) {
            lyricView.apply {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SHIFT,
                    if (config.lyricSize == 0) clockView.textSize else config.lyricSize.toFloat()
                )
                setMargins(
                    config.lyricStartMargins,
                    config.lyricTopMargins,
                    config.lyricEndMargins,
                    config.lyricBottomMargins
                )
                if (config.lyricGradientColor.isEmpty()) {
                    if (config.lyricColor.isEmpty()) {
                        setTextColor(clockView.currentTextColor)
                    } else {
                        setTextColor(config.lyricColor.toColorInt())
                    }
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
                                setColor(config.lyricBackgroundColor.toColorInt())
                            }
                        } else {
                            setBackgroundColor(config.lyricBackgroundColor.toColorInt())
                        }
                    } else {
                        config.lyricBackgroundColor.trim().split(",").map { it.trim().toColorInt() }
                            .let { colors ->
                                val gradientDrawable = GradientDrawable(
                                    GradientDrawable.Orientation.LEFT_RIGHT, colors.toIntArray()
                                ).apply {
                                    if (config.lyricBackgroundRadius != 0) {
                                        cornerRadius = config.lyricBackgroundRadius.toFloat()
                                    }
                                }
                                background = gradientDrawable
                            }
                    }
                }

                val animation = config.lyricAnimation
                isRandomAnima = animation == 11
                if (!isRandomAnima) {
                    val interpolator = config.lyricInterpolator
                    val duration = config.animationDuration
                    inAnimation =
                        LyricViewTools.switchViewInAnima(animation, interpolator, duration)
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
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    ).apply {
                        setMargins(
                            config.iconStartMargins,
                            config.iconTopMargins,
                            0,
                            config.iconBottomMargins
                        )
                        if (config.iconSize == 0) {
                            width = clockView.height / 2
                            height = clockView.height / 2
                        } else {
                            width = config.iconSize
                            height = config.iconSize
                        }
                    }
                    if (config.iconColor.isEmpty()) {
                        setColorFilter(clockView.currentTextColor, PorterDuff.Mode.SRC_IN)
                    } else {
                        setColorFilter(config.iconColor.toColorInt(), PorterDuff.Mode.SRC_IN)
                    }
                    if (config.iconBgColor.isEmpty()) {
                        setBackgroundColor(Color.TRANSPARENT)
                    } else {
                        setBackgroundColor(config.iconBgColor.toColorInt())
                    }
                }
            }
        }
    }

    private fun getLyricWidth(paint: Paint, text: String): Int {
        "Getting Lyric Width".log()
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

    inner class SystemUISpecial {
        init {
            if (isXiaomi) {
                loadClassOrNull("com.android.systemui.controlcenter.shade.NotificationHeaderExpandController\$notificationCallback$1").isNotNull {
                    it.methodFinder().filterByName("onExpansionChanged").first().createHook {
                        before { hook ->
                            if (isMusicPlaying && !isHiding && config.hideTime) {
                                val notificationHeaderExpandController =
                                    hook.thisObject.getObjectField("this$0")
                                notificationHeaderExpandController?.setObjectField(
                                    "bigTimeTranslationY",
                                    0
                                )
                                notificationHeaderExpandController?.setObjectField(
                                    "notificationTranslationX",
                                    0
                                )
                                // notificationHeaderExpandController?.setObjectField("notificationTranslationY", 0)

                                val notificationBigTime =
                                    notificationHeaderExpandController?.getObjectField("headerController")
                                        ?.callMethod("get")
                                        ?.getObjectField("notificationBigTime") as View

                                val f = hook.args[0] as Float
                                if (f < 0.75f)
                                    notificationBigTime.visibility = View.GONE
                                else
                                    notificationBigTime.visibility = View.VISIBLE

                                this@SystemUILyric.notificationBigTime = notificationBigTime
                            }
                        }
                    }
                }

                if (config.mMiuiHideNetworkSpeed) {
                    moduleRes.getString(R.string.miui_hide_network_speed).log()
                    loadClassOrNull("com.android.systemui.statusbar.views.NetworkSpeedView").isNotNull {
                        it.constructorFinder().first().createHook {
                            after { hookParam ->
                                miuiNetworkSpeedView = hookParam.thisObject as TextView
                            }
                        }
                        it.methodFinder().filterByName("setVisibilityByController").first()
                            .createHook {
                                before { hookParam ->
                                    if (isMusicPlaying) hookParam.args[0] = false
                                }
                            }
                    }
                }

                for (i in 0..10) {
                    val clazz =
                        loadClassOrNull("com.android.keyguard.wallpaper.MiuiKeyguardWallPaperManager$$i")
                    if (clazz.isNotNull()) {
                        if (clazz!!.existMethod("onWallpaperChanged")) {
                            clazz.methodFinder().filterByName("onWallpaperChanged").first()
                                .createHook {
                                    after {
                                        "onWallpaperChanged".log()
                                        canLoad = true
                                        hideLyric()
                                    }
                                }
                            break
                        }
                    }
                }
            }

            loadClassOrNull("com.android.systemui.SystemUIApplication").isNotNull { clazz ->
                clazz.methodFinder().filterByName("onConfigurationChanged").first().createHook {
                    after { hookParam ->
                        "onConfigurationChanged".log()
                        val newConfig = hookParam.args[0] as Configuration

                        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            if (!isReady) return@after
                            updateLyricState()
                        }
                    }
                }
                if (isXiaomi && config.mMiuiPadOptimize) {
                    clazz.methodFinder().filterByName("onCreate").first().createHook {
                        after {
                            if (isPad) {
                                loadClassOrNull("com.android.systemui.statusbar.phone.MiuiCollapsedStatusBarFragment").isNotNull {
                                    if (it.existMethod("initMiuiViewsOnViewCreated")) {
                                        it.methodFinder().filterByName("initMiuiViewsOnViewCreated")
                                            .first()
                                    } else {
                                        it.methodFinder().filterByName("onViewCreated").first()
                                    }.let { method ->
                                        method.createHook {
                                            after { hookParam ->
                                                hookParam.thisObject.objectHelper {
                                                    padClockView =
                                                        this.getObjectOrNullAs<View>("mPadClockView")!!
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

    inner class UpdateConfig : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("type")) {
                "normal" -> {
                    if (!isReady) return
                    updateConfig()
                }

                "change_font" -> {}
                "reset_font" -> {}
            }
        }
    }

    inner class ScreenLockReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isScreenLocked = intent.action == Intent.ACTION_SCREEN_OFF
            "isScreenLocked: $isScreenLocked".log()
            if (isScreenLocked) {
                updateLyricState(showLyric = false)
            } else {
                if (isMusicPlaying && lastLyric.isNotEmpty()) {
                    updateLyricState(lastLyric)
                }
            }
        }
    }
}
