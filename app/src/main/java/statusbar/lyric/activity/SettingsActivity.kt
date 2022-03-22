/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/577fkj/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/StatusBarLyric/blob/main/LICENSE>.
 */

@file:Suppress("DEPRECATION")

package statusbar.lyric.activity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.data.MIUIPopupData
import cn.fkj233.ui.activity.view.*
import cn.fkj233.ui.dialog.MIUIDialog
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.AbstractCrashesListener
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog
import com.microsoft.appcenter.crashes.model.ErrorReport
import ice.lib.ads.admob.AdmobManager
import ice.lib.ads.admob.BannerAdConfig
import ice.lib.ads.admob.result.RewardedAdLoadResult
import ice.lib.ads.admob.result.RewardedAdShowResult
import statusbar.lyric.App
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.config.IconConfig
import statusbar.lyric.utils.*
import java.util.*
import kotlin.system.exitProcess
import ice.lib.ads.admob.result.RewardedAdShowResult.Enum as RewardedShowEnum


class SettingsActivity : MIUIActivity() {
    private val activity = this
    private var isRegister = false
    private var updateConfig = false

    companion object {
        const val OPEN_FONT_FILE = 2114745
    }

    init {
        setAllCallBacks {
            updateConfig = true
        }
        initView {
            registerMain(getString(R.string.AppName)) {
                val adBinding = GetDataBinding(0) { view: View, i: Int, any: Any ->
                    if (i == 1) {
                        view.visibility = if (any == 0) View.GONE else View.VISIBLE
                    }
                }
                App.admobManager.preloadRewardedAds(5) { callback ->
                    Log.d("StatusbarLyric-Ad", "${callback.result}: ${callback.any}")
                    if (callback.result == RewardedAdLoadResult.Enum.Success) {
                        adBinding.bindingSend.send(-1)
                    }
                }
                TextSummaryArrow(TextSummaryV(textId = R.string.ApplicableVersion, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.VerExplanation)
                        setMessage(
                            String.format(
                                " %s [%s] %s",
                                getString(R.string.CurrentVer),
                                BuildConfig.VERSION_NAME,
                                getString(R.string.VerExp)
                            )
                        )
                        setRButton(R.string.Done) {
                            dismiss()
                        }
                    }.show()
                }))
                TextSummaryArrow(TextSummaryV(textId = R.string.WarnExplanation, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.WarnExplanation)
                        setMessage(
                            String.format(
                                " %s [%s] %s",
                                getString(R.string.CurrentVer),
                                BuildConfig.VERSION_NAME,
                                getString(R.string.WarnExp)
                            )
                        )
                        setRButton(R.string.Done) {
                            dismiss()
                        }
                    }.show()
                }, colorId = android.R.color.holo_blue_dark))
                TextSummaryArrow(
                    TextSummaryV(
                        textId = R.string.Manual,
                        onClickListener = { ActivityUtils.openUrl(activity, "https://app.xiaowine.cc") },
                        colorId = android.R.color.holo_red_dark
                    )
                )
                val givenList =
                    listOf(getString(R.string.TitleTips1), getString(R.string.TitleTips2), getString(R.string.FirstTip))
                val randomElement = givenList[Random().nextInt(givenList.size)]
                TitleText(text = randomElement)
                Line()
                TitleText(resId = R.string.BaseSetting)
                TextWithSwitch(TextV(resId = R.string.AllSwitch), SwitchV("LService"))
                TextSummaryArrow(TextSummaryV(
                    textId = R.string.Custom,
                    onClickListener = { showFragment("custom") }
                ))
                TextSummaryArrow(TextSummaryV(
                    textId = R.string.AdvancedSettings,
                    onClickListener = { showFragment("advancedSettings") }
                ))
                Line()
                TitleTextV(resId = R.string.About)
                TextSummaryArrow(
                    TextSummaryV(
                        "${getString(R.string.CheckUpdate)} (${BuildConfig.VERSION_NAME})",
                        onClickListener = {
                            ActivityUtils.showToastOnLooper(
                                activity,
                                getString(R.string.StartCheckUpdate)
                            )
                            ActivityUtils.checkUpdate(activity)
                        })
                )
                TextSummaryArrow(TextSummaryV(
                    textId = R.string.showAd,
                    onClickListener = {
                        App.admobManager.showRewardAds(activity, true) { callback: RewardedAdShowResult ->
                            when (callback.result) {
                                RewardedShowEnum.Null -> {}
                                RewardedShowEnum.OnClicked -> {}
                                RewardedShowEnum.OnDismissed -> {}
                                RewardedShowEnum.OnEarnedReward -> {}
                                RewardedShowEnum.OnFailedToShow -> {}
                                RewardedShowEnum.OnShowed -> {}
                                RewardedShowEnum.ImmediatelyLoaded -> {}
                                RewardedShowEnum.ImmediatelyLoadFailed -> {}
                                else -> {}
                            }
                        }
                    }
                ), dataBindingRecv = adBinding.binding.getRecv(1))
                TextSummaryArrow(TextSummaryV(
                    textId = R.string.AboutModule,
                    onClickListener = { showFragment("about") }
                ))
                if (ActivityOwnSP.ownSPConfig.getAd()) {
                    CustomView(
                        AdmobManager.getBannerAd(
                            activity,
                            BannerAdConfig("ca-app-pub-9730534578915916/8650086997")
                        )
                    )
                }
                Text()
            }

            registerMenu(getString(R.string.Menu)) {
                TextWithSwitch(
                    TextV(resId = R.string.HideDeskIcon),
                    SwitchV("hLauncherIcon", customOnCheckedChangeListener = {
                        packageManager.setComponentEnabledSetting(
                            ComponentName(activity, "${BuildConfig.APPLICATION_ID}.launcher"),
                            if (it) {
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                            } else {
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                            },
                            PackageManager.DONT_KILL_APP
                        )
                    })
                )
                TextWithSwitch(TextV(resId = R.string.DebugMode), SwitchV("Debug"))
                TextWithSwitch(TextV(text = "App Center"), SwitchV("AppCenter", true))
                TextSummaryArrow(TextSummaryV(textId = R.string.ResetModule, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.ResetModuleDialog)
                        setMessage(R.string.ResetModuleDialogTips)
                        setRButton(R.string.Ok) {
                            ActivityUtils.cleanConfig(
                                activity
                            )
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                }))
                TextSummaryArrow(TextSummaryV(textId = R.string.ReStartSystemUI, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.RestartUI)
                        setMessage(R.string.RestartUITips)
                        setRButton(R.string.Ok) {
                            Utils.voidShell("pkill -f com.android.systemui", true)
                            Analytics.trackEvent("重启SystemUI")
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                }))
                TextSummaryArrow(TextSummaryV(textId = R.string.Backup, onClickListener = {
                    BackupUtils.backup(activity, getSP())
                }))
                TextSummaryArrow(TextSummaryV(textId = R.string.Recovery, onClickListener = {
                    BackupUtils.recovery(activity, getSP())
                }))
                TextSummaryArrow(TextSummaryV(textId = R.string.Test, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.Test)
                        setMessage(R.string.TestDialogTips)
                        setRButton(R.string.Start) {
                            ActivityUtils.showToastOnLooper(activity, "尝试唤醒界面")
                            activity.sendBroadcast(
                                Intent().apply {
                                    action = "Lyric_Server"
                                    putExtra("Lyric_Type", "test")
                                }
                            )
                            dismiss()
                        }
                        setLButton(R.string.Back) { dismiss() }
                    }.show()
                }))
                Line()
                TitleText("Module Version")
                Text("${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})-${BuildConfig.BUILD_TYPE}")
                if (ActivityOwnSP.ownSPConfig.getAd()) {
                    CustomView(
                        AdmobManager.getBannerAd(
                            activity,
                            BannerAdConfig("ca-app-pub-9730534578915916/8650086997")
                        )
                    )
                }
            }

            register("custom", getString(R.string.Custom)) {
                TextSummaryArrow(TextSummaryV(textId = R.string.LyricColor, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.LyricColor)
                        setMessage(R.string.LyricColorTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getLyricColor(), "#FFFFFF")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setLyricColor("")
                            } else {
                                try {
                                    Color.parseColor(getEditText())
                                    ActivityOwnSP.ownSPConfig.setLyricColor(getEditText())
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(
                                        activity,
                                        getString(R.string.LyricColorError)
                                    )
                                    ActivityOwnSP.ownSPConfig.setLyricColor("")
                                }
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                }))
                Text(resId = R.string.LyricSize, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.LyricSize)
                        setMessage(R.string.LyricSizeTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getLyricSize().toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setLyricSize(0)
                            } else {
                                try {
                                    ActivityOwnSP.ownSPConfig.setLyricSize(getEditText().toInt())
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(
                                        activity,
                                        getString(R.string.LyricColorError)
                                    )
                                    ActivityOwnSP.ownSPConfig.setLyricSize(0)
                                }
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                SeekBarWithText("LSize", 0, 50)
                val dataBinding = GetDataBinding(ActivityOwnSP.ownSPConfig.getLyricWidth()) { view, flags, data ->
                    when (flags) {
                        1 -> view.visibility = if ((data as Int) == -1) View.VISIBLE else View.GONE
                        2 -> view.visibility = if ((data as Int) == -1) View.VISIBLE else View.GONE
                    }
                }
                Text(resId = R.string.LyricWidth, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.LyricWidth)
                        setMessage(R.string.LyricTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getLyricWidth().toString(), "-1")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setLyricSize(-1)
                            } else {
                                try {
                                    ActivityOwnSP.ownSPConfig.setLyricWidth(getEditText().toInt())
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(
                                        activity,
                                        getString(R.string.LyricColorError)
                                    )
                                    ActivityOwnSP.ownSPConfig.setLyricSize(-1)
                                }
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                SeekBarWithText("LWidth", -1, 100, defaultProgress = -1, dataBindingSend = dataBinding.bindingSend)
                Text(
                    resId = R.string.LyricAutoMaxWidth,
                    dataBindingRecv = dataBinding.binding.getRecv(1),
                    onClickListener = {
                        MIUIDialog(activity) {
                            setTitle(R.string.LyricAutoMaxWidth)
                            setMessage(R.string.LyricTips)
                            setEditText(ActivityOwnSP.ownSPConfig.getLyricMaxWidth().toString(), "-1")
                            setRButton(R.string.Ok) {
                                if (getEditText().isEmpty()) {
                                    ActivityOwnSP.ownSPConfig.setLyricMaxWidth(-1)
                                } else {
                                    try {
                                        ActivityOwnSP.ownSPConfig.setLyricMaxWidth(getEditText().toInt())
                                    } catch (e: Throwable) {
                                        ActivityUtils.showToastOnLooper(
                                            activity,
                                            getString(R.string.LyricColorError)
                                        )
                                        ActivityOwnSP.ownSPConfig.setLyricMaxWidth(-1)
                                    }
                                }
                                dismiss()
                            }
                            setLButton(R.string.Cancel) { dismiss() }
                        }.show()
                    })
                SeekBarWithText(
                    "LMaxWidth",
                    -1,
                    100,
                    defaultProgress = -1,
                    dataBindingRecv = dataBinding.binding.getRecv(2)
                )
                Text(resId = R.string.LyricPos, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.LyricPos)
                        setMessage(R.string.LyricPosTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getLyricPosition().toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setLyricPosition(-1)
                            } else {
                                try {
                                    ActivityOwnSP.ownSPConfig.setLyricPosition(getEditText().toInt())
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(
                                        activity,
                                        getString(R.string.LyricColorError)
                                    )
                                    ActivityOwnSP.ownSPConfig.setLyricPosition(-1)
                                }
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                SeekBarWithText("LPosition", -900, 900)
                Text(resId = R.string.LyricHigh, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.LyricHigh)
                        setMessage(R.string.LyricHighTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getLyricHigh().toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setLyricHigh(-1)
                            } else {
                                try {
                                    ActivityOwnSP.ownSPConfig.setLyricHigh(getEditText().toInt())
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(
                                        activity,
                                        getString(R.string.LyricColorError)
                                    )
                                    ActivityOwnSP.ownSPConfig.setLyricHigh(-1)
                                }
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                        show()
                    }.show()
                })
                SeekBarWithText("LHigh", -100, 100)
                Text(resId = R.string.FontWeight, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.FontWeight)
                        setEditText(ActivityOwnSP.ownSPConfig.getLyricFontWeight().toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setLyricFontWeight(0)
                            } else {
                                ActivityOwnSP.ownSPConfig.setLyricFontWeight(getEditText().toInt())
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                SeekBarWithText("LFontWeight", 0, 400)
                Text(resId = R.string.LyricSpacing, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.LyricSpacing)
                        setEditText(ActivityOwnSP.ownSPConfig.getLyricSpacing().toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.getLyricSpacing(0)
                            } else {
                                ActivityOwnSP.ownSPConfig.getLyricSpacing(getEditText().toInt())
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                SeekBarWithText("LSpacing", 0, 200)
                val dict: HashMap<String, String> = hashMapOf()
                dict["off"] = getString(R.string.Off)
                dict["top"] = getString(R.string.top)
                dict["lower"] = getString(R.string.lower)
                dict["left"] = getString(R.string.left)
                dict["right"] = getString(R.string.right)
                dict["random"] = getString(R.string.random)
                TextWithSpinner(TextV(resId = R.string.LyricsAnimation), SpinnerV(arrayListOf(
                    MIUIPopupData(getString(R.string.Off)) { ActivityOwnSP.ownSPConfig.setAnim("off") },
                    MIUIPopupData(getString(R.string.top)) { ActivityOwnSP.ownSPConfig.setAnim("top") },
                    MIUIPopupData(getString(R.string.lower)) { ActivityOwnSP.ownSPConfig.setAnim("lower") },
                    MIUIPopupData(getString(R.string.left)) { ActivityOwnSP.ownSPConfig.setAnim("left") },
                    MIUIPopupData(getString(R.string.right)) { ActivityOwnSP.ownSPConfig.setAnim("right") },
                    MIUIPopupData(getString(R.string.random)) { ActivityOwnSP.ownSPConfig.setAnim("random") }
                ), dict[ActivityOwnSP.ownSPConfig.getAnim()]!!))
                val timeBinding = GetDataBinding(ActivityOwnSP.ownSPConfig.getHideTime()) { view, flags, data ->
                    when (flags) {
                        2 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                    }
                }
                TextWithSwitch(
                    TextV(resId = R.string.HideTime),
                    SwitchV("HideTime", true, dataBindingSend = timeBinding.bindingSend),
                    dataBindingRecv = timeBinding.binding.getRecv(1)
                )
                TextWithSwitch(
                    TextV(resId = R.string.ClickLyric),
                    SwitchV("LSwitch", false),
                    dataBindingRecv = timeBinding.binding.getRecv(2)
                )
                val pseudoTimeBinding = GetDataBinding(ActivityOwnSP.ownSPConfig.getPseudoTime()) { view, flags, data ->
                    when (flags) {
                        2 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                    }
                }
                TextWithSwitch(
                    TextV(resId = R.string.pseudoTime),
                    SwitchV("PseudoTime", false, dataBindingSend = pseudoTimeBinding.bindingSend),
                    dataBindingRecv = pseudoTimeBinding.binding.getRecv(1)
                )
                TextSummaryArrow(TextSummaryV(textId = R.string.pseudoTimeStyle, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.pseudoTime)
                        setMessage(R.string.pseudoTimeStyleTips)
                        setEditText(
                            ActivityOwnSP.ownSPConfig.getPseudoTimeStyle(),
                            ""
                        )
                        setRButton(R.string.Ok) {
                            ActivityOwnSP.ownSPConfig.setPseudoTimeStyle(getEditText())
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                }), dataBindingRecv = pseudoTimeBinding.binding.getRecv(2))
                val meiZuStyle = GetDataBinding(ActivityOwnSP.ownSPConfig.getLyricStyle()) { view, flags, data ->
                    when (flags) {
                        2 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                    }
                }
                TextWithSwitch(
                    TextV(resId = R.string.MeizuStyle, dataBindingRecv = meiZuStyle.binding.getRecv(1)),
                    SwitchV("LStyle", true, dataBindingSend = meiZuStyle.bindingSend)
                )
                Text(resId = R.string.LyricSpeed, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.LyricSpeed)
                        setEditText(ActivityOwnSP.ownSPConfig.getLyricSpeed().toString(), "100")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setLyricSpeed(100)
                            } else {
                                ActivityOwnSP.ownSPConfig.setLyricSpeed(getEditText().toInt())
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                }, dataBindingRecv = meiZuStyle.binding.getRecv(2))
                SeekBarWithText(
                    "LSpeed",
                    0,
                    200,
                    defaultProgress = 100,
                    dataBindingRecv = meiZuStyle.binding.getRecv(2)
                )
                TextSummaryArrow(TextSummaryV(textId = R.string.CustomFont, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.CustomFont)
                        setRButton(R.string.ChooseFont) {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                            intent.addCategory(Intent.CATEGORY_OPENABLE)
                            intent.type = "*/*"
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                            startActivityForResult(intent, OPEN_FONT_FILE)
                            dismiss()
                        }
                        setLButton(R.string.Reset) {
                            application.sendBroadcast(Intent().apply {
                                action = "Lyric_Server"
                                putExtra("Lyric_Type", "delete_font")
                            })
                            dismiss()
                        }
                    }.show()
                }))
                Line()
                TitleText(resId = R.string.IconSettings)
                TextWithSwitch(TextV(resId = R.string.LyricIcon), SwitchV("I", true))
                TextSummaryArrow(TextSummaryV(textId = R.string.IconColor, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.IconColor)
                        setMessage(R.string.LyricColorTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getIconColor(), "#FFFFFF")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setIconColor("")
                            } else {
                                try {
                                    Color.parseColor(getEditText())
                                    ActivityOwnSP.ownSPConfig.setIconColor(getEditText())
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(
                                        activity,
                                        getString(R.string.LyricColorError)
                                    )
                                    ActivityOwnSP.ownSPConfig.setIconColor("")
                                }
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                }))
                Text(resId = R.string.IconSize, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.IconSize)
                        setMessage(R.string.LyricHighTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getIconSize().toString(), "0")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setIconSize(-1)
                            } else {
                                try {
                                    ActivityOwnSP.ownSPConfig.setIconSize(getEditText().toInt())
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(
                                        activity,
                                        getString(R.string.LyricColorError)
                                    )
                                    ActivityOwnSP.ownSPConfig.setIconSize(-1)
                                }
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                SeekBarWithText("ISize", 0, 50, -1)
                Text(resId = R.string.IconHigh, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.IconHigh)
                        setMessage(R.string.LyricSizeTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getIconHigh().toString(), "-1")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setIconHigh(-1)
                            } else {
                                try {
                                    ActivityOwnSP.ownSPConfig.setIconHigh(getEditText().toInt())
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(
                                        activity,
                                        getString(R.string.LyricColorError)
                                    )
                                    ActivityOwnSP.ownSPConfig.setIconHigh(-1)
                                }
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                })
                SeekBarWithText("IHigh", -100, 100, defaultProgress = ActivityOwnSP.ownSPConfig.getIconHigh())
                TextSummaryArrow(TextSummaryV(textId = R.string.IconSettings, onClickListener = {
                    showFragment("icon")
                }))
                if (ActivityOwnSP.ownSPConfig.getAd()) {
                    CustomView(
                        AdmobManager.getBannerAd(
                            activity,
                            BannerAdConfig("ca-app-pub-9730534578915916/8650086997")
                        )
                    )
                }
                Text()
            }

            register("icon", getString(R.string.IconSettings)) {
                val iconConfig = Utils.getSP(activity, "Icon_Config")?.let { IconConfig(it) }
                for (icon in arrayOf("Netease", "KuGou", "QQMusic", "Myplayer", "MiGu", "MiPlayer", "Default")) {
                    Author(
                        BitmapDrawable(Utils.stringToBitmap(iconConfig?.getIcon(icon))).also { it.setTint(getColor(R.color.customIconColor)) },
                        icon,
                        round = 0f,
                        onClick = {
                            MIUIDialog(activity) {
                                setTitle(icon)
                                setMessage(R.string.MakeIconTitle)
                                setEditText(iconConfig?.getIcon(icon).toString(), "")
                                setRButton(R.string.Ok) {
                                    if (getEditText().isEmpty()) {
                                        iconConfig?.setIcon(icon, iconConfig.getDefaultIcon(icon))
                                    } else {
                                        iconConfig?.setIcon(icon, getEditText())
                                    }
                                    dismiss()
//                                    showFragment("icon")
                                }
                                setLButton(R.string.Cancel) { dismiss() }
                            }.show()
                        })
                }
                TextSummaryArrow(TextSummaryV(textId = R.string.MakeIcon, onClickListener = {
                    val componentName =
                        ComponentName(
                            "com.byyoung.setting",
                            "com.byyoung.setting.MediaFile.activitys.ImageBase64Activity"
                        )
                    val intent = Intent().setClassName("com.byyoung.setting", "utils.ShortcutsActivity")
                    intent.putExtra("PackageName", componentName.packageName)
                    intent.putExtra("PackageClass", componentName.className)
                    try {
                        activity.startActivity(intent)
                    } catch (_: Exception) {
                        ActivityUtils.showToastOnLooper(activity, getString(R.string.MakeIconError))
                    }
                }))

                if (ActivityOwnSP.ownSPConfig.getAd()) {
                    CustomView(
                        AdmobManager.getBannerAd(
                            activity,
                            BannerAdConfig("ca-app-pub-9730534578915916/8650086997")
                        )
                    )
                }
                Text()
            }

            register("advancedSettings", getString(R.string.AdvancedSettings)) {
                TextSummaryArrow(TextSummaryV(textId = R.string.CustomHook, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.CustomHook)
                        setEditText(
                            ActivityOwnSP.ownSPConfig.getHook(),
                            getString(R.string.InputCustomHook)
                        )
                        setRButton(R.string.Ok) {
                            ActivityOwnSP.ownSPConfig.setHook(getEditText())
                            ActivityUtils.showToastOnLooper(
                                activity,
                                String.format(
                                    "%s %s\n%s",
                                    getString(R.string.HookSetTips),
                                    ActivityOwnSP.ownSPConfig.getHook().ifEmpty { getString(R.string.Default) },
                                    getString(R.string.RestartSystemUI)
                                )
                            )
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                }))
                val antiBurnBinding =
                    GetDataBinding(ActivityOwnSP.ownSPConfig.getAntiBurn()) { view, flags, data ->
                        when (flags) {
                            2 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                        }
                    }
                TextWithSwitch(
                    TextV(resId = R.string.AbScreen),
                    SwitchV("AntiBurn", true, dataBindingSend = antiBurnBinding.bindingSend)
                )
                Text(resId = R.string.AntiBurnTime, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.AntiBurnTime)
                        setMessage(R.string.AntiBurnTimeTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getAntiBurnTime().toString(), "60000")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setAntiBurnTime(60000)
                            } else {
                                try {
                                    ActivityOwnSP.ownSPConfig.setAntiBurnTime(getEditText().toInt())
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(
                                        activity,
                                        getString(R.string.LyricColorError)
                                    )
                                    ActivityOwnSP.ownSPConfig.setAntiBurnTime(6000)
                                }
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                }, dataBindingRecv = antiBurnBinding.binding.getRecv(2))
                SeekBarWithText(
                    "AntiBurnTime",
                    1,
                    3600000,
                    defaultProgress = 60000,
                    dataBindingRecv = antiBurnBinding.binding.getRecv(2)
                )
                val dataBinding =
                    GetDataBinding(ActivityOwnSP.ownSPConfig.getUseSystemReverseColor()) { view, flags, data ->
                        when (flags) {
                            2 -> view.visibility = if ((data as Boolean)) View.GONE else View.VISIBLE
                        }
                    }
                TextWithSwitch(
                    TextV(resId = R.string.UseSystemReverseColor),
                    SwitchV("UseSystemReverseColor", true, dataBindingSend = dataBinding.bindingSend)
                )
                Text(resId = R.string.ReverseColorTime, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.ReverseColorTime)
                        setMessage(R.string.ReverseColorTimeTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getReverseColorTime().toString(), "1")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setReverseColorTime(1)
                            } else {
                                try {
                                    ActivityOwnSP.ownSPConfig.setReverseColorTime(getEditText().toInt())
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(
                                        activity,
                                        getString(R.string.LyricColorError)
                                    )
                                    ActivityOwnSP.ownSPConfig.setReverseColorTime(1)
                                }
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                }, dataBindingRecv = dataBinding.binding.getRecv(2))
                SeekBarWithText(
                    "ReverseColorTime",
                    1,
                    3000,
                    defaultProgress = 1,
                    dataBindingRecv = dataBinding.binding.getRecv(2)
                )
                val autoOffBinding =
                    GetDataBinding(ActivityOwnSP.ownSPConfig.getLyricAutoOff()) { view, flags, data ->
                        when (flags) {
                            2 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                        }
                    }
                TextWithSwitch(
                    TextV(resId = R.string.SongPauseCloseLyrics),
                    SwitchV("LAutoOff", true, dataBindingSend = autoOffBinding.bindingSend)
                )
                Text(resId = R.string.SongPauseCloseLyricsTime, onClickListener = {
                    MIUIDialog(activity) {
                        setTitle(R.string.SongPauseCloseLyricsTime)
                        setMessage(R.string.ReverseColorTimeTips)
                        setEditText(ActivityOwnSP.ownSPConfig.getLyricAutoOffTime().toString(), "1")
                        setRButton(R.string.Ok) {
                            if (getEditText().isEmpty()) {
                                ActivityOwnSP.ownSPConfig.setLyricAutoOffTime(1000)
                            } else {
                                try {
                                    ActivityOwnSP.ownSPConfig.setLyricAutoOffTime(getEditText().toInt())
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(
                                        activity,
                                        getString(R.string.LyricColorError)
                                    )
                                    ActivityOwnSP.ownSPConfig.setLyricAutoOffTime(1000)
                                }
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                }, dataBindingRecv = autoOffBinding.binding.getRecv(2))
                SeekBarWithText(
                    "LyricAutoOffTime",
                    1,
                    3000,
                    defaultProgress = 1000,
                    dataBindingRecv = autoOffBinding.binding.getRecv(2)
                )
                TextWithSwitch(TextV(resId = R.string.UnlockShow), SwitchV("LockScreenOff"))
                TextWithSwitch(TextV(resId = R.string.AutoHideNotiIcon), SwitchV("HNoticeIcon"))
                TextWithSwitch(TextV(resId = R.string.HideNetWork), SwitchV("HNetSpeed"))
                TextWithSwitch(TextV(resId = R.string.AutoHideCarrierName), SwitchV("HCuk"))
                val dict: HashMap<String, String> = hashMapOf()
                dict["first"] = getString(R.string.First)
                dict["latest"] = getString(R.string.Latest)
                TextWithSpinner(TextV(resId = R.string.ViewPosition), SpinnerV(arrayListOf(
                    MIUIPopupData(getString(R.string.First)) {
                        ActivityOwnSP.ownSPConfig.setViewPosition(
                            "first"
                        )
                    },
                    MIUIPopupData(getString(R.string.Latest)) {
                        ActivityOwnSP.ownSPConfig.setViewPosition(
                            "latest"
                        )
                    }
                ), dict[ActivityOwnSP.ownSPConfig.getViewPosition()]!!))
                Text(resId = R.string.DelayedLoading, onClickListener = {  MIUIDialog(activity) {
                    setTitle(R.string.DelayedLoading)
                    setEditText(ActivityOwnSP.ownSPConfig.getDelayedLoading().toString(), "0")
                    setRButton(R.string.Ok) {
                        if (getEditText().isEmpty()) {
                            ActivityOwnSP.ownSPConfig.setDelayedLoading(1)
                        } else {
                            try {
                                ActivityOwnSP.ownSPConfig.setDelayedLoading(getEditText().toInt())
                            } catch (e: Throwable) {
                                ActivityUtils.showToastOnLooper(
                                    activity,
                                    getString(R.string.LyricColorError)
                                )
                                ActivityOwnSP.ownSPConfig.setDelayedLoading(1)
                            }
                        }
                        dismiss()
                    }
                    setLButton(R.string.Cancel) { dismiss() }
                }.show()
                })
                SeekBarWithText("DelayedLoadingTime", 1, 5)
                if (ActivityOwnSP.ownSPConfig.getAd()) {
                    CustomView(
                        AdmobManager.getBannerAd(
                            activity,
                            BannerAdConfig("ca-app-pub-9730534578915916/8650086997")
                        )
                    )
                }
                Text()
            }

            register("about", getString(R.string.About)) {
                Author(getDrawable(R.drawable.header_577fkj)!!, "577fkj", getString(R.string.AboutTips1), onClick = {
                    ActivityUtils.openUrl(
                        activity,
                        "https://github.com/577fkj"
                    )
                })
                Author(
                    getDrawable(R.drawable.header_xiaowine)!!,
                    "xiaowine",
                    getString(R.string.AboutTips2),
                    onClick = {
                        ActivityUtils.openUrl(
                            activity,
                            "https://github.com/xiaowine"
                        )
                    })
                Line()
                TitleText(resId = R.string.ThkListTips)
                TextSummaryArrow(TextSummaryV(textId = R.string.ThkListTips, onClickListener = {
                    ActivityUtils.openUrl(
                        activity,
                        "https://github.com/577fkj/StatusBarLyric#%E6%84%9F%E8%B0%A2%E5%90%8D%E5%8D%95%E4%B8%8D%E5%88%86%E5%85%88%E5%90%8E"
                    )
                }))
                TextSummaryArrow(TextSummaryV(textId = R.string.SponsoredList, onClickListener = {
                    ActivityUtils.openUrl(
                        activity,
                        "https://github.com/577fkj/StatusBarLyric/blob/Dev/doc/SPONSOR.md"
                    )
                }))
                Line()
                TitleText(resId = R.string.Other)
                TextSummaryArrow(TextSummaryV(textId = R.string.PrivacyPolicy, onClickListener = {
                    ActivityUtils.openUrl(
                        activity,
                        "https://github.com/577fkj/StatusBarLyric/blob/main/EUAL.md"
                    )
                }))
                TextSummaryArrow(TextSummaryV(textId = R.string.Source, onClickListener = {
                    ActivityUtils.openUrl(
                        activity,
                        "https://github.com/577fkj/StatusBarLyric"
                    )
                }))
                TextSummaryArrow(TextSummaryV(textId = R.string.Donate, onClickListener = {
                    ActivityUtils.openUrl(
                        activity,
                        "https://fkj2005.gitee.io/merger/"
                    )
                }))
                if (ActivityOwnSP.ownSPConfig.getAd()) {
                    CustomView(
                        AdmobManager.getBannerAd(
                            activity,
                            BannerAdConfig("ca-app-pub-9730534578915916/8650086997")
                        )
                    )
                }
                Text()
            }

            register("close", getString(R.string.About)) {
                TextWithSwitch(TextV(text = "AD"), SwitchV("Ad", true))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityOwnSP.activity = this
        if (!checkLSPosed()) isLoad = false
        super.onCreate(savedInstanceState)
        if (isLoad && !isRegister) {
            registerReceiver(AppReceiver(), IntentFilter().apply {
                addAction("App_Server")
            })
            isRegister = true
            ActivityUtils.getNotice(activity)
            Crashes.setListener(CrashesFilter())

            if (BuildConfig.DEBUG) {
                ActivityOwnSP.ownSPConfig.setDebug(true)
            }
            AppCenter.start(
                application, Utils.appCenterKey,
                Analytics::class.java, Crashes::class.java
            )
            if (intent.getBooleanExtra("close", false)) {
                showFragment("close")
            }
            Timer().schedule(UpdateConfigTask(), 0, 1000)
            val tips = "Tips1"
            val preferences = activity.getSharedPreferences(tips, MODE_PRIVATE)
            if (!preferences.getBoolean(tips, false)) {
                MIUIDialog(activity) {
                    setTitle(R.string.Tips)
                    setMessage(R.string.FirstTip)
                    setRButton(R.string.Ok) {
                        preferences.edit().putBoolean(tips, true).apply();
                        dismiss()
                    }
                    setLButton(R.string.Cancel) {
                        dismiss()
                        exitProcess(0)
                    }
                }.show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && resultCode == RESULT_OK) {
            when (requestCode) {
                BackupUtils.CREATE_DOCUMENT_CODE -> {
                    BackupUtils.handleCreateDocument(activity, data.data)
                }
                BackupUtils.OPEN_DOCUMENT_CODE -> {
                    BackupUtils.handleReadDocument(activity, data.data)
                }
                OPEN_FONT_FILE -> {
                    data.data?.let {
                        Log.e("Lyric_Server", data.data?.toString().toString())
                        activity.sendBroadcast(
                            Intent().apply {
                                action = "Lyric_Server"
                                putExtra("Lyric_Type", "copy_font")
                                putExtra("Font_Path", FileUtils(activity).getFilePathByUri(it))
                            }
                        )
                    }
                }
            }
        }
    }

    inner class UpdateConfigTask : TimerTask() {
        override fun run() {
            if (updateConfig) {
                application.sendBroadcast(Intent().apply {
                    action = "Lyric_Server"
                    putExtra("Lyric_Type", "update_config")
                })
                updateConfig = false
            }
        }
    }

    inner class CrashesFilter : AbstractCrashesListener() {
        override fun shouldProcess(report: ErrorReport): Boolean {
            for (name in packageName) {
                if (report.stackTrace.contains(name)) {
                    return true
                }
            }
            return false
        }

        override fun getErrorAttachments(report: ErrorReport): MutableIterable<ErrorAttachmentLog> {
            val textLog = ErrorAttachmentLog.attachmentWithText(
                "StatusBarLyric: ${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}",
                "debug.txt"
            )
            return mutableListOf(textLog)
        }

        private val packageName = arrayOf(
            "statusbar.lyric",
            "cn.fkj233"
        )
    }

    inner class AppReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                Handler(Looper.getMainLooper()).post {
                    when (intent.getStringExtra("app_Type")) {
                        "Hook" -> {
                            val message: String = if (intent.getBooleanExtra("Hook", false)) {
                                getString(R.string.HookSureSuccess)
                            } else {
                                getString(R.string.HookSureFail)
                            }
                            MIUIDialog(activity) {
                                setTitle(getString(R.string.HookSure))
                                setMessage(message)
                                setRButton(getString(R.string.Ok)) { dismiss() }
                            }.show()
                            val channelId = "Hook_Ok"
                            (applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).apply {
                                createNotificationChannel(
                                    NotificationChannel(
                                        channelId,
                                        "Hook",
                                        NotificationManager.IMPORTANCE_DEFAULT
                                    )
                                )
                                notify(0, Notification.Builder(applicationContext).let {
                                    it.setChannelId(channelId)
                                    it.setSmallIcon(R.drawable.ic_notification)
                                    it.setContentTitle(getString(R.string.AppName))
                                    it.setContentText(message)
                                    it.build()
                                })
                            }
                        }
                        "CopyFont" -> {
                            val message: String = if (intent.getBooleanExtra("CopyFont", false)) {
                                getString(R.string.CustomFontSuccess)
                            } else {
                                getString(R.string.CustomFoneFail) + "\n" + intent.getStringExtra("font_error")
                            }
                            MIUIDialog(activity) {
                                setTitle(getString(R.string.CustomFont))
                                setMessage(message)
                                setRButton(getString(R.string.Ok)) { dismiss() }
                            }.show()
                        }
                        "DeleteFont" -> {
                            val message: String = if (intent.getBooleanExtra("DeleteFont", false)) {
                                getString(R.string.DeleteFontSuccess)
                            } else {
                                getString(R.string.DeleteFoneFail)
                            }
                            MIUIDialog(activity) {
                                setTitle(getString(R.string.DeleteFont))
                                setMessage(message)
                                setRButton(getString(R.string.Ok)) { dismiss() }
                            }.show()
                        }
                    }
                }

            } catch (_: Throwable) {
            }
        }
    }

    private fun checkLSPosed(): Boolean {
        return try {
            Utils.getSP(this, "Lyric_Config")?.let { setSP(it) }
            true
        } catch (e: Throwable) {
            MIUIDialog(this) {
                setTitle(R.string.Tips)
                setMessage(R.string.NotSupport)
                setRButton(R.string.ReStart) {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    exitProcess(0)
                }
                setCancelable(false)
            }.show()
            false
        }
    }
}
