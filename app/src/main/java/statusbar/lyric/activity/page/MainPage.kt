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
        TextSSw(textId = R.string.master_switch, key = "masterSwitch", onClickListener = { masterSwitchBinding.send(it) })
        Line(dataBindingRecv = masterSwitchBinding.getRecv(1))
        TextSA(textId = R.string.hook_page, onClickListener = { showPage(HookPage::class.java) }, dataBindingRecv = masterSwitchBinding.getRecv(1))
        TextSA(textId = R.string.lyric_page, onClickListener = { showPage(LyricPage::class.java) }, dataBindingRecv = masterSwitchBinding.getRecv(1))
        TextSA(textId = R.string.icon_page, onClickListener = { showPage(IconPage::class.java) }, dataBindingRecv = masterSwitchBinding.getRecv(1))
        Line(dataBindingRecv = masterSwitchBinding.getRecv(1))
        TextSA(textId = R.string.extend_page, onClickListener = { showPage(ExtendPage::class.java) }, dataBindingRecv = masterSwitchBinding.getRecv(1))
        TextSA(textId = R.string.system_special_page, onClickListener = { showPage(SystemSpecialPage::class.java) }, dataBindingRecv = masterSwitchBinding.getRecv(1))
        TitleText(textId = R.string.tips1)
    }

    private fun checkApi() {
        val openLyricGetter = { ActivityTools.openUrl("https://github.com/xiaowine/Lyric-Getter/") }
        ActivityTools.checkInstalled("cn.lyric.getter").isNotNull {
            val getterVersion = it.metaData.getInt("Getter_Version")
            if (getterVersion != BuildConfig.API_VERSION) {
                TextSA(text = "${getString(R.string.no_supported_version_lyric_getter)}\nLyric Getter Api Version:${getterVersion}", tipsId = R.string.click_to_install, onClickListener = {
                    openLyricGetter()
                })
            }
        }.isNot {
            TextSA(textId = R.string.no_lyric_getter, tipsId = R.string.click_to_install, onClickListener = {
                openLyricGetter()
            })
        }
    }
}