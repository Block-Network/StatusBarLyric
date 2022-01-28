package cn.fkj233.ui.miui

import android.content.Context
import android.view.View
import cn.fkj233.ui.miui.view.BaseView

object ViewBuilder {

    fun build(context: Context, view: BaseView): View? {
        if (view.hasLoad) return null
        return view.create(context).also { view.hasLoad = true }
    }

}