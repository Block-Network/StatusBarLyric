package cn.fkj233.ui.activity.data

import android.view.View

class DataBinding(val recvCallbacks: (View, Any) -> Unit) {

    var data: ArrayList<Recv> = arrayListOf()

    fun send(any: Any) {
        for (recv in data) {
            recv.recv(any)
        }
    }

    fun add(recv: Recv) {
        data.add(recv)
    }

    inner class Recv(val view: View) {
        fun recv(any: Any) {
            recvCallbacks(view, any)
        }
    }
}