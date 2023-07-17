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
import statusbar.lyric.tools.ActivityTools.changeConfig
import statusbar.lyric.tools.ViewTools.hideView
import statusbar.lyric.tools.ViewTools.showView

@BMPage
class IconPage : BasePage() {
    override fun onCreate() {
        val binding = GetDataBinding({ config.iconSwitch }) { view, _, data ->
            if (data as Boolean) view.showView() else view.hideView()
        }
        TextSSw(textId = R.string.IconSwitch, key = "iconSwitch", defValue = false, onClickListener = {
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
        TextSA(textId = R.string.IconBottomMargins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.IconBottomMargins))
                setMessage(getString(R.string.IconBottomMarginsTips))
                setEditText(config.iconBottomMargins.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -100..100) {
                            config.iconBottomMargins = value
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
        TextSA(textId = R.string.IconStartMargins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.IconStartMargins))
                setMessage(getString(R.string.IconStartMarginsTips))
                setEditText(config.iconStartMargins.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.OK)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -500..500) {
                            config.iconStartMargins = value
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
        TextSSw(textId = R.string.ForceTheIconToBeDisplayed, key = "forceTheIconToBeDisplayed", onClickListener = { changeConfig() }, dataBindingRecv = binding.getRecv(1))
    }
}