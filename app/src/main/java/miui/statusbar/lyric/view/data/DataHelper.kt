package miui.statusbar.lyric.view.data

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import com.microsoft.appcenter.analytics.Analytics
import miui.statusbar.lyric.BuildConfig
import miui.statusbar.lyric.R
import miui.statusbar.lyric.activity.AboutActivity
import miui.statusbar.lyric.activity.ApiAPPListActivity
import miui.statusbar.lyric.utils.ActivityOwnSP
import miui.statusbar.lyric.utils.ActivityUtils
import miui.statusbar.lyric.utils.ShellUtils
import miui.statusbar.lyric.utils.XposedOwnSP
import miui.statusbar.lyric.view.miuiview.MIUIDialog

@SuppressLint("StaticFieldLeak")
object DataHelper {
    var thisItems = "Main"
    var main = "Main"
    val menu = "Menu"
    val custom = "Custom"
    var backView: ImageView? = null
    lateinit var currentActivity: Activity

    fun setItems(string: String) {
        backView?.setImageResource(if (string != main) R.drawable.abc_ic_ab_back_material else R.drawable.abc_ic_menu_overflow_material)
        thisItems = string
        currentActivity.recreate()
    }

    fun getItems(): ArrayList<Item> = when (thisItems) {
        menu -> loadMenuItems()
        custom -> loadCustomItems()
        else -> loadItems()
    }

    fun setBackButton() {
        backView?.setImageResource(if (thisItems != main) R.drawable.abc_ic_ab_back_material else R.drawable.abc_ic_menu_overflow_material)
    }

    private fun loadMenuItems(): ArrayList<Item> {
        val itemList = arrayListOf<Item>()
        itemList.apply {
            add(Item(Text("Module Version", isTitle = true), null, line = true))
            add(Item(Text("${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})-${BuildConfig.BUILD_TYPE}"), null))
        }
        return itemList
    }

