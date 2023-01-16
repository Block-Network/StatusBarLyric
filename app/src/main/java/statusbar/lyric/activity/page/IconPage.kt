package statusbar.lyric.activity.page

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.widget.LinearLayout
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.RoundCornerImageView
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.R
import statusbar.lyric.activity.SettingsActivity
import statusbar.lyric.utils.ActivityOwnSP
import statusbar.lyric.utils.ActivityUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.Utils.indexOfArr

@SuppressLint("NonConstantResourceId")
@BMPage("icon", titleId = R.string.IconSettings)
class IconPage : BasePage() {
    override fun onCreate() {
        TitleText(textId = R.string.MakeIconTitle)
        val iconConfig = ActivityOwnSP.ownSPConfig
        val iconList = iconConfig.gerIconList()
        val iconDataBinding = GetDataBinding({ "" }) { view, i, any ->
            if ((any as String).isNotEmpty()) {
                val iconData = any.split("|%|")
                if (iconList[i] == iconData[0]) ((view as LinearLayout).getChildAt(0) as RoundCornerImageView).background = BitmapDrawable(Utils.stringToBitmap(if (iconData[1] == "") iconConfig.getIcon(iconData[0]) else iconData[1])).also { it.setTint(getColor(R.color.customIconColor)) }
            }
        }
        for (icon in iconList) {
            Author(BitmapDrawable(Utils.stringToBitmap(iconConfig.getIcon(icon))).also { it.setTint(getColor(R.color.customIconColor)) }, icon, round = 0f, onClickListener = {
                MIUIDialog(activity) {
                    setTitle(icon)
                    setEditText(iconConfig.getIcon(icon), "")
                    setRButton(R.string.Ok) {
                        if (getEditText().isNotEmpty()) {
                            try {
                                val value = getEditText().replace(" ", "").replace("\n", "")
                                iconConfig.setIcon(icon, value)
                                iconDataBinding.bindingSend.send("$icon|%|${value}")
                                SettingsActivity.updateConfig = true
                                dismiss()
                                return@setRButton
                            } catch (_: Throwable) {
                            }
                        }
                        ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                        iconConfig.setIcon(icon, iconConfig.getDefaultIcon(icon))
                        iconDataBinding.bindingSend.send("$icon|%|")
                        ActivityUtils.showToastOnLooper(activity, getString(R.string.InputError))
                        SettingsActivity.updateConfig = true
                        dismiss()
                    }
                    setLButton(R.string.Cancel) { dismiss() }
                }.show()
            }, dataBindingRecv = iconDataBinding.binding.getRecv(iconList.indexOfArr(icon)))
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
        Text()
    }
}