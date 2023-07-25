package statusbar.lyric.activity.page

import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import statusbar.lyric.R

@BMPage
class SystemSpecialPage : BasePage() {
    override fun onCreate() {
        TitleText(getString(R.string.MIUI))
        TextSw(textId = R.string.MIUIHideNetworkSpeed, key = "mMIUIHideNetworkSpeed")
        Line()
        TitleText("More...")
    }
}