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
        TextSA(textId = R.string.LyricWidth, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.LyricWidth))
                setMessage(getString(R.string.LyricWidthTips))
                setEditText(config.lyricWidth.toString(), "0", config = {
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..100) {
                            config.lyricWidth = value
                            updateConfig()
                        } else {
                            throw Exception()
                        }
                    } catch (_: Exception) {
                        ActivityTools.showToastOnLooper(getString(R.string.InputError))
                    }
                }
                setLButton(getString(R.string.Cancel))
                finally {
                    dismiss()
                }
            }.show()
            updateConfig()
        })
        TextSSw(textId = R.string.FixedLyricWidth, tipsId = R.string.fixedLyricWidthTips, key = "fixedLyricWidth", onClickListener = { updateConfig() })
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
        TextSA(textId = R.string.LyricLetterSpacing, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.LyricLetterSpacing))
                setMessage(getString(R.string.LyricLetterSpacingTips))
                setEditText(config.lyricLetterSpacing.toString(), "0", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..50) {
                            config.lyricLetterSpacing = value
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
        TextSA(textId = R.string.lyricStrokeWidth, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyricStrokeWidth))
                setMessage(getString(R.string.lyricStrokeWidthTips))
                setEditText(config.lyricStrokeWidth.toString(), "0", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..400) {
                            config.lyricStrokeWidth = value
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
        TextSA(textId = R.string.lyricSpeed, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyricSpeed))
                setMessage(getString(R.string.lyricSpeedTips))
                setEditText(config.lyricSpeed.toString(), "4", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..10) {
                            config.lyricSpeed = value
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