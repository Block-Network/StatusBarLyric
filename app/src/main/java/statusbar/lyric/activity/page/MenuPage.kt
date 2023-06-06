package statusbar.lyric.activity.page

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import cn.fkj233.ui.activity.annotation.BMMenuPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import com.microsoft.appcenter.analytics.Analytics
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.activity.SettingsActivity
import statusbar.lyric.utils.ActivityUtils
import statusbar.lyric.utils.BackupUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.ktx.lpparam
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("NonConstantResourceId")
@BMMenuPage(titleId = R.string.Menu)
class MenuPage : BasePage() {
    override fun onCreate() {
        TextWithSwitch(TextV(textId = R.string.HideDeskIcon), SwitchV("hLauncherIcon", onClickListener = {
            activity.packageManager.setComponentEnabledSetting(ComponentName(activity, "${BuildConfig.APPLICATION_ID}.launcher"), if (it) {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            }, PackageManager.DONT_KILL_APP)
        }))
        TextWithSwitch(TextV(textId = R.string.DebugMode), SwitchV("Debug"))
        TextWithSwitch(TextV(text = "App Center"), SwitchV("AppCenter", true))
        TextWithSwitch(TextV(textId = R.string.CheckUpdate), SwitchV("CheckUpdate", true))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.ResetModule, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.ResetModuleDialog)
                setMessage(R.string.ResetModuleDialogTips)
                setRButton(R.string.Ok) {
                    ActivityUtils.cleanConfig(activity)
                        SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.ReStartSystemUI, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.RestartUI)
                setMessage(R.string.RestartUITips)
                setLButton(R.string.Ok) {
                    Utils.voidShell("pkill -f com.android.systemui", true)
                    Analytics.trackEvent("重启SystemUI")
                    dismiss()
                }
                setRButton(R.string.Cancel) { dismiss() }
            }.show()
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.Backup, onClickListener = {
            activity.getSP()?.let { BackupUtils.backup(activity, it) }
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.Recovery, onClickListener = {
            activity.getSP()?.let { BackupUtils.recovery(activity, it) }
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.Test, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.Test)
                setMessage(R.string.TestDialogTips)
                setRButton(R.string.Start) {
                    ActivityUtils.showToastOnLooper(activity, "尝试唤醒界面")
                    activity.sendBroadcast(Intent().apply {
                        action = "Lyric_Server"
                        putExtra("Lyric_Type", "test")
                        putExtra("Lyric_PackageName", activity.packageName)
                    })
                    dismiss()
                }
                setLButton(R.string.Back) { dismiss() }
            }.show()
        }))
        Line()
        TitleText(textId = R.string.ModuleVersion)
        TextSummary(textId = R.string.ModuleVersion, tips = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})-${BuildConfig.BUILD_TYPE}")
        val buildTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(BuildConfig.BUILD_TIME.toLong())
        TextSummary(textId = R.string.BuildTime, tips = buildTime)
        Text()
    }
}