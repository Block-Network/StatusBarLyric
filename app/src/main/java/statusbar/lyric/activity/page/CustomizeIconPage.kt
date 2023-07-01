package statusbar.lyric.activity.page


import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage

@BMPage
class CustomizeIconPage : BasePage() {
    override fun onCreate() {
//        config.iconHashMap.forEach {
//            ImageWithText(BitmapDrawable(activity.resources, base64ToDrawable(it.value)).apply { setTint(getColor(R.color.customIconColor)) }, it.key, round = 0f, onClickListener = {
//                val key = it.key
//                NewDialog(context) {
//                    setTitle(key)
//                    setEditText(config.iconHashMap[key]!!, "", false)
//                    Button(getString(R.string.OK)) {
//                        val value = getEditText()
//                        try {
//                            if (value.isEmpty()) throw Exception()
//                            base64ToDrawable(value)
//                            config.setIcon(key, value)
//                            reload()
//                        } catch (_: Exception) {
//                            ActivityTools.showToastOnLooper(getString(R.string.InputError))
//                        }
//                    }
//                    Button(getString(R.string.Reset), cancelStyle = true) {
//                        config.resetIcon(key)
//                        reload()
//                    }
//                    Button(getString(R.string.Cancel), cancelStyle = true)
//                    Finally { dismiss() }
//                }.show()
//            })
//        }
    }

    private fun reload() {
        itemList.clear()
        activity.onBackPressed()
        showPage(CustomizeIconPage::class.java)
    }
}