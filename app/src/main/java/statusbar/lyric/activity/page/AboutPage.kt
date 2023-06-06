package statusbar.lyric.activity.page

import android.annotation.SuppressLint
import android.widget.ImageView
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.R
import statusbar.lyric.utils.ActivityUtils
import java.util.HashMap

@SuppressLint("NonConstantResourceId")
@BMPage("about", titleId = R.string.About)
class AboutPage : BasePage() {
    override fun onCreate() {
        ImageWithText(getDrawable(R.drawable.header_577fkj), "577fkj", getString(R.string.AboutTips1), onClickListener = { ActivityUtils.openUrl(activity, "https://github.com/577fkj") })
        ImageWithText(getDrawable(R.drawable.header_xiaowine), "xiaowine", getString(R.string.AboutTips2), onClickListener = { ActivityUtils.openUrl(activity, "https://github.com/xiaowine") })
        Line()
        TitleText(textId = R.string.ThkListTips)
        TextSummaryWithArrow(TextSummaryV(textId = R.string.ThkListTips, onClickListener = {
            ActivityUtils.openUrl(activity, "https://github.com/577fkj/StatusBarLyric#%E6%84%9F%E8%B0%A2%E5%90%8D%E5%8D%95%E4%B8%8D%E5%88%86%E5%85%88%E5%90%8E")
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.SponsoredList, onClickListener = {
            ActivityUtils.openUrl(activity, "https://github.com/577fkj/StatusBarLyric/blob/Dev/doc/SPONSOR.md")
        }))
        Line()
        TitleText(textId = R.string.Other)
        TextSummaryWithArrow(TextSummaryV(textId = R.string.PrivacyPolicy, onClickListener = {
            ActivityUtils.openUrl(activity, "https://github.com/577fkj/StatusBarLyric/blob/main/EUAL.md")
        }))
        TextSummaryWithArrow(TextSummaryV(textId = R.string.Source, onClickListener = {
            ActivityUtils.openUrl(activity, "https://github.com/577fkj/StatusBarLyric")
        }))
        val dict: HashMap<String, String> = hashMapOf()
        dict["Alipay"] = "Alipay"
        dict["WeChat"] = "WeChat"
        dict["Afdian"] = "Afdian"
        TextWithSpinner(TextV(textId = R.string.Donate), SpinnerV("") {
            add("Alipay") {
                MIUIDialog(activity) {
                    setTitle(R.string.Donate)
                    setMessage("Alipay")
                    addView(ImageView(activity).also { it.setImageDrawable(getDrawable(R.drawable.alipay)) })
                    setLButton(R.string.Ok) { dismiss() }
                }.show()
            }
            add("WeChat") {
                MIUIDialog(activity) {
                    setTitle(R.string.Donate)
                    setMessage("WeChat")
                    addView(ImageView(activity).also { it.setImageDrawable(getDrawable(R.drawable.wechat)) })
                    setLButton(R.string.Ok) { dismiss() }
                }.show()
            }
            add("Afdian") { ActivityUtils.openUrl(activity, "https://afdian.net/@xiao_wine") }
        })
        Text()
    }
}