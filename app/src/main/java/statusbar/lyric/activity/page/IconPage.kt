@file:Suppress("DEPRECATION")

package statusbar.lyric.activity.page

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.widget.LinearLayout
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.RoundCornerImageView
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.dialog.MIUIDialog
import cn.fkj233.ui.dialog.NewDialog
import org.json.JSONObject
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.utils.ActivityOwnSP
import statusbar.lyric.utils.ActivityUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.Utils.b64Decode
import statusbar.lyric.utils.Utils.b64Encode
import statusbar.lyric.utils.Utils.copyToClipboard

@SuppressLint("NonConstantResourceId")
@BMPage("icon", titleId = R.string.IconSettings)
class IconPage : BasePage() {
    class Data(val key: String = "", val value: String = "")

    private val iconConfig = ActivityOwnSP.ownSPConfig
    private var iconList = iconConfig.gerIconList().toList()
    private val iconDataBinding by lazy {
        GetDataBinding({ Data() }) { view, i, any ->
            val data = any as Data
            if (data.key.isNotEmpty()) {
                if (iconList[i] == data.key) ((view as LinearLayout).getChildAt(0) as RoundCornerImageView).background = BitmapDrawable(Utils.stringToBitmap(if (data.value == "") iconConfig.getIcon(data.key, true) else data.value)).also { it.setTint(getColor(R.color.customIconColor)) }
            }
        }
    }

    override fun onCreate() {
        TitleText(textId = R.string.MakeIconTitle1)
        TitleText(textId = R.string.MakeIconTitle2, colorInt = Color.RED)
        for (icon in iconList) {
            Author(BitmapDrawable(Utils.stringToBitmap(iconConfig.getIcon(icon, true))).also { it.setTint(getColor(R.color.customIconColor)) }, icon, round = 0f, onClickListener = {
                NewDialog(activity) {
                    setTitle(R.string.PlaceSelect)
                    Button(getString(R.string.Edit)) {
                        MIUIDialog(activity) {
                            setTitle(icon)
                            setEditText(iconConfig.getIcon(icon, true), "")
                            setRButton(R.string.Ok) {
                                if (getEditText().isNotEmpty()) {
                                    try {
                                        val value = getEditText().replace(" ", "").replace("\n", "")
                                        iconConfig.setIcon(icon, value)
                                        iconDataBinding.send(Data(icon, value))
                                        dismiss()
                                        return@setRButton
                                    } catch (_: Throwable) {
                                    }
                                }
                                ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                                iconConfig.setIcon(icon, iconConfig.getDefaultIcon(icon, true))
                                iconDataBinding.send(Data(icon))
                                ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                                dismiss()
                            }
                            setLButton(R.string.Cancel) { dismiss() }
                        }.show()
                        dismiss()
                    }
                    Button(getString(R.string.Copy)) {
                        MIUIDialog(activity) {
                            setTitle(getString(R.string.Copy))
                            setEditText("sblicon://" + iconConfig.getIconJson(icon).b64Encode(), "")
                            setRButton(getString(R.string.Ok)) {
                                getEditText().copyToClipboard(activity)
                                dismiss()
                            }
                            setLButton(R.string.Cancel) {
                                dismiss()
                            }
                        }.show()
                        dismiss()
                    }
                    if (icon !in iconConfig.getDefaultIconList()) {
                        Button("Delete") {
                            iconConfig.removeIcon(icon)
                            reload()
                            dismiss()
                        }
                    }
                    Button(getString(R.string.Cancel), cancelStyle = true) {
                        dismiss()
                    }
                }.show()
            }, dataBindingRecv = iconDataBinding.getRecv(iconList.indexOf(icon)))
        }
        TextSummaryArrow(TextSummaryV(textId = R.string.MakeIcon, onClickListener = {
            val componentName = ComponentName("com.byyoung.setting", "com.byyoung.setting.MediaFile.activitys.ImageBase64Activity")
            try {
                activity.startActivity(Intent().setClassName("com.byyoung.setting", "utils.ShortcutsActivity").apply {
                    putExtra("PackageName", componentName.packageName)
                    putExtra("PackageClass", componentName.className)
                })
            } catch (_: Exception) {
                ActivityUtils.showToastOnLooper(activity, getString(R.string.MakeIconError))
            }
        }))
        TextSummaryArrow(TextSummaryV("${getString(R.string.Add)} / ${getString(R.string.Import)} / ${getString(R.string.Export)}", onClickListener = {
            NewDialog(activity) {
                setTitle("${getString(R.string.Add)} / ${getString(R.string.Import)} / ${getString(R.string.Export)}")
                Button(getString(R.string.Add)) {
                    MIUIDialog(activity) {
                        setTitle(getString(R.string.SetPackageName))
                        setEditText("", BuildConfig.APPLICATION_ID)
                        setRButton(R.string.Ok) {
                            val packageName = getEditText()
                            if (packageName.isNotEmpty()) {
                                MIUIDialog(activity) {
                                    setTitle("Set icon base64")
                                    setEditText("", "")
                                    setRButton(R.string.Ok) {
                                        val icon = getEditText()
                                        if (icon.isNotEmpty()) {
                                            try {
                                                iconConfig.setIcon(packageName, icon)
                                                reload()
                                            } catch (_: Throwable) {
                                            }
                                        } else {
                                            ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                                        }
                                        dismiss()
                                    }
                                    setLButton(R.string.Cancel) { dismiss() }
                                }.show()
                                dismiss()
                            } else {
                                ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                    dismiss()
                }
                Button(getString(R.string.Import)) {
                    MIUIDialog(activity) {
                        setTitle(getString(R.string.Import))
                        setEditText("", "")
                        setRButton(getString(R.string.Ok)) {
                            if (getEditText().isNotEmpty()) {
                                try {
                                    val value = getEditText().replace(" ", "").replace("\n", "")
                                    if (!value.lowercase().startsWith("sblicon://")) {
                                        JSONObject(value.substring(10).b64Decode()).also {
                                            for (key in it.keys()) {
                                                iconConfig.setIcon(key, it.getString(key))
                                            }
                                        }
                                        reload()
                                    } else {
                                        ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                                    }
                                } catch (e: Throwable) {
                                    ActivityUtils.showToastOnLooper(activity, "Import error: $e")
                                }
                            } else {
                                ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) {
                            dismiss()
                        }
                    }.show()
                    dismiss()
                }
                Button(getString(R.string.Export)) {
                    MIUIDialog(activity) {
                        setTitle(getString(R.string.Export))
                        setEditText("sblicon://${iconConfig.getIconJson().b64Encode()}", "")
                        setRButton(getString(R.string.Ok)) {
                            getEditText().copyToClipboard(activity)
                            dismiss()
                        }
                        setLButton(R.string.Cancel) {
                            dismiss()
                        }
                    }.show()
                    dismiss()
                }
                Button(getString(R.string.Cancel), cancelStyle = true) {
                    dismiss()
                }
            }.show()
        }))
        Text()
    }

    private fun reload() {
        itemList.clear()
        iconList = iconConfig.gerIconList().toList()
        activity.onBackPressed()
        showFragment("icon")
    }
}