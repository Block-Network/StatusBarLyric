package statusbar.lyric.activity.page

import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.tools.ActivityTools.changeConfig

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
        TextSSw(textId = R.string.LimitVisibilityChange, tipsId = R.string.LimitVisibilityChangeTips, key = "limitVisibilityChange", onClickListener = { changeConfig() })
        TextSw(textId = R.string.HideLyricWhenLockScreen, key = "hideLyricWhenLockScreen", onClickListener = { changeConfig() })
        TextSSw(textId = R.string.LyricInNotificationIconAreaInner, tipsId = R.string.LyricInNotificationIconAreaInnerTips, key = "lyricInNotificationIconAreaInner", onClickListener = { changeConfig() })
    }
}