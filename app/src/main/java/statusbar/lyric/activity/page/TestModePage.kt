package statusbar.lyric.activity.page

import android.annotation.SuppressLint
import android.view.View
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.dialog.MIUIDialog
import cn.fkj233.ui.dialog.NewDialog
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.tools.ActivityTestTools.getClass
import statusbar.lyric.tools.ActivityTestTools.waitResponse
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.Tools
import statusbar.lyric.tools.Tools.dispose
import java.text.SimpleDateFormat
import java.util.Locale


@SuppressLint("NonConstantResourceId")
@BMPage
class TestModePage : BasePage() {
    override fun onCreate() {
        val testModeBinding = GetDataBinding({ ActivityOwnSP.config.testMode }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextSSw(textId = R.string.TestMode, key = "testMode", onClickListener = { testModeBinding.send(it) })
        TextSSw(textId = R.string.RelaxConditions, tipsId = R.string.RelaxConditionsTips, key = "relaxConditions", dataBindingRecv = testModeBinding.getRecv(1))
        TextSA(textId = R.string.TimeFormat, onClickListener = {
            NewDialog(activity) {
                setTitle(getString(R.string.TimeFormat))
                setEditText(ActivityOwnSP.config.timeFormat, "H:mm")
                Button(getString(R.string.UnderstandTimeFormat)) { ActivityTools.openUrl("https://zhuanlan.zhihu.com/p/51695220") }
                Button(getString(R.string.OK)) {
                    ActivityOwnSP.config.timeFormat = getEditText()
                    val currentTime = System.currentTimeMillis()
                    val dateFormat = SimpleDateFormat(ActivityOwnSP.config.timeFormat, Locale.getDefault())
                    val nowTime = dateFormat.format(currentTime).dispose()
                    ActivityTools.showToastOnLooper(getString(R.string.PrintTimeFormat).format(getEditText(), nowTime))
                }
                Button(getString(R.string.Cancel), cancelStyle = true)
                Finally { dismiss() }
            }.show()
        }, dataBindingRecv = testModeBinding.getRecv(1))
        TextSA(textId = R.string.GetHook, onClickListener = {
            waitResponse()
            activity.getClass()
        }, dataBindingRecv = testModeBinding.getRecv(1))
        Line()
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
    }
}