package statusbar.lyric.activity.page

import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.tools.ActivityTools.changeConfig
import statusbar.lyric.tools.Tools

@BMPage
class ExtendPage : BasePage() {
    override fun onCreate() {
        val indexMaps: LinkedHashMap<Int, String> = LinkedHashMap<Int, String>().apply {
            this[0] = getString(R.string.AddLocationStart)
            this[1] = getString(R.string.AddLocationEnd)
        }
        TextSSp(textId = R.string.LyricAddLocation, currentValue = indexMaps[ActivityOwnSP.config.viewIndex].toString(), data = {
            indexMaps.forEach {
                add(it.value) { ActivityOwnSP.config.viewIndex = it.key }
            }
        })
        TextSw(textId = R.string.HideNotificationIcon, key = "hideNotificationIcon", onClickListener = { changeConfig() })
        if (!Tools.isMIUI) {
            TextSSw(textId = R.string.LimitVisibilityChange, tipsId = R.string.LimitVisibilityChangeTips, key = "limitVisibilityChange")
        }
        TextSw(textId = R.string.HideLyricWhenLockScreen, key = "hideLyricWhenLockScreen")
        TextSw(textId = R.string.HideCarrier, key = "hideCarrier")
    }
}