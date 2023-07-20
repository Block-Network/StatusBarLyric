package statusbar.lyric.activity.page

import android.text.InputFilter
import android.text.InputType
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.dialog.MIUIDialog
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
        TextSSw(textId = R.string.LimitVisibilityChange, tipsId = R.string.LimitVisibilityChangeTips, key = "limitVisibilityChange")
        TextSw(textId = R.string.HideLyricWhenLockScreen, key = "hideLyricWhenLockScreen", defValue = true)
        TextSw(textId = R.string.HideCarrier, key = "hideCarrier")
        val lyricColorScheme: LinkedHashMap<Int, String> = LinkedHashMap<Int, String>().apply {
            this[0] = getString(R.string.ColorScheme1)
            this[1] = getString(R.string.ColorScheme2)
        }
        TextSSp(textId = R.string.LyricColorScheme, currentValue = lyricColorScheme[ActivityOwnSP.config.lyricColorScheme].toString(), data = {
            lyricColorScheme.forEach {
                add(it.value) { ActivityOwnSP.config.lyricColorScheme = it.key }
            }
        })
        TextSw(textId = R.string.DynamicLyricSpeed, key = "dynamicLyricSpeed", onClickListener = { changeConfig() })
        TextSw(textId = R.string.ClickStatusBarToHideLyric, key = "clickStatusBarToHideLyric")
        TextSA(textId = R.string.RegexReplace, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.RegexReplace))
                setMessage(getString(R.string.RegexReplaceTips))
                setEditText(ActivityOwnSP.config.regexReplace, "", config = {
                    it.inputType = InputType.TYPE_CLASS_TEXT
                    it.filters = arrayOf(InputFilter.LengthFilter(200))
                })
                setRButton(getString(R.string.OK)) {
                    ActivityOwnSP.config.regexReplace = getEditText()
                }
                setLButton(getString(R.string.Cancel))
                finally { dismiss() }
            }.show()
        })
    }
}