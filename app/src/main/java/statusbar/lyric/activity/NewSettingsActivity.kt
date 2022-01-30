package statusbar.lyric.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import cn.fkj233.ui.activity.view.*
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.data.Item
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.utils.ActivityOwnSP
import statusbar.lyric.utils.ActivityUtils
import statusbar.lyric.utils.Utils
import kotlin.system.exitProcess

enum class DataItem {
    Custom, Author
}

class NewSettingsActivity: MIUIActivity() {
    private val activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (!checkLSPosed()) return
        ActivityOwnSP.activity = this
        setSP(this.getPreferences(0))
    }

    private fun checkLSPosed(): Boolean {
        return try {
            Utils.getSP(this, "Lyric_Config")
            true
        } catch (e: Throwable) {
            statusbar.lyric.view.miuiview.MIUIDialog(this).apply {
                setTitle(R.string.Tips)
                setMessage(R.string.NotSupport)
                setButton(R.string.ReStart) {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    exitProcess(0)
                }
                setCancelable(false)
                show()
            }
            false
        }
    }

    override fun menuName(): String {
        return getString(R.string.AppName)
    }

    override fun menuItems(): ArrayList<Item> {
        return arrayListOf<Item>().apply {

        }
    }

    override fun mainName(): String {
        return getString(R.string.AppName)
    }

    override fun mainItems(): ArrayList<Item> {
        return arrayListOf<Item>().apply {
            add(Item(arrayListOf<BaseView>().apply {
                add(TextSummaryV(textId = R.string.UseInfo, onClick = {
                    MIUIDialog(activity).apply {
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
                        show()
                    }
                }))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextSummaryV(textId = R.string.WarnExplanation, onClick = {
                    MIUIDialog(activity).apply {
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
                        show()
                    }
                }))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextV(resId = R.string.BaseSetting))
                add(LineV())
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextWithSwitchV(TextV(resId = R.string.AllSwitch), SwitchV("LService")))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextWithSwitchV(TextV(resId = R.string.LyricIcon), SwitchV("I", true)))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextSummaryV(textId = R.string.Custom, onClick = { showFragment(getItems(DataItem.Custom), getString(R.string.Custom)) }))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextV(resId = R.string.AdvancedSettings))
                add(LineV())
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextWithSwitchV(TextV(resId = R.string.AbScreen), SwitchV("AntiBurn")))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextWithSwitchV(TextV(resId = R.string.UseSystemReverseColor), SwitchV("UseSystemReverseColor", true)))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextWithSwitchV(TextV(resId = R.string.SongPauseCloseLyrics), SwitchV("LAutoOff")))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextWithSwitchV(TextV(resId = R.string.UnlockShow), SwitchV("LockScreenOff")))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextWithSwitchV(TextV(resId = R.string.AutoHideNotiIcon), SwitchV("HNoticeIcon")))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextWithSwitchV(TextV(resId = R.string.HideNetWork), SwitchV("HNetSpeed")))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextWithSwitchV(TextV(resId = R.string.AutoHideCarrierName), SwitchV("HCuk")))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextV(resId = R.string.Other))
                add(LineV())
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextSummaryV(textId = R.string.CustomHook, onClick = {
                    MIUIDialog(activity).apply {
                        setTitle(R.string.HookSetTips)
                        setEditText(
                            ActivityOwnSP.ownSPConfig.getHook(),
                            getString(R.string.InputCustomHook)
                        )
                        setRButton(R.string.Ok) {
                            ActivityOwnSP.ownSPConfig.setHook(getEditText())
                            ActivityUtils.showToastOnLooper(
                                activity,
                                String.format(
                                    "%s %s%s",
                                    getString(R.string.HookSetTips),
                                    if (ActivityOwnSP.ownSPConfig.getHook() == "") getString(R.string.Default) else ActivityOwnSP.ownSPConfig.getHook(),
                                    getString(R.string.RestartSystemUI)
                                )
                            )
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                        show()
                    }
                }))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextWithSwitchV(TextV(resId = R.string.DebugMode), SwitchV("Debug")))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextSummaryV(textId = R.string.Test, onClick = {
                    MIUIDialog(activity).apply {
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
                        show()
                    }
                }))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextV(resId = R.string.About))
                add(LineV())
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextSummaryV("${getString(R.string.CheckUpdate)} (${BuildConfig.VERSION_NAME})", onClick = {
                    ActivityUtils.showToastOnLooper(
                        activity,
                        getString(R.string.StartCheckUpdate)
                    )
                    ActivityUtils.checkUpdate(activity)
                }))
            }))
            add(Item(arrayListOf<BaseView>().apply {
                add(TextSummaryV(textId = R.string.AboutModule, onClick = { showFragment(getItems(DataItem.Author), getString(R.string.AboutModule)) }))
            }))
        }
    }

    private fun getItems(dataItem: DataItem): ArrayList<Item> {
        return when (dataItem) {
            DataItem.Custom -> arrayListOf<Item>().apply {
                add(Item(arrayListOf<BaseView>().apply {
                    add(TextSummaryV(textId = R.string.LyricColor, onClick = {
                        MIUIDialog(activity).apply {
                            setTitle(R.string.LyricColor)
                            setMessage(R.string.LyricColorTips)
                            setEditText(ActivityOwnSP.ownSPConfig.getLyricColor(), "#FFFFFF")
                            setRButton(R.string.Ok) {
                                if (getEditText() == "") {
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
                            show()
                        }
                    }))
                }))
                add(Item(arrayListOf<BaseView>().apply {
                    add(TextV(resId = R.string.LyricSize))
                    add(SeekBarWithTextV("LSize", 0, 100))
                }))
                val dataBinding = DataBinding { view, data ->
                    when (view) {
                        is LinearLayout -> view.visibility = if ((data as Int) == -1) View.VISIBLE else View.GONE
                        is TextView -> view.visibility = if ((data as Int) == -1) View.VISIBLE else View.GONE
                    }
                }
                add(Item(arrayListOf<BaseView>().apply {
                    add(TextV(resId = R.string.LyricWidth))
                    add(SeekBarWithTextV("LWidth", -1, 100, dataBinding = dataBinding, send = true))
                }))
                add(Item(arrayListOf<BaseView>().apply {
                    add(TextV(resId = R.string.LyricAutoMaxWidth, dataBinding = dataBinding))
                    add(SeekBarWithTextV("LMaxWidth", -1, 100, dataBinding = dataBinding))
                }))
            }
            else -> arrayListOf()
        }
    }
}