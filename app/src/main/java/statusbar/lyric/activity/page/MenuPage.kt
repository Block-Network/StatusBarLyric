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
        TextWithSwitch(TextV(textId = R.string.hide_desk_icon), SwitchV("hLauncherIcon", onClickListener = {
            activity.packageManager.setComponentEnabledSetting(ComponentName(activity, "${BuildConfig.APPLICATION_ID}.launcher"), if (it) {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            }, PackageManager.DONT_KILL_APP)
        }))
        TextSw(textId = R.string.check_update, key = "checkUpdate", defValue = true)
        TextSA(textId = R.string.reset_config, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.reset_config)
                setLButton(R.string.ok) {
                    ActivityOwnSP.config.clear()
                    ActivityTools.changeConfig()
                    MIUIDialog(activity) {
                        setTitle(getString(R.string.restart_app_effect))
                        setRButton(getString(R.string.ok)) {
                            ActivityTools.restartApp()
                        }
                        setCancelable(false)
                    }.show()
                }
                setRButton(R.string.cancel)
                finally { dismiss() }
            }.show()
        })
        TextSA(textId = R.string.reset_system_ui, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.reset_system_ui)
                setMessage(R.string.restart_systemui_tips)
                setLButton(R.string.ok) {
                    Tools.shell("killall com.android.systemui", true)
                    dismiss()
                }
                setRButton(R.string.cancel) { dismiss() }
            }.show()
        })
        TextSA(textId = R.string.backup_config, onClickListener = { activity.getSP()?.let { BackupTools.backup(activity, it) } })
        TextSA(textId = R.string.recovery_config, onClickListener = { activity.getSP()?.let { BackupTools.recovery(activity, it) } })

        Line()
        TitleText(textId = R.string.module_version)
        TextS(textId = R.string.module_version, tips = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})-${BuildConfig.BUILD_TYPE}")
        val buildTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(BuildConfig.BUILD_TIME)
        TextS(textId = R.string.module_build_time, tips = buildTime)
        Text()
    }
}