package statusbar.lyric.activity.page

import android.graphics.Color
import android.text.InputFilter
import android.text.InputType
import android.view.View
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
        TextSSw(textId = R.string.icon_switch, key = "iconSwitch", defValue = false, onClickListener = {
            changeConfig()
            binding.send(it)
        })
        TextSA(textId = R.string.icon_size, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.icon_size))
                setMessage(getString(R.string.icon_size_tips))
                setEditText(config.iconSize.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..100) {
                            config.iconSize = value
                            changeConfig()
                        } else {
                            throw Exception()
                        }
                    } catch (_: Exception) {
                        ActivityTools.showToastOnLooper(getString(R.string.input_error))
                    }
                }
                setLButton(getString(R.string.cancel))
                finally { dismiss() }
            }.show()
        }, dataBindingRecv = binding.getRecv(1))
        TextSA(textId = R.string.icon_color_and_transparency, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.icon_color_and_transparency))
                setMessage(getString(R.string.icon_color_and_transparency_tips))
                setEditText(config.iconColor, "#FFFFFF", config = {
                    it.inputType = InputType.TYPE_CLASS_TEXT
                    it.filters = arrayOf(InputFilter.LengthFilter(9))
                })
                setRButton(getString(R.string.ok)) {
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
                        ActivityTools.showToastOnLooper(getString(R.string.input_error))
                    }
                }
                setLButton(getString(R.string.cancel))
                finally { dismiss() }
            }.show()
        }, dataBindingRecv = binding.getRecv(1))
        TextSA(textId = R.string.icon_top_margins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.icon_top_margins))
                setMessage(getString(R.string.icon_top_margins_tips))
                setEditText(config.iconTopMargins.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -100..100) {
                            config.iconTopMargins = value
                            changeConfig()
                        } else {
                            throw Exception()
                        }
                    } catch (_: Exception) {
                        ActivityTools.showToastOnLooper(getString(R.string.input_error))
                    }
                }
                setLButton(getString(R.string.cancel))
                finally { dismiss() }
            }.show()
        }, dataBindingRecv = binding.getRecv(1))
        TextSA(textId = R.string.icon_bottom_margins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.icon_bottom_margins))
                setMessage(getString(R.string.icon_bottom_margins_tips))
                setEditText(config.iconBottomMargins.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -100..100) {
                            config.iconBottomMargins = value
                            changeConfig()
                        } else {
                            throw Exception()
                        }
                    } catch (_: Exception) {
                        ActivityTools.showToastOnLooper(getString(R.string.input_error))
                    }
                }
                setLButton(getString(R.string.cancel))
                finally { dismiss() }
            }.show()
        }, dataBindingRecv = binding.getRecv(1))
        TextSA(textId = R.string.icon_start_margins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.icon_start_margins))
                setMessage(getString(R.string.icon_start_margins_tips))
                setEditText(config.iconStartMargins.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -500..500) {
                            config.iconStartMargins = value
                            changeConfig()
                        } else {
                            throw Exception()
                        }
                    } catch (_: Exception) {
                        ActivityTools.showToastOnLooper(getString(R.string.input_error))
                    }
                }
                setLButton(getString(R.string.cancel))
                finally { dismiss() }
            }.show()
        }, dataBindingRecv = binding.getRecv(1))
        TextSSw(textId = R.string.force_the_icon_to_be_displayed, key = "forceTheIconToBeDisplayed", onClickListener = { changeConfig() }, dataBindingRecv = binding.getRecv(1))
    }
}