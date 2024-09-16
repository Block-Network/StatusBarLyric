package statusbar.lyric.activity.page

import android.graphics.Color
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.ActivityTools.changeConfig
import statusbar.lyric.tools.Tools.isHyperOS
import statusbar.lyric.tools.Tools.isMIUI

@BMPage
class SystemSpecialPage : BasePage() {
    override fun onCreate() {
        if (isMIUI) {
            TextSw(textId = R.string.miui_hide_network_speed, key = "mMIUIHideNetworkSpeed")
            TextSw(textId = R.string.miui_pad_optimize, key = "mMiuiPadOptimize")
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) TextSw(textId = R.string.hide_carrier, key = "hideCarrier")
            Line()
            if (isHyperOS()) {
                TextSw(textId = R.string.hyperos_texture, key = "mHyperOSTexture")
                TextSA(textId = R.string.hyperos_texture_radio, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(getString(R.string.hyperos_texture_radio))
                        setMessage(getString(R.string.lyric_stroke_width_tips))
                        setEditText(ActivityOwnSP.config.mHyperOSTextureRadio.toString(), "50", config = {
                            it.filters = arrayOf(InputFilter.LengthFilter(2))
                        })
                        setRButton(getString(R.string.ok)) {
                            try {
                                val value = getEditText().toInt()
                                if (value in 0..50) {
                                    ActivityOwnSP.config.mHyperOSTextureRadio = value
                                    changeConfig()
                                } else {
                                    throw Exception()
                                }
                            } catch (_: Exception) {
                                ActivityTools.showToastOnLooper(getString(R.string.input_error))
                            }
                        }
                        setLButton(getString(R.string.cancel))
                        finally {
                            dismiss()
                        }
                    }.show()
                })
                TextSA(textId = R.string.hyperos_texture_corner, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(getString(R.string.hyperos_texture_corner))
                        setMessage(getString(R.string.lyric_letter_spacing_tips))
                        setEditText(ActivityOwnSP.config.mHyperOSTextureCorner.toString(), "32", config = {
                            it.filters = arrayOf(InputFilter.LengthFilter(2))
                        })
                        setRButton(getString(R.string.ok)) {
                            try {
                                val value = getEditText().toInt()
                                if (value in 0..50) {
                                    ActivityOwnSP.config.mHyperOSTextureCorner = value
                                    changeConfig()
                                } else {
                                    throw Exception()
                                }
                            } catch (_: Exception) {
                                ActivityTools.showToastOnLooper(getString(R.string.input_error))
                            }
                        }
                        setLButton(getString(R.string.cancel))
                        finally {
                            dismiss()
                        }
                    }.show()
                })
                TextSA(textId = R.string.hyperos_texture_color, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(getString(R.string.hyperos_texture_color))
                        setMessage(getString(R.string.lyric_color_and_transparency_tips))
                        setEditText(ActivityOwnSP.config.mHyperOSTextureBgColor, "#10818181", config = {
                            it.inputType = InputType.TYPE_CLASS_TEXT
                            it.filters = arrayOf(InputFilter.LengthFilter(9))
                        })
                        setRButton(getString(R.string.ok)) {
                            try {
                                val value = getEditText()
                                if (value.isEmpty()) {
                                    ActivityOwnSP.config.mHyperOSTextureBgColor = ""
                                } else {
                                    Color.parseColor(value)
                                    ActivityOwnSP.config.mHyperOSTextureBgColor = value
                                }
                            } catch (_: Exception) {
                                ActivityTools.showToastOnLooper(getString(R.string.input_error))
                            }
                        }
                        setLButton(getString(R.string.cancel))
                        finally {
                            dismiss()
                        }
                    }.show()
                })
            }
        }
        Line()
        TitleText("Wait for More...")
    }
}