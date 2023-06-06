package statusbar.lyric.activity.page

import android.annotation.SuppressLint
import cn.fkj233.ui.activity.annotation.BMMainPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.activity.view.TitleTextV
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.utils.ActivityUtils
import kotlin.random.Random

@SuppressLint("NonConstantResourceId")
@BMMainPage(titleId = R.string.AppName)
class MainPage : BasePage() {
    override fun onCreate() {
        TextSummaryWithArrow(TextSummaryV(textId = R.string.ApplicableVersion, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.VerExplanation)
                setMessage(String.format(" %s [%s] %s", getString(R.string.CurrentVer), BuildConfig.VERSION_NAME, getString(R.string.VerExp)), false)
                setRButton(R.string.Done) {
                    dismiss()
                }
            }.show()
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.WarnExplanation, onClickListener = {
            MIUIDialog(activity) {
                setTitle(R.string.WarnExplanation)
                setMessage(String.format(" %s [%s] %s", getString(R.string.CurrentVer), BuildConfig.VERSION_NAME, getString(R.string.WarnExp)), false)
                setRButton(R.string.Done) {
                    dismiss()
                }
            }.show()
        }, colorId = android.R.color.holo_blue_dark))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.Manual, onClickListener = { ActivityUtils.openUrl(activity, "https://app.xiaowine.cc") }, colorId = android.R.color.holo_red_dark))
        val givenList = listOf(getString(R.string.TitleTips1), getString(R.string.TitleTips2), getString(R.string.TitleTips3), getString(R.string.TitleTips4), getString(R.string.FirstTip))
        TitleText(text = givenList[Random.nextInt(givenList.size)])
        Line()
        TitleText(textId = R.string.BaseSetting)
        TextWithSwitch(TextV(textId = R.string.AllSwitch), SwitchV("LService"))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.CustomLyric, onClickListener = { showFragment("custom") }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.CustomIcon, onClickListener = { showFragment("IconCustom") }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.AdvancedSettings, onClickListener = { showFragment("advancedSettings") }))
        Line()
        TitleTextV(textId = R.string.About)
        TextSummaryWithArrow(TextSummaryV("${getString(R.string.CheckUpdate)} (${BuildConfig.VERSION_NAME})", onClickListener = {
            ActivityUtils.showToastOnLooper(activity, getString(R.string.StartCheckUpdate))
            ActivityUtils.getUpdate(activity)
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.AboutModule, onClickListener = { showFragment("about") }))
        Text()
    }

}