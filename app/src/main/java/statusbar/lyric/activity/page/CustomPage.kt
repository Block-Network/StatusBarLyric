package statusbar.lyric.activity.page

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.View
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.R
import statusbar.lyric.activity.SettingsActivity
import statusbar.lyric.utils.ActivityOwnSP
import statusbar.lyric.utils.ActivityUtils
import statusbar.lyric.utils.ktx.lpparam
import java.util.HashMap

@SuppressLint("NonConstantResourceId")
@BMPage("custom", titleId = R.string.Custom)
class CustomPage : BasePage() {
    override fun onCreate() {
        TextSummaryArrow(TextSummaryV(textId = R.string.LyricColor, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.LyricColor)
                setMessage(R.string.LyricColorTips)
                setEditText(ActivityOwnSP.ownSPConfig.getLyricColor(), "#FFFFFF")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            Color.parseColor(getEditText())
                            ActivityOwnSP.ownSPConfig.setLyricColor(getEditText())
                            SettingsActivity.updateConfig = true
                            dismiss()
                            return@setRButton
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.LyricColorError))
                    ActivityOwnSP.ownSPConfig.setLyricColor("")
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        }))
        TextSummaryArrow(TextSummaryV(textId = R.string.BackgroundColor, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.BackgroundColor)
                setMessage(R.string.LyricColorTips)
                setEditText(ActivityOwnSP.ownSPConfig.getBackgroundColor(), "#FFFFFF")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            Color.parseColor(getEditText())
                            ActivityOwnSP.ownSPConfig.setBackgroundColor(getEditText())
                            SettingsActivity.updateConfig = true
                            dismiss()
                            return@setRButton
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.LyricColorError))
                    ActivityOwnSP.ownSPConfig.setBackgroundColor("")
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        }))
        TextSummaryArrow(TextSummaryV(textId = R.string.BgCorners, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.BgCorners)
                setMessage(R.string.LyricHighTips)
                setEditText(ActivityOwnSP.ownSPConfig.getBgCorners().toString(), "30")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (0..100)) {
                                ActivityOwnSP.ownSPConfig.setBgCorners(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setBgCorners(0)
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        }))
        Text(textId = R.string.LyricSize, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.LyricSize)
                setMessage(R.string.LyricSizeTips)
                setEditText(ActivityOwnSP.ownSPConfig.getLyricSize().toString(), "0")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (0..50)) {
                                ActivityOwnSP.ownSPConfig.setLyricSize(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setLyricSize(0)
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        })
        SeekBarWithText("LSize", 0, 50)
        val dataBinding = GetDataBinding({ ActivityOwnSP.ownSPConfig.getLyricWidth() }) { view, flags, data ->
            when (flags) {
                1 -> view.visibility = if ((data as Int) == -1) View.VISIBLE else View.GONE
                2 -> view.visibility = if ((data as Int) == -1) View.VISIBLE else View.GONE
            }
        }
        Text(textId = R.string.LyricWidth, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.LyricWidth)
                setMessage(R.string.LyricTips)
                setEditText(ActivityOwnSP.ownSPConfig.getLyricWidth().toString(), "-1")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (-1..100)) {
                                ActivityOwnSP.ownSPConfig.setLyricWidth(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }

                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setLyricWidth(-1)
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        })
        SeekBarWithText("LWidth", -1, 100, defaultProgress = -1, dataBindingSend = dataBinding.bindingSend)
        Text(textId = R.string.LyricAutoMaxWidth, dataBindingRecv = dataBinding.binding.getRecv(1), onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.LyricAutoMaxWidth)
                setMessage(R.string.LyricTips)
                setEditText(ActivityOwnSP.ownSPConfig.getLyricMaxWidth().toString(), "-1")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (-1..100)) {
                                ActivityOwnSP.ownSPConfig.setLyricMaxWidth(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setLyricMaxWidth(-1)
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        })
        SeekBarWithText("LMaxWidth", -1, 100, defaultProgress = -1, dataBindingRecv = dataBinding.binding.getRecv(2))
        Text(textId = R.string.LyricPos, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.LyricPos)
                setMessage(R.string.LyricPosTips)
                setEditText(ActivityOwnSP.ownSPConfig.getLyricPosition().toString(), "0")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (-900..900)) {
                                ActivityOwnSP.ownSPConfig.setLyricPosition(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setLyricPosition(0)
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        })
        SeekBarWithText("LPosition", -900, 900)
        Text(textId = R.string.LyricHigh, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.LyricHigh)
                setMessage(R.string.LyricHighTips)
                setEditText(ActivityOwnSP.ownSPConfig.getLyricHigh().toString(), "0")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (-100..100)) {
                                ActivityOwnSP.ownSPConfig.setLyricHigh(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setLyricHigh(0)
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        })
        SeekBarWithText("LHigh", -100, 100)
        Text(textId = R.string.FontWeight, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.FontWeight)
                setEditText(ActivityOwnSP.ownSPConfig.getLyricFontWeight().toString(), "0")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (0..400)) {
                                ActivityOwnSP.ownSPConfig.setLyricFontWeight(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setLyricFontWeight(0)
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        })
        SeekBarWithText("LFontWeight", 0, 400)
        Text(textId = R.string.LyricSpacing, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.LyricSpacing)
                setEditText(ActivityOwnSP.ownSPConfig.getLyricSpacing().toString(), "0")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (0..200)) {
                                ActivityOwnSP.ownSPConfig.setLyricSpacing(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setLyricSpacing(0)
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        })
        SeekBarWithText("LSpacing", 0, 200)
        val aDicts: HashMap<String, String> = hashMapOf()
        aDicts["off"] = getString(R.string.Off)
        aDicts["top"] = getString(R.string.top)
        aDicts["lower"] = getString(R.string.lower)
        aDicts["left"] = getString(R.string.left)
        aDicts["right"] = getString(R.string.right)
        aDicts["random"] = getString(R.string.random)
        TextWithSpinner(TextV(textId = R.string.LyricsAnimation), SpinnerV(aDicts[ActivityOwnSP.ownSPConfig.getAnim()]!!) {
            add(getString(R.string.Off)) { ActivityOwnSP.ownSPConfig.setAnim("off") }
            add(getString(R.string.top)) { ActivityOwnSP.ownSPConfig.setAnim("top") }
            add(getString(R.string.lower)) { ActivityOwnSP.ownSPConfig.setAnim("lower") }
            add(getString(R.string.left)) { ActivityOwnSP.ownSPConfig.setAnim("left") }
            add(getString(R.string.right)) { ActivityOwnSP.ownSPConfig.setAnim("right") }
            add(getString(R.string.random)) { ActivityOwnSP.ownSPConfig.setAnim("random") }
        })
        val timeBinding = GetDataBinding({ ActivityOwnSP.ownSPConfig.getHideTime() }) { view, flags, data ->
            when (flags) {
                2 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextWithSwitch(TextV(textId = R.string.HideTime), SwitchV("HideTime", true, dataBindingSend = timeBinding.bindingSend), dataBindingRecv = timeBinding.binding.getRecv(1))
        TextWithSwitch(TextV(textId = R.string.ClickLyric), SwitchV("LSwitch", false), dataBindingRecv = timeBinding.binding.getRecv(2))
        val meiZuStyle = GetDataBinding({ ActivityOwnSP.ownSPConfig.getLyricStyle() }) { view, flags, data ->
            when (flags) {
                2 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextWithSwitch(TextV(textId = R.string.MeizuStyle, dataBindingRecv = meiZuStyle.binding.getRecv(1)), SwitchV("LStyle", true, dataBindingSend = meiZuStyle.bindingSend))
        TextWithSwitch(TextV(textId = R.string.FadingEdge), SwitchV("FadingEdge", false))
        Text(textId = R.string.LyricSpeed, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.LyricSpeed)
                setEditText(ActivityOwnSP.ownSPConfig.getLyricSpeed().toString(), "100")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (0..200)) {
                                ActivityOwnSP.ownSPConfig.setLyricSpeed(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setLyricSpeed(100)
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        }, dataBindingRecv = meiZuStyle.binding.getRecv(2))
        SeekBarWithText("LSpeed", 0, 200, defaultProgress = 100, dataBindingRecv = meiZuStyle.binding.getRecv(2))
        val lDict: HashMap<Boolean, String> = hashMapOf()
        lDict[true] = getString(R.string.First)
        lDict[false] = getString(R.string.Latest)
        TextWithSpinner(TextV(textId = R.string.LyricViewPosition), SpinnerV(lDict[ActivityOwnSP.ownSPConfig.getLyricViewPosition()]!!) {
            add(getString(R.string.First)) { ActivityOwnSP.ownSPConfig.setLyricViewPosition(true) }
            add(getString(R.string.Latest)) { ActivityOwnSP.ownSPConfig.setLyricViewPosition(false) }
        })
        val cDict: HashMap<Boolean, String> = hashMapOf()
        cDict[false] = getString(R.string.Latest)
        cDict[true] = getString(R.string.First)
        TextWithSpinner(TextV(textId = R.string.CustomizePosition), SpinnerV(cDict[ActivityOwnSP.ownSPConfig.getCustomizeViewPosition()]!!) {
            add(getString(R.string.First)) { ActivityOwnSP.ownSPConfig.setCustomizeViewPosition(true) }
            add(getString(R.string.Latest)) { ActivityOwnSP.ownSPConfig.setCustomizeViewPosition(false) }
        })
        TextSummaryArrow(TextSummaryV(textId = R.string.CustomizeText, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.CustomizeText)
                setEditText(ActivityOwnSP.ownSPConfig.getCustomizeText(), "")
                setRButton(R.string.Ok) {
                    try {
                        val value = getEditText()
                        ActivityOwnSP.ownSPConfig.setCustomizeText(value)
                    } catch (_: Throwable) {
                    }
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) {
                    dismiss()
                }
            }.show()
        }))
        TextSummaryArrow(TextSummaryV(textId = R.string.CustomFont, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.CustomFont)
                setRButton(R.string.ChooseFont) {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "*/*"
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    activity.startActivityForResult(intent, SettingsActivity.OPEN_FONT_FILE)
                    dismiss()
                }
                setLButton(R.string.Reset) {
                    activity.sendBroadcast(Intent().apply {
                        action = "Lyric_Server"
                        putExtra("Lyric_Type", "delete_font")
                        putExtra("Lyric_PackageName", activity.packageName)
                    })
                    dismiss()
                }
            }.show()
        }))
        Line()
        TitleText(textId = R.string.IconSettings)
        TextWithSwitch(TextV(textId = R.string.LyricIcon), SwitchV("I", true))
        TextWithSwitch(TextV(textId = R.string.ShowEmptyIcon), SwitchV("ShowEmptyIcon", false))
        TextSummaryArrow(TextSummaryV(textId = R.string.IconColor, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.IconColor)
                setMessage(R.string.LyricColorTips)
                setEditText(ActivityOwnSP.ownSPConfig.getIconColor(), "#FFFFFF")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            Color.parseColor(getEditText())
                            ActivityOwnSP.ownSPConfig.setIconColor(getEditText())
                            SettingsActivity.updateConfig = true
                            dismiss()
                            return@setRButton
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.LyricColorError))
                    ActivityOwnSP.ownSPConfig.setIconColor("")
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        }))
        Text(textId = R.string.IconSize, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.IconSize)
                setMessage(R.string.LyricSizeTips)
                setEditText(ActivityOwnSP.ownSPConfig.getIconSize().toString(), "0")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (0..50)) {
                                ActivityOwnSP.ownSPConfig.setIconSize(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setIconSize(0)
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        })
        SeekBarWithText("ISize", 0, 50, -1)
        Text(textId = R.string.IconHigh, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.IconHigh)
                setMessage(R.string.LyricHighTips)
                setEditText(ActivityOwnSP.ownSPConfig.getIconHigh().toString(), "0")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (-100..100)) {
                                ActivityOwnSP.ownSPConfig.setIconHigh(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setIconHigh(0)
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        })
        SeekBarWithText("IHigh", -100, 100, defaultProgress = ActivityOwnSP.ownSPConfig.getIconHigh())
        Text(textId = R.string.IconSpacing, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.IconSpacing)
                setMessage(R.string.IconSpacingTips)
                setEditText(ActivityOwnSP.ownSPConfig.getIconspacing().toString(), "5")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (0..20)) {
                                ActivityOwnSP.ownSPConfig.setIconspacing(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setIconspacing(5)
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        })
        SeekBarWithText("ISpacing", 0, 20, defaultProgress = ActivityOwnSP.ownSPConfig.getIconspacing())
        TextSummaryArrow(TextSummaryV(textId = R.string.IconSettings, onClickListener = {
            showFragment("icon")
        }))
        Text()
    }
}