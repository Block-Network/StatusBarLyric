package cn.fkj233.ui.activity.data

import java.io.Serializable

class Person : Serializable {
    var dataBinding: DataBinding? = null
    var callBacks: (() -> Unit)? = null
    var mDataItem: List<Item>? = null
}