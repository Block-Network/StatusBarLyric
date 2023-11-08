package statusbar.lyric.activity.page

import android.view.View
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.tools.ActivityTestTools.getClass
import statusbar.lyric.tools.ActivityTestTools.waitResponse
import statusbar.lyric.tools.Tools


@BMPage
class HookPage : BasePage() {
    override fun onCreate() {
        val testModeBinding = GetDataBinding({ ActivityOwnSP.config.testMode }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextSSw(textId = R.string.test_mode, key = "testMode", onClickListener = { testModeBinding.send(it) })
        TextSSw(textId = R.string.relax_conditions, tipsId = R.string.relax_conditions_tips, key = "relaxConditions", dataBindingRecv = testModeBinding.getRecv(1))
        TextSA(textId = R.string.get_hook, onClickListener = {
            waitResponse()
            activity.getClass()
        }, dataBindingRecv = testModeBinding.getRecv(1))
        Line()
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
    }
}