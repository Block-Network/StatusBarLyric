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
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
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
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP.config
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.tools.BlurTools.cornerRadius
import statusbar.lyric.tools.BlurTools.setBackgroundBlur
import statusbar.lyric.tools.LogTools.log
import statusbar.lyric.tools.LyricViewTools
import statusbar.lyric.tools.LyricViewTools.hideView
import statusbar.lyric.tools.LyricViewTools.randomAnima
import statusbar.lyric.tools.LyricViewTools.showView
import statusbar.lyric.tools.SystemMediaSessionListener
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
import java.lang.reflect.Method
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SystemUILyric : BaseHook() {

    private lateinit var hook: XC_MethodHook.Unhook
    val context: Context by lazy { AndroidAppHelper.currentApplication() }

    private var lastColor: Int by observableChange(Color.WHITE) { oldValue, newValue ->
        if (oldValue == newValue) return@observableChange
        "Change Color: $newValue".log()
        goMainThread {
            if (config.lyricColor.isEmpty() && config.lyricGradientColor.isEmpty()) {
                lyricView.setTextColor(newValue)
            }
            if (config.iconColor.isEmpty()) {
                iconView.setColorFilter(newValue, PorterDuff.Mode.SRC_IN)
            }
        }
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
    private var isClickHide: Boolean = false
    private var isRandomAnima: Boolean = false
    private var isInFullScreenMode: Boolean = false
    private var mAutoHideController: Any? = null
    private var focusedNotify: Any? = null
    private var shouldIgnore: Boolean = false
    private var isHideFocusNotify: Boolean = false
    private var canHideFocusNotify = false
    private var isOS2FocusNotifyShowing = false
    private var isOS1FocusNotifyShowing = false // OS1 不要支持隐藏焦点通知
    val isReady: Boolean get() = this@SystemUILyric::clockView.isInitialized
    // val isReally by lazy { this@SystemUILyric::clockView.isInitialized }

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
                    config.lyricGradientColor.trim().split(",").map { it.trim().toColorInt() }.let { colors ->
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
    private lateinit var mMiuiNetworkSpeedView: TextView
    private val titleDialog by lazy {
        TitleDialog(context)
    }

    //////////////////////////////Hook//////////////////////////////////////
    private var defaultDisplay: Any? = null
    private var mCentralSurfacesImpl: Any? = null
    private var notificationBigTime: View? = null

    @SuppressLint("DiscouragedApi", "NewApi")
    override fun init() {
        "Init Hook".log()
        Application::class.java.methodFinder().filterByName("attach").first().createHook {
            after { hook ->
                registerReceiver(hook.args[0] as Context)
            }
        }

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
                        // if (!togglePrompts) hook.unhook()
                    }
                }
            }

            View::class.java.methodFinder().filterByName("onDetachedFromWindow").first().createHook {
                after { hookParam ->
                    val view = (hookParam.thisObject as View)
                    if (view.isTargetView()) {
                        "onDetachedFromWindow".log()
                        canLoad = true
                        hideLyric()
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
                            if (
                                (isReady && clockView == view && config.hideTime) ||
                                (this@SystemUILyric::mNotificationIconArea.isInitialized && mNotificationIconArea == view && config.hideNotificationIcon) ||
                                (this@SystemUILyric::mCarrierLabel.isInitialized && mCarrierLabel == view && config.hideCarrier) ||
                                (this@SystemUILyric::mMiuiNetworkSpeedView.isInitialized && mMiuiNetworkSpeedView == view && config.mMiuiHideNetworkSpeed) ||
                                (this@SystemUILyric::mPadClockView.isInitialized && mPadClockView == view && config.hideTime)
                            ) {
                                hookParam.args[0] = View.GONE
                            }
                        }
                    }
                }
            }
        }

        loadClassOrNull("com.android.systemui.statusbar.phone.DarkIconDispatcherImpl").isNotNull {
            it.methodFinder().filterByName("applyDarkIntensity").first().createHook {
                after { hookParam ->
                    if (!isPlaying) return@after
                    val mIconTint = hookParam.thisObject.objectHelper().getObjectOrNullAs<Int>("mIconTint")
                    lastColor = mIconTint ?: Color.BLACK
                }
            }
        }

        if (config.hideNotificationIcon) {
            moduleRes.getString(R.string.hide_notification_icon).log()
            fun HookFactory.hideNoticeIcon(scheme: Int) {
                after { hookParam ->
                    val clazz = hookParam.thisObject::class.java
                    val name = if (scheme == 0) "NotificationIconAreaController" else "CollapsedStatusBarFragment"
                    val method = if (scheme == 0) "mNotificationIconArea" else "mNotificationIconAreaInner"
                    if (clazz.simpleName == name) {
                        hookParam.thisObject.objectHelper {
                            mNotificationIconArea = this.getObjectOrNullAs<View>(method)!!
                        }
                    } else {
                        mNotificationIconArea = clazz.superclass.getField(method).get(hookParam.thisObject) as View
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
                        it.methodFinder().filterByName("initializeNotificationAreaViews").first().createHook {
                            hideNoticeIcon(0)
                        }
                    }
                }
            }
        }
        if (config.hideCarrier && Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            moduleRes.getString(R.string.hide_carrier).log()
            loadClassOrNull("com.android.systemui.statusbar.phone.KeyguardStatusBarView").isNotNull {
                it.methodFinder().filterByName("onFinishInflate").first().createHook {
                    after { hookParam ->
                        kotlin.runCatching {
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
        }

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
                            val isMove =
                                abs(point.y - motionEvent.rawY.toInt()) > 50 || abs(point.x - motionEvent.rawX.toInt()) > 50
                            val isLongChick = motionEvent.eventTime - motionEvent.downTime > 500
                            when (isMove) {
                                true -> {
                                    if (config.slideStatusBarCutSongs) {
                                        if (isPlaying) {
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
                                                moduleRes.getString(R.string.long_click_status_bar_stop).log()
                                                shell("input keyevent 85", false)
                                                hookParam.result = true
                                            }
                                        }

                                        false -> {
                                            if (config.clickStatusBarToHideLyric || isOS2FocusNotifyShowing) {
                                                if (isOS1FocusNotifyShowing) return@before

                                                if (isPlaying) {
                                                    moduleRes.getString(R.string.click_status_bar_to_hide_lyric).log()
                                                    if (isHiding) {
                                                        if (shouldControlFocusNotify()) {
                                                            if (!isHideFocusNotify && shouldOpenFocusNotify(motionEvent)) {
                                                                "should open focus notify".log()
                                                                return@before
                                                            }
                                                        }

                                                        isClickHide = false
                                                        isHiding = false
                                                        hookParam.result = true
                                                        hideFocusNotifyIfNeed()
                                                        changeLyric(lastLyric, 0)
                                                        autoHideStatusBarInFullScreenModeIfNeed()
                                                    } else {
                                                        val x = motionEvent.x.toInt()
                                                        val y = motionEvent.y.toInt()
                                                        val left = lyricLayout.left
                                                        val top = lyricLayout.top
                                                        val right = lyricLayout.right
                                                        val bottom = lyricLayout.bottom
                                                        if (x in left..right && y in top..bottom) {
                                                            isClickHide = true
                                                            isHiding = true
                                                            hookParam.result = true
                                                            hideLyric()
                                                            showFocusNotifyIfNeed()
                                                            autoHideStatusBarInFullScreenModeIfNeed()
                                                        }
                                                    }
                                                    "change to hide: $isHiding".log()
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
            it.constructorFinder().firstOrNull().ifNotNull { constructor ->
                constructor.createHook {
                    after { hook ->
                        mCentralSurfacesImpl = hook.thisObject
                        mAutoHideController = hook.thisObject.getObjectField("mAutoHideController")
                        val mStatusBarModeRepository = hook.thisObject.getObjectFieldIfExist("mStatusBarModeRepository")
                        defaultDisplay = mStatusBarModeRepository?.getObjectFieldIfExist("defaultDisplay")
                    }
                }
            }
        }

        if (config.automateFocusedNotice) {
            moduleRes.getString(R.string.automate_focused_notice).log()
            loadClassOrNull("com.android.systemui.statusbar.phone.FocusedNotifPromptController").isNotNull {
                it.constructorFinder().firstOrNull().ifNotNull { constructor ->
                    constructor.createHook {
                        after { hook ->
                            focusedNotify = hook.thisObject
                        }
                    }
                }

                it.declaredMethods.filter { method ->
                    (method.name == "updateVisibility\$1" || method.name == "showImmediately" || method.name == "hideImmediately" ||
                            method.name == "cancelFolme" || method.name == "setIsFocusedNotifPromptShowing")
                }.forEach { method ->
                    method.createHook {
                        before { hook ->
                            if (isHideFocusNotify) {
                                hook.result = null
                                "update focus notify visibility is hiding!".log()
                            }
                        }
                    }
                }
            }

            val shouldShowMethod =
                loadClassOrNull("com.android.systemui.statusbar.phone.FocusedNotifPromptController").ifNotNull {
                    it.declaredMethods.firstOrNull { method -> method.name == "shouldShow" }
                }
            if (shouldShowMethod != null) {
                canHideFocusNotify = true
                (shouldShowMethod as Method).createHook {
                    after { hook ->
                        if (shouldIgnore) return@after

                        isOS2FocusNotifyShowing = hook.result as Boolean
                        if (isOS2FocusNotifyShowing) {
                            if (isPlaying && !isHideFocusNotify) {
                                isHiding = true
                                hideLyric()
                            }
                        } else
                            isHiding = false
                        "new focus notify is ${if (isOS2FocusNotifyShowing) "show" else "hide"}".log()
                    }
                }
            } else {
                canHideFocusNotify = false
                loadClassOrNull("com.android.systemui.statusbar.phone.FocusedNotifPromptController\$2").isNotNull {
                    it.methodFinder().filterByName("handleMessage").first().createHook {
                        before { hook ->
                            val message = hook.args[0] as Message
                            if (message.what == 1003) {
                                val show = focusNotifyShowing()
                                if (show) {
                                    if (isPlaying) {
                                        isOS1FocusNotifyShowing = true
                                        isHiding = true
                                        hideLyric()
                                    }
                                } else {
                                    isOS1FocusNotifyShowing = false
                                    isHiding = false
                                }
                                "focus notify is ${if (show) "show" else "hide"}".log()
                            }
                        }
                    }
                }
            }
        }

        loadClassOrNull("com.android.systemui.controlcenter.shade.NotificationHeaderExpandController\$notificationCallback\$1").isNotNull {
            it.methodFinder().filterByName("onExpansionChanged").first().createHook {
                before { hook ->
                    if (isPlaying && !isHiding && config.hideTime) {
                        val notificationHeaderExpandController = hook.thisObject.getObjectField("this\$0")
                        notificationHeaderExpandController?.setObjectField("bigTimeTranslationY", 0)
                        notificationHeaderExpandController?.setObjectField("notificationTranslationX", 0)
                        // notificationHeaderExpandController?.setObjectField("notificationTranslationY", 0)

                        val notificationBigTime = notificationHeaderExpandController?.getObjectField("headerController")
                            ?.callMethod("get")?.getObjectField("notificationBigTime") as View

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

        SystemUISpecial()
    }

    private var statusbarShowing: Boolean = true
    private fun changeLyricStateIfInFullScreenMode() {
        if (mCentralSurfacesImpl.existField("mIsFullscreen")) {
            isInFullScreenMode = mCentralSurfacesImpl?.getObjectField("mIsFullscreen") as Boolean
            statusbarShowing = mCentralSurfacesImpl?.getObjectField("mTransientShown") as Boolean
        } else if (defaultDisplay.existField("isInFullscreenMode")) {
            val isInFullscreenMode = defaultDisplay?.getObjectField("isInFullscreenMode")
            isInFullScreenMode = isInFullscreenMode?.getObjectField("\$\$delegate_0")?.callMethod("getValue") as Boolean

            val isTransientShown = defaultDisplay?.getObjectField("isTransientShown")
            statusbarShowing = isTransientShown?.getObjectField("\$\$delegate_0")?.callMethod("getValue") as Boolean
        }
        if (isInFullScreenMode &&
            (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                    context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)) {
            if (statusbarShowing && isPlaying) {
                if (!(isOS2FocusNotifyShowing || isOS1FocusNotifyShowing || isClickHide)) {
                    isHiding = false
                    hideFocusNotifyIfNeed()
                }
                "statusBar state is showing.".log()
            } else if (!statusbarShowing) {
                isHiding = true
                hideLyric()
                showFocusNotifyIfNeed()
                "statusBar state is hiding".log()
            }
        } else {
            if (!(isOS2FocusNotifyShowing || isOS1FocusNotifyShowing || isClickHide)) {
                isHiding = false
                hideFocusNotifyIfNeed()
            }
        }
    }

    private fun autoHideStatusBarInFullScreenModeIfNeed() {
        if (mAutoHideController == null) return
        if (!isInFullScreenMode) return
        mAutoHideController!!.callMethod("touchAutoHide")
    }

    private fun hideFocusNotifyIfNeed() {
        if (!shouldControlFocusNotify()) return
        if (!focusNotifyShowing()) return
        val mIcon = focusedNotify!!.getObjectField("mIcon")
        val mContent = focusedNotify!!.getObjectField("mContent")
        if (mIcon == null || mContent == null) return
        focusedNotify!!.callMethod("cancelFolme")
        focusedNotify!!.callMethod("hideImmediately", mIcon)
        focusedNotify!!.callMethod("hideImmediately", mContent)
        focusedNotify!!.callMethod("setIsFocusedNotifPromptShowing", false)
        isHideFocusNotify = true
        "hide focus notify!".log()
    }

    private fun showFocusNotifyIfNeed() {
        if (!shouldControlFocusNotify()) return
        if (!focusNotifyShowing()) return
        val mIcon = focusedNotify!!.getObjectField("mIcon")
        val mContent = focusedNotify!!.getObjectField("mContent")
        if (mIcon == null || mContent == null) return
        isHideFocusNotify = false
        focusedNotify!!.callMethod("cancelFolme")
        focusedNotify!!.callMethod("showImmediately", mIcon)
        focusedNotify!!.callMethod("showImmediately", mContent)
        focusedNotify!!.callMethod("setIsFocusedNotifPromptShowing", true)
        "show focus notify!".log()
    }

    private fun shouldControlFocusNotify(): Boolean {
        return focusedNotify != null && canHideFocusNotify
    }

    private fun focusNotifyShowing(): Boolean {
        val mCurrentNotifyBean = focusedNotify!!.getObjectField("mCurrentNotifBean") ?: return false
        val mIsHeadsUpShowing = focusedNotify!!.getObjectField("mIsHeadsUpShowing") as Boolean
        return if (canHideFocusNotify) {
            shouldIgnore = true
            (focusedNotify!!.callMethod("shouldShow", mCurrentNotifyBean, mIsHeadsUpShowing) as Boolean).apply {
                shouldIgnore = false
            }
        } else {
            !(focusedNotify!!.getObjectField("mIsHeadsUpShowing") as Boolean || mCurrentNotifyBean.getObjectField("headsUp") as Boolean || (TextUtils.equals(
                mCurrentNotifyBean.getObjectField("packageName") as CharSequence,
                focusedNotify!!.getObjectField("mTopActivityPackageName") as CharSequence
            ) && !(focusedNotify!!.getObjectField("mRequestHide") as Boolean)))
        }
    }

    private fun shouldOpenFocusNotify(motionEvent: MotionEvent): Boolean {
        if (!shouldControlFocusNotify()) return false
        if (!focusNotifyShowing()) return false
        val focusedNotifyPromptView = focusedNotify!!.getObjectField("mView") ?: return false

        val x = motionEvent.rawX
        val rect = focusedNotifyPromptView.getObjectField("mRect") as Rect
        val mContent = focusedNotifyPromptView.getObjectField("mContent") as FrameLayout
        val right = rect.right
        if (right <= 0 || rect.left == right) {
            mContent.getGlobalVisibleRect(rect)
            rect.right = max(rect.right, mContent.measuredWidth + rect.left)
        }
        return x >= rect.left
    }

    var playingApp: String = ""
    val TITLE_SHOW_READY: Int = 0
    val TITLE_SHOW_WAITING: Int = 1
    val TITLE_SHOWING: Int = 2
    val TIMEOUT_RESTORE: Int = 3
    val timeoutRestoreHandler: Handler by lazy {
        object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg.what == TIMEOUT_RESTORE && isPlaying && config.timeoutRestore) {
                    hideLyric()
                    playingApp = ""
                    lastLyric = ""
                    "timeout restore!!".log()
                }
            }
        }
    }
    val titleShowHandler: Handler by lazy {
        object : Handler(Looper.getMainLooper()) {
            var titleData: TitleData = TitleData("", "")

            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    TITLE_SHOW_READY -> {
                        if (titleShowHandler.hasMessages(TITLE_SHOW_WAITING))
                            titleShowHandler.removeMessages(TITLE_SHOW_WAITING)
                        titleShowHandler.sendEmptyMessageDelayed(TITLE_SHOW_WAITING, 800)
                        titleData = msg.obj as TitleData
                    }

                    TITLE_SHOW_WAITING -> {
                    }

                    TITLE_SHOWING -> {
                        if (titleShowHandler.hasMessages(TITLE_SHOW_WAITING)
                            || titleData.title.isEmpty() || titleData.caller.isEmpty()
                        ) return

                        if (playingApp.isNotEmpty() && titleData.caller == playingApp) {
                            "title: ${titleData.title}".log()
                            this@SystemUILyric.title = titleData.title
                        }

                        titleData = TitleData("", "")
                    }
                }
            }
        }
    }

    data class TitleData(val caller: String, val title: String)

    private fun lyricInit() {
        // val firstLoad = lyricLayout.parent.isNull()
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
                    intArrayOf(106, config.mHyperOSTextureBgColor.toColorInt()),
                    intArrayOf(3, config.mHyperOSTextureBgColor.toColorInt())
                )
                lyricLayout.setBackgroundBlur(blurRadio, cornerRadius, blendModes)
            }
            // themeMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)
            if (config.titleSwitch) {
                object : SystemMediaSessionListener(context) {
                    override fun onTitleChanged(caller: String, title: String) {
                        super.onTitleChanged(caller, title)

                        val message = Message.obtain()
                        message.what = TITLE_SHOW_READY
                        message.obj = TitleData(caller, title)
                        titleShowHandler.sendMessage(message)
                    }
                }
            }
        }

        // if (!firstLoad) return
        updateConfig(1)
    }

    private var mUpdateConfig: UpdateConfig = UpdateConfig()
    private var mScreenLockReceiver: ScreenLockReceiver = ScreenLockReceiver()

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerReceiver(context: Context) {
        val mLyricReceiver = LyricReceiver(object : LyricListener() {
            override fun onUpdate(lyricData: LyricData) {
                if (!isReady) return
                val lyric = lyricData.lyric
                lastLyric = lyric
                playingApp = lyricData.extraData.packageName
                changeLyricStateIfInFullScreenMode()

                if (isHiding) return
                if (timeoutRestoreHandler.hasMessages(TIMEOUT_RESTORE)) {
                    timeoutRestoreHandler.removeMessages(TIMEOUT_RESTORE)
                    timeoutRestoreHandler.sendEmptyMessageDelayed(TIMEOUT_RESTORE, 10000L)
                } else timeoutRestoreHandler.sendEmptyMessageDelayed(TIMEOUT_RESTORE, 10000L)
                hideFocusNotifyIfNeed()
                changeIcon(lyricData.extraData)
                changeLyric(lastLyric, lyricData.extraData.delay)
                titleShowHandler.sendEmptyMessage(TITLE_SHOWING)
            }

            override fun onStop(lyricData: LyricData) {
                if (!isReady) return
                if (playingApp.isNotEmpty()) {
                    if (lyricData.extraData.packageName.isNotEmpty() &&
                        playingApp != lyricData.extraData.packageName
                    ) return
                }
                playingApp = ""
                lastLyric = ""
                hideLyric()
                showFocusNotifyIfNeed()
                if (timeoutRestoreHandler.hasMessages(TIMEOUT_RESTORE)) {
                    timeoutRestoreHandler.removeMessages(TIMEOUT_RESTORE)
                }
            }
        })
        registerLyricListener(context, BuildConfig.API_VERSION, mLyricReceiver)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(mUpdateConfig, IntentFilter("updateConfig"), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(mUpdateConfig, IntentFilter("updateConfig"))
        }

        if (config.hideLyricWhenLockScreen) {
            val screenLockFilter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_USER_PRESENT)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(mScreenLockReceiver, screenLockFilter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(mScreenLockReceiver, screenLockFilter)
            }
        }

        "Register Receiver".log()
    }

    private fun changeLyric(lyric: String, delay: Int) {
        if (!isReady) return
        if (lyric.isEmpty()) return
        if (isHiding || isScreenLock) return

        "lyric:$lyric".log()
        isStop = false
        isPlaying = true
        goMainThread {
            lastColor = clockView.currentTextColor
            lyricLayout.showView()
            if (config.hideTime) {
                clockView.hideView()
                if (this::mPadClockView.isInitialized) mPadClockView.hideView()
            }
            if (this::mNotificationIconArea.isInitialized && config.hideNotificationIcon) mNotificationIconArea.hideView()
            if (this::mCarrierLabel.isInitialized && config.hideCarrier) mCarrierLabel.hideView()
            if (this::mMiuiNetworkSpeedView.isInitialized && config.mMiuiHideNetworkSpeed) mMiuiNetworkSpeedView.hideView()
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
    }

    private fun hideLyric(anim: Boolean = true) {
        if (!isReady) return
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
            notificationBigTime?.visibility = View.VISIBLE
            if (config.titleSwitch) titleDialog.hideTitle()
            if (this::mNotificationIconArea.isInitialized) mNotificationIconArea.showView()
            if (this::mCarrierLabel.isInitialized) mCarrierLabel.showView()
            if (this::mMiuiNetworkSpeedView.isInitialized) mMiuiNetworkSpeedView.showView()
            if (this::mPadClockView.isInitialized) mPadClockView.showView()
        }
    }

    private fun updateConfig(delay: Long = 0L) {
        "Update Config".log()
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
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    ).apply {
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

    inner class SystemUISpecial {
        init {
            if (isXiaomi) {
                for (i in 0..10) {
                    val clazz = loadClassOrNull("com.android.keyguard.wallpaper.MiuiKeyguardWallPaperManager\$$i")
                    if (clazz.isNotNull()) {
                        if (clazz!!.existMethod("onWallpaperChanged")) {
                            clazz.methodFinder().filterByName("onWallpaperChanged").first().createHook {
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

            // if (togglePrompts) {
            loadClassOrNull("com.android.systemui.SystemUIApplication").isNotNull { clazz ->
                clazz.methodFinder().filterByName("onConfigurationChanged").first().createHook {
                    after { hookParam ->
                        "onConfigurationChanged".log()
                        val newConfig = hookParam.args[0] as Configuration
                        // themeMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
                        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            if (!isReady) return@after
                            changeLyricStateIfInFullScreenMode()
                        }
                    }
                }
                if (isXiaomi && config.mMiuiPadOptimize) {
                    clazz.methodFinder().filterByName("onCreate").first().createHook {
                        after {
                            if (isPad) {
                                loadClassOrNull("com.android.systemui.statusbar.phone.MiuiCollapsedStatusBarFragment").isNotNull {
                                    if (it.existMethod("initMiuiViewsOnViewCreated")) {
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
            // }

            if (isXiaomi && config.mMiuiHideNetworkSpeed) {
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
