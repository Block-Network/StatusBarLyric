package statusbar.lyric.activity.page

import android.annotation.SuppressLint
import android.view.View
import android.widget.LinearLayout
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import cn.fkj233.ui.switch.MIUISwitch
import statusbar.lyric.R
import statusbar.lyric.activity.SettingsActivity
import statusbar.lyric.utils.ActivityOwnSP
import statusbar.lyric.utils.ActivityUtils

@SuppressLint("NonConstantResourceId")
@BMPage("advancedSettings", titleId = R.string.AdvancedSettings)
class AdvancedSettingsPage : BasePage() {
    override fun onCreate() {
        TextSummaryWithSwitch(TextSummaryV(textId = R.string.JudgementTitle), SwitchV("JudgementTitle"))
        TextSummaryWithSwitch(TextSummaryV(textId = R.string.GetTitle), SwitchV("GetTitle"))
        TextSummaryWithSwitch(TextSummaryV(textId = R.string.OnlyGetLyric, tipsId = R.string.OnlyGetLyricTips), SwitchV("OnlyGetLyric"))
        TextWithSwitch(TextV(textId = R.string.TimeHide), SwitchV("TimeOff"))
        TextSummaryArrow(TextSummaryV(textId = R.string.TimeHideTime, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.TimeHideTime)
                setMessage(R.string.AntiBurnTimeTips)
                setEditText(ActivityOwnSP.ownSPConfig.getTimeOffTime().toString(), "10000")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (0..3600000)) {
                                ActivityOwnSP.ownSPConfig.setTimeOffTime(value)
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityOwnSP.ownSPConfig.setTimeOffTime(10000)
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        }))
        SeekBarWithText("TimeOffTime", 0, 3600000, defaultProgress = 10000)

        TextSummaryArrow(TextSummaryV(textId = R.string.CustomHook, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.CustomHook)
                setMessage(R.string.CustomHookTips)
                setEditText(ActivityOwnSP.ownSPConfig.getHook(), getString(R.string.InputCustomHook))
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            ActivityOwnSP.ownSPConfig.setHook(getEditText())
                            ActivityUtils.showToastOnLooper(activity, String.format("%s %s\n%s", getString(R.string.HookSetTips), ActivityOwnSP.ownSPConfig.getHook(), getString(R.string.RestartSystemUI)))
                            dismiss()
                            return@setRButton
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityOwnSP.ownSPConfig.setHook("")
                    ActivityUtils.showToastOnLooper(activity, String.format("%s %s\n%s", getString(R.string.HookSetTips), getString(R.string.Default), getString(R.string.RestartSystemUI)))
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        }))


        val antiBurnBinding = GetDataBinding({ ActivityOwnSP.ownSPConfig.getOldAntiBurn() }) { view, flags, data ->
            when (flags) {
                2 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextWithSwitch(TextV(textId = R.string.OldAbScreen), SwitchV("OldAntiBurn", true, dataBindingSend = antiBurnBinding.bindingSend))
        Text(textId = R.string.AntiBurnTime, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.AntiBurnTime)
                setMessage(R.string.AntiBurnTimeTips)
                setEditText(ActivityOwnSP.ownSPConfig.getAntiBurnTime().toString(), "60000")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (1..3600000)) {
                                ActivityOwnSP.ownSPConfig.setAntiBurnTime(value)
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setAntiBurnTime(6000)
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        }, dataBindingRecv = antiBurnBinding.binding.getRecv(2))
        SeekBarWithText("AntiBurnTime", 1, 3600000, defaultProgress = 60000, dataBindingRecv = antiBurnBinding.binding.getRecv(2))

        val dataBinding = GetDataBinding({ ActivityOwnSP.ownSPConfig.getUseSystemReverseColor() }) { view, flags, data ->
            when (flags) {
                2 -> view.visibility = if ((data as Boolean)) View.GONE else View.VISIBLE
            }
        }
        TextWithSwitch(TextV(textId = R.string.UseSystemReverseColor), SwitchV("UseSystemReverseColor", true, dataBindingSend = dataBinding.bindingSend))
        Text(textId = R.string.ReverseColorTime, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.ReverseColorTime)
                setMessage(R.string.ReverseColorTimeTips)
                setEditText(ActivityOwnSP.ownSPConfig.getReverseColorTime().toString(), "1")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (1..60000)) {
                                ActivityOwnSP.ownSPConfig.setReverseColorTime(value)
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setReverseColorTime(3000)
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        }, dataBindingRecv = dataBinding.binding.getRecv(2))
        SeekBarWithText("ReverseColorTime", 1, 3000, defaultProgress = 1, dataBindingRecv = dataBinding.binding.getRecv(2))
        val autoOffBinding = GetDataBinding({ ActivityOwnSP.ownSPConfig.getLyricOldAutoOff() }) { view, flags, data ->
            when (flags) {
                2 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }
        TextWithSwitch(TextV(textId = R.string.SongPauseCloseLyrics), SwitchV("LOldAutoOff", false, dataBindingSend = autoOffBinding.bindingSend))
        Text(textId = R.string.SongPauseCloseLyricsTime, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.SongPauseCloseLyricsTime)
                setMessage(R.string.ReverseColorTimeTips)
                setEditText(ActivityOwnSP.ownSPConfig.getLyricAutoOffTime().toString(), "1000")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (1..1000)) {
                                ActivityOwnSP.ownSPConfig.setLyricAutoOffTime(value)
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setLyricAutoOffTime(1000)
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        }, dataBindingRecv = autoOffBinding.binding.getRecv(2))
        SeekBarWithText("LyricAutoOffTime", 1, 3000, defaultProgress = 1000, dataBindingRecv = autoOffBinding.binding.getRecv(2))
        TextWithSwitch(TextV(textId = R.string.UnlockShow), SwitchV("LockScreenOff"))
        TextWithSwitch(TextV(textId = R.string.AutoHideNotiIcon), SwitchV("HNoticeIcon"))
        TextWithSwitch(TextV(textId = R.string.HideNetWork), SwitchV("HNetSpeed"))
        TextWithSwitch(TextV(textId = R.string.AutoHideCarrierName), SwitchV("HCuk"))
        Text(textId = R.string.DelayedLoading, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.DelayedLoading)
                setEditText(ActivityOwnSP.ownSPConfig.getDelayedLoading().toString(), "1")
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            val value = getEditText().toInt()
                            if (value in (1..5)) {
                                ActivityOwnSP.ownSPConfig.setDelayedLoading(value)
                                dismiss()
                                return@setRButton
                            }
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setDelayedLoading(1)
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        })
        SeekBarWithText("DelayedLoadingTime", 1, 5, 1)
        TextSummaryArrow(TextSummaryV(textId = R.string.BlockLyric, tipsId = R.string.BlockLyricTips, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.BlockLyric)
                setMessage(R.string.BlockLyricTips)
                setEditText(ActivityOwnSP.ownSPConfig.getBlockLyric(), "")
                addView(LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    setPadding(dp2px(context, 25f), 0, dp2px(context, 25f), 0)
                    addView(TextV(textId = R.string.BlockLyricMode).create(context, null))
                    addView(MIUISwitch(context).apply {
                        isChecked = ActivityOwnSP.ownSPConfig.getBlockLyricMode()
                        setOnClickListener {
                            ActivityOwnSP.ownSPConfig.setBlockLyricMode(isChecked)
                            SettingsActivity.updateConfig = true
                        }
                    })
                })
                addView(LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    setPadding(dp2px(context, 25f), 0, dp2px(context, 25f), 0)
                    addView(TextV(textId = R.string.BlockLyricOff).create(context, null))
                    addView(MIUISwitch(context).apply {
                        isChecked = ActivityOwnSP.ownSPConfig.getBlockLyricOff()
                        setOnClickListener {
                            ActivityOwnSP.ownSPConfig.setBlockLyricOff(isChecked)
                            SettingsActivity.updateConfig = true
                        }
                    })
                })
                setRButton(R.string.Ok) {
                    if (getEditText().isNotEmpty()) {
                        try {
                            ActivityOwnSP.ownSPConfig.setBlockLyric(getEditText())
                            dismiss()
                            SettingsActivity.updateConfig = true
                            return@setRButton
                        } catch (_: Throwable) {
                        }
                    }
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                    ActivityOwnSP.ownSPConfig.setBlockLyric("")
                    SettingsActivity.updateConfig = true
                    dismiss()
                }
                setLButton(R.string.Cancel) { dismiss() }
            }.show()
        }))
        Text()
    }
}