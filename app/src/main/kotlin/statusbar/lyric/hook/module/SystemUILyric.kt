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
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.session.PlaybackState
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
import statusbar.lyric.hook.module.xiaomi.FocusNotifyController
import statusbar.lyric.hook.module.xiaomi.XiaomiHooks
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
import statusbar.lyric.tools.Tools.getObjectField
import statusbar.lyric.tools.Tools.getObjectFieldIfExist
import statusbar.lyric.tools.Tools.goMainThread
import statusbar.lyric.tools.Tools.ifNotNull
import statusbar.lyric.tools.Tools.isLandscape
import statusbar.lyric.tools.Tools.isNot
import statusbar.lyric.tools.Tools.isNotNull
import statusbar.lyric.tools.Tools.isTargetView
import statusbar.lyric.tools.Tools.observableChange
import statusbar.lyric.tools.Tools.shell
import statusbar.lyric.tools.XiaomiUtils.isHyperOS
import statusbar.lyric.view.LyricSwitchView
import statusbar.lyric.view.TitleDialog
import java.io.File
import kotlin.math.abs
import kotlin.math.min

class SystemUILyric : BaseHook() {
    private val context: Context by lazy { AndroidAppHelper.currentApplication() }

    private var lastLyric: String = ""
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
    private var title: String by observableChange("") { _, newValue ->
        if (!config.titleShowWithSameLyric && lastLyric == newValue) return@observableChange
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
    private var isRandomAnima: Boolean = false
    private var autoHideController: Any? = null
    private val isReady: Boolean get() = this@SystemUILyric::clockView.isInitialized

    private var theoreticalWidth: Int = 0
    private lateinit var point: Point


    private val displayMetrics: DisplayMetrics by lazy { context.resources.displayMetrics }
    private val displayWidth: Int by lazy { displayMetrics.widthPixels }
    private val displayHeight: Int by lazy { displayMetrics.heightPixels }


    private lateinit var clockView: TextView
    private lateinit var targetView: ViewGroup


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
    private val titleDialog by lazy {
        TitleDialog(context)
    }

    //////////////////////////////Hook//////////////////////////////////////
    private var defaultDisplay: Any? = null
    private var centralSurfacesImpl: Any? = null
    private var notificationIconArea: View? = null
    private var statusBatteryContainer: View? = null

    @SuppressLint("DiscouragedApi", "NewApi")
    override fun init() {
        "Initializing Hook".log()
        Application::class.java.methodFinder().filterByName("attach").single().createHook {
            after { hook ->
                registerSuperLyric(hook.args[0] as Context)
            }
        }

        loadClassOrNull(config.textViewClassName).isNotNull {
            TextView::class.java.methodFinder().filterByName("onLayout").single()
                .createHook {
                    after { hookParam ->
                        if (!canLoad) return@after

                        val view = (hookParam.thisObject as View)
                        if (view.isTargetView()) {
                            clockView = view as TextView
                            targetView = (clockView.parent as LinearLayout).apply {
                                gravity = Gravity.CENTER
                            }
                            canLoad = false
                            lyricInit()
                        }
                    }
                }

            View::class.java.methodFinder().filterByName("onDetachedFromWindow").single()
                .createHook {
                    after { hookParam ->
                        val view = (hookParam.thisObject as View)
                        if (view.isTargetView()) {
                            "Running onDetachedFromWindow".log()
                            canLoad = true
                            updateLyricState(showLyric = false, showFocus = false)
                        }
                    }
                }

            View::class.java.methodFinder().filterByName("setVisibility").single()
                .createHook {
                    before { param ->
                        val view = param.thisObject as View
                        if (statusBatteryContainer.isNotNull()) {
                            if (statusBatteryContainer != view) return@before
                            if (!isMusicPlaying) return@before

                            val visibility = param.args[0] == View.VISIBLE
                            if (visibility) {
                                updateLyricState()
                            } else {
                                updateLyricState(showLyric = false)
                            }
                        } else {
                            val idName =
                                runCatching { view.resources.getResourceEntryName(view.id) }.getOrNull()
                            if (idName.isNotNull() && idName == "system_icons") {
                                statusBatteryContainer = view
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
            View::class.java.methodFinder().filterByName("setVisibility").single().createHook {
                before { hookParam ->
                    if (isMusicPlaying && !isHiding) {
                        if (hookParam.args[0] == View.VISIBLE) {
                            val view = hookParam.thisObject as View
                            if (
                                (isReady && clockView == view && config.hideTime) ||
                                (notificationIconArea == view && config.hideNotificationIcon) ||
                                (XiaomiHooks.getCarrierLabel() == view && config.hideCarrier) ||
                                (XiaomiHooks.getMiuiNetworkSpeedView() == view && config.mMiuiHideNetworkSpeed) ||
                                (XiaomiHooks.getPadClockView() == view && config.hideTime)
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
            it.methodFinder().filterByName("applyDarkIntensity").filterNonAbstract().single().createHook {
                after { hookParam ->
                    if (!isMusicPlaying) return@after

                    val mIconTint =
                        hookParam.thisObject.objectHelper().getObjectOrNullAs<Int>("mIconTint")
                    lastColor = mIconTint ?: Color.BLACK
                }
            }
        }

        if (config.hideNotificationIcon) {
            moduleRes.getString(R.string.hide_notification_icon).log()
            fun HookFactory.hideNoticeIcon(mode: Int) {
                after { hookParam ->
                    val clazz = hookParam.thisObject::class.java
                    val name =
                        if (mode == 0) "NotificationIconAreaController" else "CollapsedStatusBarFragment"
                    val method =
                        if (mode == 0) "mNotificationIconArea" else "mNotificationIconAreaInner"
                    if (clazz.simpleName == name) {
                        hookParam.thisObject.objectHelper {
                            notificationIconArea = this.getObjectOrNullAs<View>(method)!!
                        }
                    } else {
                        notificationIconArea =
                            clazz.superclass.getField(method).get(hookParam.thisObject) as View
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                loadClassOrNull("com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment").isNotNull {
                    it.methodFinder().filterByName("onViewCreated").single().createHook {
                        hideNoticeIcon(1)
                    }
                }
            } else {
                loadClassOrNull("com.android.systemui.statusbar.phone.NotificationIconAreaController").isNotNull {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.constructorFinder().single().createHook {
                            hideNoticeIcon(0)
                        }
                    } else {
                        it.methodFinder().filterByName("initializeNotificationAreaViews").single()
                            .createHook {
                                hideNoticeIcon(0)
                            }
                    }
                }
            }
        }

        // 触摸监听
        loadClassOrNull("com.android.systemui.statusbar.phone.PhoneStatusBarView").isNotNull {
            it.methodFinder().filterByName("onTouchEvent").single().createHook {
                before { hookParam ->
                    val motionEvent = hookParam.args[0] as MotionEvent
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            point = Point(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
                        }

                        MotionEvent.ACTION_MOVE -> {
                        }

                        MotionEvent.ACTION_UP -> {
                            val isMove =
                                abs(point.y - motionEvent.rawY.toInt()) > 50 || abs(point.x - motionEvent.rawX.toInt()) > 50
                            val isLongChick = motionEvent.eventTime - motionEvent.downTime > 500
                            when (isMove) {
                                true -> {
                                    if (config.slideStatusBarCutSongs) {
                                        if (isMusicPlaying) {
                                            if (isHiding) return@before

                                            if (abs(point.y - motionEvent.rawY.toInt()) <= config.slideStatusBarCutSongsYRadius) {
                                                val i = point.x - motionEvent.rawX.toInt()
                                                if (abs(i) > config.slideStatusBarCutSongsXRadius) {
                                                    moduleRes.getString(R.string.slide_status_bar_cut_songs)
                                                        .log()
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

                                                moduleRes.getString(R.string.long_click_status_bar_stop)
                                                    .log()
                                                shell("input keyevent 85", false)
                                                hookParam.result = true
                                            }
                                        }

                                        false -> {
                                            if (config.clickStatusBarToHideLyric || FocusNotifyController.isOS2FocusNotifyShowing) {
                                                if (!isMusicPlaying) return@before
                                                if (FocusNotifyController.isOS1FocusNotifyShowing) return@before

                                                moduleRes.getString(R.string.click_status_bar_to_hide_lyric)
                                                    .log()
                                                if (isHiding) {
                                                    if (FocusNotifyController.canControlFocusNotify()) {
                                                        if (FocusNotifyController.shouldOpenFocusNotify(
                                                                motionEvent
                                                            )
                                                        ) {
                                                            "Should open focus notify".log()
                                                            return@before
                                                        }
                                                    }
                                                    FocusNotifyController.isInteraction = false
                                                    hookParam.result = true
                                                    updateLyricState()
                                                    autoHideStatusBarInFullScreenModeIfNeed()
                                                } else {
                                                    val x = motionEvent.x.toInt()
                                                    val y = motionEvent.y.toInt()
                                                    val left = lyricLayout.left
                                                    val top = lyricLayout.top
                                                    val right = lyricLayout.right
                                                    val bottom = lyricLayout.bottom
                                                    if (x in left..right && y in top..bottom) {
                                                        FocusNotifyController.isInteraction = true
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
            it.constructorFinder().singleOrNull().ifNotNull { constructor ->
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

        loadClassOrNull("com.android.systemui.SystemUIApplication").isNotNull { clazz ->
            clazz.methodFinder().filterByName("onConfigurationChanged").single().createHook {
                after { hookParam ->
                    "onConfigurationChanged".log()
                    val newConfig = hookParam.args[0] as Configuration

                    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                        newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
                    ) {
                        if (!isReady) return@after
                        updateLyricState()
                    }
                }
            }
        }

        XiaomiHooks.init(this)
    }

    private fun lyricInit() {
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

        updateConfig(1)
    }

    private var statusBarShowing: Boolean = true

    // 适合考虑状态的更新
    fun updateLyricState(showLyric: Boolean = true, showFocus: Boolean = true, delay: Int = 0) {
        if (
            isInFullScreenMode() &&
            (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
        ) {
            if (statusBarShowing && showLyric && canShowLyric()) {
                showLyric(lastLyric, delay)
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
                showLyric(lastLyric, delay)
                FocusNotifyController.hideFocusNotifyIfNeed()
            } else {
                hideLyric()
                if (showFocus)
                    FocusNotifyController.showFocusNotifyIfNeed()
            }
        }
    }

    private fun canShowLyric(): Boolean {
        return isMusicPlaying && !FocusNotifyController.isOS1FocusNotifyShowing && !FocusNotifyController.isInteraction
    }

    private fun isInFullScreenMode(): Boolean {
        var isInFullScreenMode = false

        if (centralSurfacesImpl.existField("mIsFullscreen")) {
            isInFullScreenMode = centralSurfacesImpl?.getObjectField("mIsFullscreen") as Boolean
            statusBarShowing = centralSurfacesImpl?.getObjectField("mTransientShown") as Boolean
        } else if (defaultDisplay.existField("isInFullscreenMode")) {
            val isInFullscreenMode = defaultDisplay?.getObjectField("isInFullscreenMode")
            isInFullScreenMode = isInFullscreenMode?.getObjectField("$\$delegate_0")
                ?.callMethod("getValue") as Boolean

            val isTransientShown = defaultDisplay?.getObjectField("isTransientShown")
            statusBarShowing =
                isTransientShown?.getObjectField("$\$delegate_0")?.callMethod("getValue") as Boolean
        }

        return isInFullScreenMode
    }

    private fun autoHideStatusBarInFullScreenModeIfNeed() {
        if (autoHideController == null) return
        if (!isInFullScreenMode()) return

        autoHideController!!.callMethod("touchAutoHide")
    }

    private var lastArtist: String = ""
    private var lastAlbum: String = ""
    private var playingApp: String = ""
    private var updateConfig: UpdateConfig = UpdateConfig()
    private var screenLockReceiver: ScreenLockReceiver = ScreenLockReceiver()
    private val timeoutRestore: Int = 0
    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == timeoutRestore && config.timeoutRestore) {
                lastLyric = ""
                playingApp = ""
                updateLyricState(showLyric = false)
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
                if (data.playbackState?.state == PlaybackState.STATE_BUFFERING) return
                if (playingApp.isNotEmpty() && playingApp != data.packageName) return

                lastLyric = ""
                playingApp = ""
                isMusicPlaying = false
                if (lastRunnable.isNotNull()) handler.removeCallbacks(lastRunnable!!)
                if (handler.hasMessages(timeoutRestore)) handler.removeMessages(timeoutRestore)
                updateLyricState(showLyric = false)
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

                            if (lastRunnable.isNotNull()) handler.removeCallbacks(lastRunnable!!)
                            lastRunnable = Runnable { showTitleConsumer.accept(data) }

                            ("Title: " + data.title + ", Artist: " + lastArtist + ", Album: " + lastAlbum).log()
                        }
                    }
                }
                if (data.lyric.isEmpty()) return
                isMusicPlaying = true
                lastLyric = data.lyric
                if (lastRunnable.isNotNull()) handler.postDelayed(lastRunnable!!, 800)

                changeIcon(data)
                updateLyricState(delay = data.delay)
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
        if (!isReady || !isMusicPlaying || lyric.isEmpty() || isScreenLocked) return

        "Showing LyricView".log()
        goMainThread {
            isHiding = false
            lastColor = clockView.currentTextColor
            lyricLayout.cancelAnimation()
            lyricLayout.showView()
            if (config.hideTime) {
                clockView.hideView()
                XiaomiHooks.getPadClockView()?.hideView()
            }
            if (config.hideNotificationIcon) notificationIconArea?.hideView()
            XiaomiHooks.getMiuiNetworkSpeedView()?.hideView()
            XiaomiHooks.getCarrierLabel()?.hideView()

            lyricView.apply {
                val lyricWidth = getLyricWidth(lyric)
                width = lyricWidth
                val i = theoreticalWidth - lyricWidth
                "Lyric width: $lyricWidth, Theoretical width: $theoreticalWidth, i: $i".log()
                if (i > 0) {
                    if (delay > 0) {
                        val durationInSeconds = delay / 1000f
                        if (durationInSeconds > 0) {
                            val speed = 0.3f + (i.toFloat() / lyricWidth) * (5f / durationInSeconds)
                            val boundedSpeed = speed.coerceIn(0.3f, 5.0f)
                            setScrollSpeed(boundedSpeed)
                            "Delay mode - Duration: ${durationInSeconds}, Speed: $boundedSpeed".log()
                        }
                    } else if (config.dynamicLyricSpeed) {
                        val proportion = i / lyricWidth
                        val speed = 10 * proportion + 0.7f
                        setScrollSpeed(speed)
                        "Dynamic mode - Proportion: $proportion, Speed: $speed".log()
                    }
                } else {
                    setScrollSpeed(config.lyricSpeed.toFloat())
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

    // 更改图标
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
    private fun hideLyric() {
        if (!isReady) return
        if (isHiding) return
        isHiding = true

        "Hiding LyricView".log()
        goMainThread {
            lyricLayout.hideView(false)
            lyricView.setText("")
            clockView.showView()
            if (config.titleSwitch) titleDialog.hideTitle()
            notificationIconArea?.showView()
            XiaomiHooks.getPadClockView()?.showView()
            XiaomiHooks.getCarrierLabel()?.showView()
            XiaomiHooks.getMiuiNetworkSpeedView()?.showView()
            XiaomiHooks.getNotificationBigTime()?.visibility = View.VISIBLE
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
                setStrokeWidth(config.lyricStrokeWidth / 100f)
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

    private fun getLyricWidth(lyric: String): Int {
        "Getting Lyric Width".log()
        val textView = TextView(context).apply {
            setTextSize(
                TypedValue.COMPLEX_UNIT_SHIFT,
                if (config.lyricSize == 0) clockView.textSize else config.lyricSize.toFloat()
            )
            setTypeface(clockView.typeface)
            letterSpacing = config.lyricLetterSpacing / 100f
            paint.strokeWidth = config.lyricStrokeWidth / 100f
        }
        val textWidth = textView.paint.measureText(lyric).toInt()
        theoreticalWidth = textWidth
        return if (config.lyricWidth == 0) {
            min(textView.paint.measureText(lyric).toInt(), targetView.width - config.lyricStartMargins - config.lyricEndMargins)
        } else {
            if (config.fixedLyricWidth) {
                scaleWidth()
            } else {
                min(textView.paint.measureText(lyric).toInt(), scaleWidth())
            }
        }
    }

    private fun scaleWidth(): Int {
        "Scale Width".log()
        return (config.lyricWidth / 100f * if (context.isLandscape()) displayHeight else displayWidth).toInt()
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
                    updateLyricState()
                }
            }
        }
    }
}
