package statusbar.lyric.activity.page

import android.view.View
import cn.fkj233.ui.activity.annotation.BMMainPage
import cn.fkj233.ui.activity.data.BasePage
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.Tools.isNot
import statusbar.lyric.tools.Tools.isNotNull


@BMMainPage
class MainPage : BasePage() {
    override fun onCreate() {
        checkApi()
        val masterSwitchBinding = GetDataBinding({ ActivityOwnSP.config.masterSwitch }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextSSw(textId = R.string.masterSwitch, key = "masterSwitch", onClickListener = { masterSwitchBinding.send(it) })
        Line(dataBindingRecv = masterSwitchBinding.getRecv(1))
        TextSA(textId = R.string.HookPage, onClickListener = { showPage(HookPage::class.java) }, dataBindingRecv = masterSwitchBinding.getRecv(1))
        TextSA(textId = R.string.LyricPage, onClickListener = { showPage(LyricPage::class.java) }, dataBindingRecv = masterSwitchBinding.getRecv(1))
        TextSA(textId = R.string.IconPage, onClickListener = { showPage(IconPage::class.java) }, dataBindingRecv = masterSwitchBinding.getRecv(1))
        Line(dataBindingRecv = masterSwitchBinding.getRecv(1))
        TextSA(textId = R.string.ExtendPage, onClickListener = { showPage(ExtendPage::class.java) }, dataBindingRecv = masterSwitchBinding.getRecv(1))
        TitleText(textId = R.string.Tips1)
    }

    private fun checkApi() {
        val openLyricGetter = { ActivityTools.openUrl("https://github.com/xiaowine/Lyric-Getter/") }
        ActivityTools.checkInstalled("cn.lyric.getter").isNotNull {
            val getterVersion = it.metaData.getInt("Getter_Version")
            if (getterVersion != BuildConfig.API_VERSION) {
                TextSA(text = "${getString(R.string.NoSupportedVersionLyricGetter)}\nLyric Getter Api Version:${getterVersion}", tipsId = R.string.clickToInstall, onClickListener = {
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