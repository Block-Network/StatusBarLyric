package cn.fkj233.ui.activity.view

import android.content.Context
import android.view.View

abstract class BaseView: BaseProperties {

    var hasLoad = false

    abstract fun getType(): BaseView

    abstract fun create(context: Context, callBacks: (() -> Unit)?): View

}

interface BaseProperties {

    val outside: Boolean
        get() = false

}