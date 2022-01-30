package cn.fkj233.ui.activity.data

open class MIUIPopupData(private val name: String, private val callBacks: () -> Unit) {
    fun getName(): String = name

    fun getCallBacks(): () -> Unit = callBacks
}