package statusbar.lyric.activity.page

import android.annotation.SuppressLint
import cn.fkj233.ui.activity.annotation.BMMainPage
import cn.fkj233.ui.activity.data.BasePage
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.ActivityTools.showToastOnLooper
import statusbar.lyric.tools.Tools.isNot
import statusbar.lyric.tools.Tools.isNotNull


@SuppressLint("NonConstantResourceId")
@BMMainPage
class MainPage : BasePage() {
    override fun onCreate() {
        checkApi()
        TextSSw(textId = R.string.masterSwitch, key = "masterSwitch")
        Line()
        TextSA(textId = R.string.HookPage, onClickListener = { if(XposedOwnSP.config.masterSwitch) showPage(HookPage::class.java) else showToastOnLooper(getString(R.string.notEnabledTips)) })
        TextSA(textId = R.string.LyricPage, onClickListener = { if(XposedOwnSP.config.masterSwitch) showPage(LyricPage::class.java) else showToastOnLooper(getString(R.string.notEnabledTips)) })
        TextSA(textId = R.string.IconPage, onClickListener = { if(XposedOwnSP.config.masterSwitch) showPage(IconPage::class.java) else showToastOnLooper(getString(R.string.notEnabledTips)) })
        Line()
        TextSA(textId = R.string.ExtendPage, onClickListener = { if(XposedOwnSP.config.masterSwitch) showPage(ExtendPage::class.java) else showToastOnLooper(getString(R.string.notEnabledTips)) })
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
