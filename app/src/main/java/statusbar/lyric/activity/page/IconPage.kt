package statusbar.lyric.activity.page

import android.graphics.Color
import android.text.InputFilter
import android.text.InputType
import android.view.View
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.ActivityTools.changeConfig

@BMPage
class IconPage : BasePage() {
    override fun onCreate() {
        val binding = GetDataBinding({ config.iconSwitch }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextSSw(textId = R.string.IconSwitch, key = "iconSwitch", defValue = true, onClickListener = {
            changeConfig()
            binding.send(it)
        })
        TextSA(textId = R.string.IconSize, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.IconSize))
                setMessage(getString(R.string.IconSizeTips))
                setEditText(config.iconSize.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..100) {
                            config.iconSize = value
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
        }, dataBindingRecv = binding.getRecv(1))
        TextSA(textId = R.string.IconColorAndTransparency, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.IconColorAndTransparency))
                setMessage(getString(R.string.IconColorAndTransparencyTips))
                setEditText(config.iconColor, "#FFFFFF", config = {
                    it.inputType = InputType.TYPE_CLASS_TEXT
                    it.filters = arrayOf(InputFilter.LengthFilter(9))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText()
                        if (value.isEmpty()) {
                            config.iconColor = ""
                        } else {
                            Color.parseColor(value)
                            config.iconColor = value
                        }
                        changeConfig()
                    } catch (_: Exception) {
                        ActivityTools.showToastOnLooper(getString(R.string.InputError))
                    }
                }
                setLButton(getString(R.string.Cancel))
                finally { dismiss() }
            }.show()
        }, dataBindingRecv = binding.getRecv(1))
        TextSA(textId = R.string.IconTopMargins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.IconTopMargins))
                setMessage(getString(R.string.IconTopMarginsTips))
                setEditText(config.iconTopMargins.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -100..100) {
                            config.iconTopMargins = value
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
        }, dataBindingRecv = binding.getRecv(1))
        TextSA(textId = R.string.IconLeftMargins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.IconLeftMargins))
                setMessage(getString(R.string.IconLeftMarginsTips))
                setEditText(config.iconLeftMargins.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -100..100) {
                            config.iconLeftMargins = value
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
        }, dataBindingRecv = binding.getRecv(1))

    }
}