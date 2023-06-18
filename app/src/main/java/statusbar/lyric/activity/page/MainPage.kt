package statusbar.lyric.activity.page

import android.annotation.SuppressLint
import cn.fkj233.ui.activity.annotation.BMMainPage
import cn.fkj233.ui.activity.data.BasePage
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.Tools.isNot
import statusbar.lyric.tools.Tools.isNotNull


@SuppressLint("NonConstantResourceId")
@BMMainPage
class MainPage : BasePage() {
    override fun onCreate() {
        ActivityTools.checkInstalled("cn.lyric.getter").isNotNull {
            val apiVersion = it.versionName.split(".")[0].toInt()
            if (apiVersion != BuildConfig.apiVersion) {
                Text(textId = R.string.NoSupportedVersionLyricGetter)
            }
        }.isNot {
            TextS(textId = R.string.NoLyricGetter, tipsId = R.string.clickToInstall, onClickListener = {
                ActivityTools.openUrl("https://github.com/Block-Network/StatusBarLyric/realease")
            })
        }
        TextSSw(textId = R.string.masterSwitch, key = "masterSwitch")
        TextSA(textId = R.string.TestMode, onClickListener = {
            showPage(TestModePage::class.java)
        })
    }
}