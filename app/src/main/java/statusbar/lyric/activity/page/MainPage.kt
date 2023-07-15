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
        Line()
        TextSA(textId = R.string.ExtendPage, onClickListener = { showPage(ExtendPage::class.java) })
    }

    private fun checkApi() {
        val openLyricGetter = { ActivityTools.openUrl("https://github.com/xiaowine/Lyric-Getter/") }
        ActivityTools.checkInstalled("cn.lyric.getter").isNotNull {
            if (it.metaData.getInt("Getter_Version") != BuildConfig.API_VERSION) {
                TextSA(textId = R.string.NoSupportedVersionLyricGetter, tipsId = R.string.clickToInstall, onClickListener = {
                    openLyricGetter()
                })
            }
        }.isNot {
            TextSA(textId = R.string.NoLyricGetter, tipsId = R.string.clickToInstall, onClickListener = {
                openLyricGetter()
            })
        }
    }
}