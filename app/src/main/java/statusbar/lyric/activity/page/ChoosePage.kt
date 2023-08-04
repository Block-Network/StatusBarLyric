package statusbar.lyric.activity.page

import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.R
import statusbar.lyric.tools.ActivityTools.dataList
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTestTools.showView

@BMPage
class ChoosePage : BasePage() {
    override fun onCreate() {
        TitleText(textId = R.string.choose_page_tips)
        Line()
        dataList.forEach { data ->
            TextSA(text = "${data.textViewClassName} ${data.textViewID}", tips = "${data.parentClassName} ${data.parentID}", onClickListener = {
                activity.showView(data)
                MIUIDialog(activity) {
                    setTitle(activity.getString(R.string.select_hook))
                    setRButton(activity.getString(R.string.ok)) {
                        config.textViewClassName = data.textViewClassName
                        config.textViewID = data.textViewID
                        config.parentClassName = data.parentClassName
                        config.parentID = data.parentID
                        config.index = data.index
                    }
                    setLButton(activity.getString(R.string.cancel))
                    finally { dismiss() }
                }.show()

            })
            TitleText(activity.getString(R.string.a_a_a_tips).format(data.isRepeat, data.index))
            Line()
        }
    }
}