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
        checkApi()
        TextSSw(textId = R.string.masterSwitch, key = "masterSwitch")
        Line()
        TextSA(textId = R.string.HookPage, onClickListener = { showPage(HookPage::class.java) })
        TextSA(textId = R.string.LyricPage, onClickListener = { showPage(LyricPage::class.java) })
        TextSA(textId = R.string.IconPage, onClickListener = { showPage(IconPage::class.java) })
//        TextSA(textId = R.string.CustomizeIconPage, onClickListener = { showPage(CustomizeIconPage::class.java) })
        Line()
        TextSA(textId = R.string.ExtendPage, onClickListener = { showPage(ExtendPage::class.java) })
    }

    private fun checkApi() {
        ActivityTools.checkInstalled("cn.lyric.getter").isNotNull {
            val value = it.versionName.split(".")
            val apiVersion = value[value.lastIndex].toLong()
            if (apiVersion != BuildConfig.API_VERSION) {
                TextSA(textId = R.string.NoSupportedVersionLyricGetter, tipsId = R.string.clickToInstall, onClickListener = {
                    ActivityTools.openUrl("https://github.com/xiaowine/Lyric-Getter/")
                })
            }
        }.isNot {
            TextSA(textId = R.string.NoLyricGetter, tipsId = R.string.clickToInstall, onClickListener = {
                ActivityTools.openUrl("https://github.com/xiaowine/Lyric-Getter/")
            })
        }
    }
}