    @SuppressLint("SetTextI18n")
    private fun loadCustomItems(): ArrayList<Item> {
        currentActivity.findViewById<TextView>(R.id.Title).setText(R.string.Custom)
        val itemList = arrayListOf<Item>()
        itemList.apply {
            if (!ActivityOwnSP.ownSPConfig.getUseSystemReverseColor()) {
                add(Item(Text(resId = R.string.LyricColor, showArrow = true, onClickListener = {
                    MIUIDialog(currentActivity).apply {
                        setTitle(R.string.LyricColor)
                        setMessage(R.string.LyricColorTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getLyricColor(), "#FFFFFF")
                        setButton(R.string.Ok) {
                            if (getEditText() == "") {
                                ActivityOwnSP.ownSPConfig.setLyricColor("")
                            } else {
                                try {
                                    Color.parseColor(getEditText())
                                    ActivityOwnSP.ownSPConfig.setLyricColor(getEditText())
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(currentActivity, currentActivity.getString(R.string.LyricColorError))
                                    ActivityOwnSP.ownSPConfig.setLyricColor("")
                                }
                            }
                            dismiss()
                        }
                        setCancelButton(R.string.Cancel) { dismiss() }
                        show()
                    }
                })))
            }
            add(Item(Text("${currentActivity.getString(R.string.LyricWidth)} (${if (ActivityOwnSP.ownSPConfig.getLyricWidth() != -1) ActivityOwnSP.ownSPConfig.getLyricWidth() else currentActivity.getString(R.string.Adaptive)}"), seekBar = SeekBar(-1, 100, ActivityOwnSP.ownSPConfig.getLyricWidth()) { pos, text ->
                ActivityOwnSP.ownSPConfig.setLyricWidth(pos)
                if (pos == -1) {
                    text.text = "${currentActivity.getString(R.string.LyricWidth)} (${currentActivity.getString(R.string.Adaptive)})"
                } else {
                    text.text = "${currentActivity.getString(R.string.LyricWidth)} (${pos})"
                }
            }))
            if (ActivityOwnSP.ownSPConfig.getLyricWidth() == -1) {
                add(Item(Text(
                    "${currentActivity.getString(R.string.LyricAutoMaxWidth)} (${
                        if (ActivityOwnSP.ownSPConfig.getLyricMaxWidth() != -1) ActivityOwnSP.ownSPConfig.getLyricMaxWidth() else currentActivity.getString(
                            R.string.Adaptive
                        )
                    })"
                ), seekBar = SeekBar(-1, 100, ActivityOwnSP.ownSPConfig.getLyricMaxWidth()) { pos, text ->
                    ActivityOwnSP.ownSPConfig.setLyricMaxWidth(pos)
                    if (pos == -1) {
                        text.text = "${currentActivity.getString(R.string.LyricAutoMaxWidth)} (${
                            currentActivity.getString(R.string.Adaptive)
                        })"
                    } else {
                        text.text = "${currentActivity.getString(R.string.LyricAutoMaxWidth)} (${pos})"
                    }
                })
                )
            }
        }
        return itemList
    }

    private fun loadItems(): ArrayList<Item> {
        val itemList = arrayListOf<Item>()
        itemList.apply {
            add(Item(Text(resId = R.string.UseInfo, showArrow = true, onClickListener = { // 使用说明
                MIUIDialog(currentActivity).apply {
                    setTitle(R.string.VerExplanation)
                    setMessage(String.format(
                        " %s [%s] %s",
                        currentActivity.getString(R.string.CurrentVer),
                        BuildConfig.VERSION_NAME,
                        currentActivity.getString(R.string.VerExp)
                    ))
                    setButton(R.string.Done) {
                        dismiss()
                    }
                    show()
                }
            })))
            add(Item(Text(resId = R.string.WarnExplanation, showArrow = true, onClickListener = { // 模块注意事项
                MIUIDialog(currentActivity).apply {
                    setTitle(R.string.WarnExplanation)
                    setMessage(String.format(
                        " %s [%s] %s",
                        currentActivity.getString(R.string.CurrentVer),
                        BuildConfig.VERSION_NAME,
                        currentActivity.getString(R.string.WarnExp)
                    ))
                    setButton(R.string.Done) {
                        dismiss()
                    }
                    show()
                }
            })))
            add(Item(Text(resId = R.string.BaseSetting, isTitle = true), line = true)) // 基础设置分割线
            add(Item(Text(resId = R.string.AllSwitch), Switch("LService"))) // 总开关
            add(Item(Text(resId = R.string.LyricIcon), Switch("I"))) // 图标
            add(Item(Text(resId = R.string.Custom, showArrow = true, onClickListener = { setItems(custom) }))) // 个性化
            add(Item(Text(resId = R.string.AdvancedSettings, isTitle = true), line = true)) // 高级设置分割线
            add(Item(Text(resId = R.string.UseApiList, showArrow = true, onClickListener = { currentActivity.startActivity(Intent(currentActivity, ApiAPPListActivity::class.java)) })))
            add(Item(Text(resId = R.string.HideDeskIcon), Switch("hLauncherIcon", onCheckedChangeListener = { _, newValue -> // 隐藏桌面图标
                val packageManager: PackageManager = currentActivity.packageManager
                val mode: Int = if (newValue) {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                }
                packageManager.setComponentEnabledSetting(
                    ComponentName(currentActivity, "miui.statusbar.lyric.launcher"),
                    mode,
                    PackageManager.DONT_KILL_APP
                )
            }))) //  使用系统反色
            add(Item(Text(resId = R.string.UseSystemReverseColor), Switch("UseSystemReverseColor"))) //  使用系统反色
            add(Item(Text(resId = R.string.SongPauseCloseLyrics), Switch("LAutoOff"))) // 暂停歌词自动关闭歌词
            add(Item(Text(resId = R.string.Other, isTitle = true), line = true)) // 其他分割线
            add(Item(Text(resId = R.string.CustomHook, showArrow = true, onClickListener = {
                MIUIDialog(currentActivity).apply {
                    setTitle(R.string.HookSetTips)
                    setEditText(ActivityOwnSP.ownSPConfig.getHook(), currentActivity.getString(R.string.InputCustomHook))
                    setButton(R.string.Ok) {
                        ActivityOwnSP.ownSPConfig.setHook(getEditText())
                        ActivityUtils.showToastOnLooper(
                            currentActivity,
                            String.format(
                                "%s %s%s",
                                currentActivity.getString(R.string.HookSetTips),
                                if (ActivityOwnSP.ownSPConfig.getHook() == "") currentActivity.getString(R.string.Default) else ActivityOwnSP.ownSPConfig.getHook(),
                                currentActivity.getString(R.string.RestartSystemUI)
                            )
                        )
                        dismiss()
                    }
                    setCancelButton(R.string.Cancel) { dismiss() }
                    show()
                }
            })))
            add(Item(Text(resId = R.string.DebugMode), Switch("Debug")))
            add(Item(Text(resId = R.string.Test, showArrow = true, onClickListener = {
                MIUIDialog(currentActivity).apply {
                    setTitle(R.string.Test)
                    setMessage(R.string.TestDialogTips)
                    setButton(R.string.Start) {
                        ActivityUtils.showToastOnLooper(currentActivity, "尝试唤醒界面")
                        currentActivity.sendBroadcast(
                            Intent().apply {
                                action = "Lyric_Server"
                                putExtra("Lyric_Type", "test")
                            }
                        )
                        dismiss()
                    }
                    setCancelButton(R.string.Back) { dismiss() }
                    show()
                }
            })))
            add(Item(Text(resId = R.string.ReStartSystemUI, onClickListener = { // 重启SystemUI
                MIUIDialog(currentActivity).apply {
                    setTitle(R.string.RestartUI)
                    setMessage(R.string.RestartUITips)
                    setButton(R.string.Ok) {
                        ShellUtils.voidShell("pkill -f com.android.systemui", true)
                        Analytics.trackEvent("重启SystemUI")
                        dismiss()
                    }
                    setCancelButton(R.string.Cancel) {
                        dismiss()
                    }
                    show()
                }
            }), null))
            add(Item(Text(resId = R.string.About, isTitle = true), line = true)) // 关于分割线
            add(Item(Text("${currentActivity.getString(R.string.CheckUpdate)} (${BuildConfig.VERSION_NAME})", onClickListener = {
                ActivityUtils.showToastOnLooper(
                    currentActivity,
                    currentActivity.getString(R.string.StartCheckUpdate)
                )
                ActivityUtils.checkUpdate(currentActivity)
            })))
            add(Item(Text(resId = R.string.AboutModule, onClickListener = { currentActivity.startActivity(Intent(currentActivity, AboutActivity::class.java)) })))
        }
        return itemList
    }
}