package statusbar.lyric.activity.page

import android.annotation.SuppressLint
import android.content.Intent
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.config.XposedOwnSP
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.Tools
import statusbar.lyric.tools.Tools.dispose
import java.text.SimpleDateFormat
import java.util.Locale


@SuppressLint("NonConstantResourceId")
@BMPage
class TestModePage : BasePage() {
    override fun onCreate() {
        TextSSw(textId = R.string.TestMode, key = "testMode", onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.RestartEffect))
                setMessage(getString(R.string.WhetherSoftware))
                setRButton(getString(R.string.OK)) {
                    ActivityTools.restartApp()
                }
            }.show()
        })
        TextSA(textId = R.string.TimeFormat, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.TimeFormat))
                setEditText(ActivityOwnSP.config.timeFormat, "H:mm")
                setRButton(getString(R.string.OK)) {
                    ActivityOwnSP.config.timeFormat = getEditText()
                    val currentTime = System.currentTimeMillis()
                    val dateFormat = SimpleDateFormat(ActivityOwnSP.config.timeFormat, Locale.getDefault())
                    val nowTime = dateFormat.format(currentTime).dispose()
                    ActivityTools.showToastOnLooper(getString(R.string.PrintTimeFormat).format(XposedOwnSP.config.timeFormat, nowTime))
                }
                setLButton(textId = R.string.Cancel)
                finally { dismiss() }
            }.show()
        })
        TextSA(textId = R.string.GetHook, onClickListener = {
            ActivityTools.context.sendBroadcast(Intent().apply {
                action = "TestReceiver"
                putExtra("Type", "SendClass")
            })
        })
        Line()
        TextSA(textId = R.string.ResetSystemUi, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.ResetSystemUi)
                setMessage(R.string.RestartUITips)
                setLButton(R.string.OK) {
                    Tools.voidShell("pkill -f com.android.systemui", true)
                    dismiss()
                }
                setRButton(R.string.Cancel) { dismiss() }
            }.show()
        })
    }
}