package statusbar.lyric.activity.page


import android.graphics.Color
import android.text.InputFilter
import android.text.InputType
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.ActivityTools.updateConfig


@BMPage
class LyricPage : BasePage() {
    override fun onCreate() {
        TextSA(textId = R.string.LyricColorAndTransparency, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.LyricColorAndTransparency))
                setMessage(getString(R.string.LyricColorAndTransparencyTips))
                setEditText(config.lyricColor, "#FFFFFF", config = {
                    it.filters = arrayOf(InputFilter.LengthFilter(9))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText()
                        Color.parseColor(value)
                        config.lyricColor = value
                        updateConfig()
                    } catch (_: Exception) {
                        ActivityTools.showToastOnLooper(getString(R.string.ColorError))
                    }

                }
                setLButton(getString(R.string.Cancel))
                finally {
                    dismiss()
                }
            }.show()
            updateConfig()
        })
        TextSA(textId = R.string.LyricSize, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.LyricSize))
                setMessage(getString(R.string.LyricSizeTips))
                setEditText(config.lyricSize.toString(), "0", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..100) {
                            config.lyricSize = value
                            updateConfig()
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
        })
    }
}