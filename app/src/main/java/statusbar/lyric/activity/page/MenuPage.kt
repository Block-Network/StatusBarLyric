package statusbar.lyric.activity.page

import android.content.ComponentName
import android.content.pm.PackageManager
import cn.fkj233.ui.activity.annotation.BMMenuPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.BackupTools
import statusbar.lyric.tools.Tools
import java.text.SimpleDateFormat
import java.util.Locale


@BMMenuPage
class MenuPage : BasePage() {
    override fun onCreate() {
        TextWithSwitch(TextV(textId = R.string.HideDeskIcon), SwitchV("hLauncherIcon", onClickListener = {
            activity.packageManager.setComponentEnabledSetting(ComponentName(activity, "${BuildConfig.APPLICATION_ID}.launcher"), if (it) {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            }, PackageManager.DONT_KILL_APP)
        }))
        TextSw(textId = R.string.CheckUpdate, key = "checkUpdate", defValue = true)
        TextSA(textId = R.string.ResetConfig, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.ResetConfig)
                setLButton(R.string.OK) {
                    ActivityOwnSP.config.clear()
                    ActivityTools.changeConfig()
                    MIUIDialog(activity) {
                        setTitle(getString(R.string.RestartAppEffect))
                        setRButton(getString(R.string.OK)) {
                            ActivityTools.restartApp()
                        }
                        setCancelable(false)
                    }.show()
                }
                setRButton(R.string.Cancel)
                finally { dismiss() }
            }.show()
        })
        TextSA(textId = R.string.ResetSystemUi, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.ResetSystemUi)
                setMessage(R.string.RestartUITips)
                setLButton(R.string.OK) {
                    Tools.shell("pkill -f com.android.systemui", true)
                    dismiss()
                }
                setRButton(R.string.Cancel) { dismiss() }
            }.show()
        })
        TextSA(textId = R.string.BackupConfig, onClickListener = { activity.getSP()?.let { BackupTools.backup(activity, it) } })
        TextSA(textId = R.string.RecoveryConfig, onClickListener = { activity.getSP()?.let { BackupTools.recovery(activity, it) } })

        Line()
        TitleText(textId = R.string.ModuleVersion)
        TextSA(textId = R.string.ModuleVersion, tips = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})-${BuildConfig.BUILD_TYPE}")
        val buildTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(BuildConfig.BUILD_TIME)
        TextSA(textId = R.string.ModuleBuildTime, tips = buildTime)
        Text()
    }
}