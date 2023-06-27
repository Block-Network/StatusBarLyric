package statusbar.lyric.activity.page


import android.graphics.Color
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.ActivityTools.changeConfig


@BMPage
class LyricPage : BasePage() {
    override fun onCreate() {
        val widthBinding = GetDataBinding({ config.lyricWidth }) { view, flag, data ->
            if (flag == 1) {
                view.visibility = if (data as Int != 0) View.VISIBLE else View.GONE
            } else {
                val linearLayout = view as LinearLayout
                val seekBar = linearLayout.getChildAt(0) as SeekBar
                seekBar.progress = data as Int
            }
        }
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
                            changeConfig()
                            widthBinding.send(value)
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
        })
        SeekBarWithText(key = "lyricWidth", min = 0, max = 100, defaultProgress = 0, dataBindingRecv = widthBinding.getRecv(2), dataBindingSend = widthBinding.bindingSend)
        TextSSw(textId = R.string.FixedLyricWidth, tipsId = R.string.fixedLyricWidthTips, key = "fixedLyricWidth", onClickListener = { changeConfig() }, dataBindingRecv = widthBinding.getRecv(1))
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
                            changeConfig()
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
        TextSA(textId = R.string.LyricColorAndTransparency, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.LyricColorAndTransparency))
                setMessage(getString(R.string.LyricColorAndTransparencyTips))
                setEditText(config.lyricColor, "#FFFFFF", config = {
                    it.inputType = InputType.TYPE_CLASS_TEXT
                    it.filters = arrayOf(InputFilter.LengthFilter(9))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText()
                        if (value.isEmpty()) {
                            config.lyricColor = ""
                        } else {
                            Color.parseColor(value)
                            config.lyricColor = value
                        }
                        changeConfig()
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
                            changeConfig()
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
        TextSA(textId = R.string.LyricStrokeWidth, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.LyricStrokeWidth))
                setMessage(getString(R.string.LyricStrokeWidthTips))
                setEditText(config.lyricStrokeWidth.toString(), "0", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..400) {
                            config.lyricStrokeWidth = value
                            changeConfig()
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
        TextSA(textId = R.string.LyricSpeed, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.LyricSpeed))
                setMessage(getString(R.string.LyricSpeedTips))
                setEditText(config.lyricSpeed.toString(), "4", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(2))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..10) {
                            config.lyricSpeed = value
                            changeConfig()
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
        TextSSw(textId = R.string.LyricBlurredEdges, key = "lyricBlurredEdges", defValue = true, onClickListener = {
            changeConfig()
        })
        TextSA(textId = R.string.LyricBlurredEdgesRadius, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.LyricBlurredEdgesRadius))
                setMessage(getString(R.string.LyricBlurredEdgesRadiusTips))
                setEditText(config.lyricBlurredEdgesRadius.toString(), "40", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..100) {
                            config.lyricBlurredEdgesRadius = value
                            changeConfig()
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
        TextSA(textId = R.string.LyricTopMargins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.LyricTopMargins))
                setMessage(getString(R.string.LyricTopMarginsTips))
                setEditText(config.lyricTopMargins.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -100..100) {
                            config.lyricTopMargins = value
                            changeConfig()
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
        TextSA(textId = R.string.LyricLeftMargins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.LyricLeftMargins))
                setMessage(getString(R.string.LyricLeftMarginsTips))
                setEditText(config.lyricLeftMargins.toString(), "7", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -100..100) {
                            config.lyricLeftMargins = value
                            changeConfig()
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
        TextSA(textId = R.string.RegexReplace, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.RegexReplace))
                setMessage(getString(R.string.RegexReplaceTips))
                setEditText(config.regexReplace, "", config = {
                    it.inputType = InputType.TYPE_CLASS_TEXT
                    it.filters = arrayOf(InputFilter.LengthFilter(100))
                })
                setRButton(getString(R.string.OK)) {
                    config.regexReplace = getEditText()
                }
                setLButton(getString(R.string.Cancel))
                finally { dismiss() }
            }.show()
        })
    }
}