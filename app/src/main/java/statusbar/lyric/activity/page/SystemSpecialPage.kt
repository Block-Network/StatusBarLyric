package statusbar.lyric.activity.page

import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import statusbar.lyric.R
import statusbar.lyric.tools.Tools.isMIUI

@BMPage
class SystemSpecialPage : BasePage() {
    override fun onCreate() {
        if (isMIUI) {
            TextSw(textId = R.string.miui_hide_network_speed, key = "mMIUIHideNetworkSpeed")
            TextSw(textId = R.string.miui_pad_optimize, key = "mMiuiPadOptimize")
        }
        Line()
        TitleText("Wait for More...")
    }
}