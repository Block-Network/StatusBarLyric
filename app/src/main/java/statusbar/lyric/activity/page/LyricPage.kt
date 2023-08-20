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
        TextSA(textId = R.string.lyric_width, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_width))
                setMessage(getString(R.string.lyric_width_tips))
                setEditText(config.lyricWidth.toString(), "0", config = {
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.ok)) {
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
                        ActivityTools.showToastOnLooper(getString(R.string.input_error))
                    }
                }
                setLButton(getString(R.string.cancel))
                finally {
                    dismiss()
                }
            }.show()
        })
        SeekBarWithText(key = "lyricWidth", min = 0, max = 100, defaultProgress = 0, dataBindingRecv = widthBinding.getRecv(2), dataBindingSend = widthBinding.bindingSend, callBacks = { _, _ ->
            changeConfig()
        })
        TextSSw(textId = R.string.fixed_lyric_width, tipsId = R.string.fixed_lyric_width_tips, key = "fixedLyricWidth", onClickListener = { changeConfig() }, dataBindingRecv = widthBinding.getRecv(1))
        TextSA(textId = R.string.lyric_size, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_size))
                setMessage(getString(R.string.lyric_size_tips))
                setEditText(config.lyricSize.toString(), "0", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..100) {
                            config.lyricSize = value
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
        })
        TextSA(textId = R.string.lyric_color_and_transparency, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_color_and_transparency))
                setMessage(getString(R.string.lyric_color_and_transparency_tips))
                setEditText(config.lyricColor, "#FFFFFF", config = {
                    it.inputType = InputType.TYPE_CLASS_TEXT
                    it.filters = arrayOf(InputFilter.LengthFilter(9))
                })
                setRButton(getString(R.string.ok)) {
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
                        ActivityTools.showToastOnLooper(getString(R.string.input_error))
                    }
                }
                setLButton(getString(R.string.cancel))
                finally { dismiss() }
            }.show()
        })
        TextSA(textId = R.string.lyric_background_color_and_transparency, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_background_color_and_transparency))
                setMessage(getString(R.string.lyric_background_color_and_transparency_tips))
                setEditText(config.lyricBackgroundColor, "#00000000", config = {
                    it.inputType = InputType.TYPE_CLASS_TEXT
                    it.filters = arrayOf(InputFilter.LengthFilter(9))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText()
                        Color.parseColor(value)
                        config.lyricBackgroundColor = value
                        changeConfig()
                    } catch (_: Exception) {
                        ActivityTools.showToastOnLooper(getString(R.string.input_error))
                    }
                }
                setLButton(getString(R.string.cancel))
                finally { dismiss() }
            }.show()
        })
        TextSA(textId = R.string.lyric_background_radius, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_background_radius))
                setMessage(getString(R.string.lyric_background_radius_tips))
                setEditText(config.lyricBackgroundRadius.toString(), "0", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..100) {
                            config.lyricBackgroundRadius = value
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
        })
        TextSA(textId = R.string.lyric_letter_spacing, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_letter_spacing))
                setMessage(getString(R.string.lyric_letter_spacing_tips))
                setEditText(config.lyricLetterSpacing.toString(), "0", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..50) {
                            config.lyricLetterSpacing = value
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
        })
        TextSA(textId = R.string.lyric_stroke_width, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_stroke_width))
                setMessage(getString(R.string.lyric_stroke_width_tips))
                setEditText(config.lyricStrokeWidth.toString(), "130", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..400) {
                            config.lyricStrokeWidth = value
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
        })
        TextSA(textId = R.string.lyric_speed, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_speed))
                setMessage(getString(R.string.lyric_speed_tips))
                setEditText(config.lyricSpeed.toString(), "4", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(2))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..10) {
                            config.lyricSpeed = value
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
        })
        TextSA(textId = R.string.lyric_top_margins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_top_margins))
                setMessage(getString(R.string.lyric_top_margins_tips))
                setEditText(config.lyricTopMargins.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -100..100) {
                            config.lyricTopMargins = value
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
        })
        TextSA(textId = R.string.lyric_bottom_margins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_bottom_margins))
                setMessage(getString(R.string.lyric_bottom_margins_tips))
                setEditText(config.lyricBottomMargins.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -100..100) {
                            config.lyricBottomMargins = value
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
        })
        TextSA(textId = R.string.lyric_start_margins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_start_margins))
                setMessage(getString(R.string.lyric_start_margins_tips))
                setEditText(config.lyricStartMargins.toString(), "7", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -500..500) {
                            config.lyricStartMargins = value
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
        })
        TextSA(textId = R.string.lyric_end_margins, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_end_margins))
                setMessage(getString(R.string.lyric_end_margins_tips))
                setEditText(config.lyricEndMargins.toString(), "0", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in -500..500) {
                            config.lyricEndMargins = value
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
        })
        TextSw(textId = R.string.hide_time, key = "hideTime", defValue = true, onClickListener = {
            changeConfig()
        })
        val animationMaps: LinkedHashMap<String, String> = LinkedHashMap<String, String>().apply {
            this["None"] = getString(R.string.lyrics_animation_none)
            this["Top"] = getString(R.string.lyrics_animation_top)
            this["Bottom"] = getString(R.string.lyrics_animation_bottom)
            this["Start"] = getString(R.string.lyrics_animation_start)
            this["End"] = getString(R.string.lyrics_animation_end)
            this["ScaleX"] = getString(R.string.lyrics_animation_scale_x)
            this["ScaleY"] = getString(R.string.lyrics_animation_scale_y)
            this["ScaleXY"] = getString(R.string.lyrics_animation_scale_x_y)
            this["Random"] = getString(R.string.lyrics_animation_random)
        }
        TextSSp(textId = R.string.lyrics_animation, currentValue = animationMaps[config.animation].toString(), data = {
            animationMaps.forEach {
                add(it.value) {
                    config.animation = (it.key)
                    changeConfig()
                }
            }
        })
        val interpolatorMaps: LinkedHashMap<String, String> = LinkedHashMap<String, String>().apply {
            this["Linear"] = getString(R.string.lyrics_interpolator_linear)
            this["Accelerate"] = getString(R.string.lyrics_interpolator_accelerate)
            this["Decelerate"] = getString(R.string.lyrics_interpolator_decelerate)
            this["Accelerate&Decelerate"] = getString(R.string.lyrics_interpolator_accelerate_decelerate)
            this["Overshoot"] = getString(R.string.lyrics_interpolator_overshoot)
            this["Bounce"] = getString(R.string.lyrics_interpolator_bounce)
        }
        TextSSp(textId = R.string.lyrics_animation_interpolator, currentValue = interpolatorMaps[config.interpolator].toString(), data = {
            interpolatorMaps.forEach {
                add(it.value) {
                    config.interpolator = (it.key)
                    changeConfig()
                }
            }
        })
        TextSA(textId = R.string.lyrics_animation_duration, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyrics_animation_duration))
                setMessage(getString(R.string.lyric_animation_duration_tips))
                setEditText(config.animationDuration.toString(), "500", config = {
                    it.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 300..1000) {
                            config.animationDuration = value
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
        })
    }
}