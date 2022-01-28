package statusbar.lyric.activity

import android.os.Bundle
import cn.fkj233.ui.miui.MIUIActivity
import cn.fkj233.ui.miui.data.Item
import cn.fkj233.ui.miui.view.BaseView
import cn.fkj233.ui.miui.view.SeekBarV
import cn.fkj233.ui.miui.view.SwitchV
import cn.fkj233.ui.miui.view.TextV
import statusbar.lyric.R

class NewSettingsActivity: MIUIActivity() {
    private val main = "Main"
    private val test = "Test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSP(this.getPreferences(0))
        showFragment(getItems(main), getString(R.string.AppName))
    }

    private fun getItems(string: String): ArrayList<Item> {
        return when (string) {
            test -> arrayListOf<Item>().apply {
                add(Item(arrayListOf<BaseView>().apply {
                    add(TextV("test6", onClickListener = {}))
                }))
                add(Item(arrayListOf<BaseView>().apply {
                    add(TextV("test7"))
                    add(SwitchV("test7"))
                }))
                add(Item(arrayListOf<BaseView>().apply {
                    add(TextV("test8"))
                    add(SeekBarV("test8", 0, 100, defaultProgress = 0))
                }))
            }
            else -> arrayListOf<Item>().apply {
                add(Item(arrayListOf<BaseView>().apply {
                    add(TextV("test1", onClickListener = {
                        showFragment(getItems(test), "123")
                    }))
                }))
                add(Item(arrayListOf<BaseView>().apply {
                    add(TextV("test2"))
                    add(SwitchV("test2"))
                }))
                add(Item(arrayListOf<BaseView>().apply {
                    add(TextV("test3"))
                    add(SeekBarV("test3", 0, 100, defaultProgress = 0))
                }))
            }
        }
    }
}