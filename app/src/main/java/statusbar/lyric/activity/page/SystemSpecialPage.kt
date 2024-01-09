package statusbar.lyric.activity.page

import android.os.Build
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
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) TextSw(textId = R.string.hide_carrier, key = "hideCarrier")
        }
        Line()
        TitleText("Wait for More...")
    }
}