package statusbar.lyric.activity.page

import android.text.InputFilter
import android.text.InputType
import android.view.View
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.tools.ActivityTools
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
        val lyricBlurredEdgesRadiusBinding = GetDataBinding({ ActivityOwnSP.config.lyricBlurredEdges }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextSSw(textId = R.string.LyricBlurredEdges, key = "lyricBlurredEdges", defValue = false, onClickListener = {
            lyricBlurredEdgesRadiusBinding.send(it)
        })
        TextSA(textId = R.string.LyricBlurredEdgesRadius, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.LyricBlurredEdgesRadius))
                setMessage(getString(R.string.LyricBlurredEdgesRadiusTips))
                setEditText(ActivityOwnSP.config.lyricBlurredEdgesRadius.toString(), "40", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..100) {
                            ActivityOwnSP.config.lyricBlurredEdgesRadius = value
                        } else {
                            throw Exception()
                        }
                    } catch (_: Exception) {
                        ActivityTools.showToastOnLooper(getString(R.string.InputError))
                    }
                }
                setLButton(getString(R.string.Cancel))
                finally { dismiss() }
            }.show()
        }, dataBindingRecv = lyricBlurredEdgesRadiusBinding.binding.getRecv(1))

        val lyricBlurredEdgesType: LinkedHashMap<Int, String> = LinkedHashMap<Int, String>().apply {
            this[0] = getString(R.string.lyricBlurredEdgesTypeAll)
            this[1] =  getString(R.string.lyricBlurredEdgesTypeStart)
            this[2] =  getString(R.string.lyricBlurredEdgesTypeEnd)
        }
        TextSSp(textId = R.string.LyricBlurredEdgesType, currentValue = lyricBlurredEdgesType[ActivityOwnSP.config.lyricBlurredEdgesType].toString(), data = {
            lyricBlurredEdgesType.forEach {
                add(it.value) { ActivityOwnSP.config.lyricBlurredEdgesType = it.key }
            }
        }, dataBindingRecv = lyricBlurredEdgesRadiusBinding.binding.getRecv(1))

    }
}