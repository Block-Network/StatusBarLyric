package cn.fkj233.ui.activity.data

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class DataBinding : Parcelable {

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeList(bindingData)
    }

    private var bindingData = arrayListOf<BindingData>()

    fun get(defValue: Any, recvCallbacks: (View, Int, Any) -> Unit): BindingData {
        val binding = Binding(recvCallbacks)
        return BindingData(defValue, binding, binding.getSend(), recvCallbacks).also { bindingData.add(it) }
    }

    fun initAll() {
        for (binding in bindingData) {
            binding.bindingSend.send(binding.defValue)
        }
    }

    data class BindingData (
        val defValue: Any,
        val binding: Binding,
        val bindingSend: Binding.Send,
        val recvCallbacks: (View, Int, Any) -> Unit
        )

    inner class Binding(val recvCallbacks: (View, Int, Any) -> Unit): Parcelable {
        var data: ArrayList<Recv> = arrayListOf()

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(p0: Parcel?, p1: Int) {
            p0?.writeList(data)
        }

        inner class Send: Parcelable {
            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(p0: Parcel?, p1: Int) {
                p0?.writeList(data)
            }

            fun send(any: Any) {
                for (recv in data) {
                    recv.recv(any)
                }
            }
        }

        fun add(recv: Recv) {
            data.add(recv)
        }

        fun getSend(): Send {
            return Send()
        }

        fun getRecv(flags: Int): Recv {
            return Recv(flags).also { add(it) }
        }

        inner class Recv(private val flags: Int): Parcelable {
            lateinit var mView: View

            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(p0: Parcel?, p1: Int) {
                p0?.writeValue(mView)
            }

            fun setView(view: View) {
                mView = view
            }

            fun recv(any: Any) {
                recvCallbacks(mView, flags, any)
            }
        }
    }
}