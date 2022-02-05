/*
 * BlockMIUI
 * Copyright (C) 2022 fkj@fkj233.cn
 * https://github.com/577fkj/BlockMIUI
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/BlockMIUI/blob/main/LICENSE>.
 */

package cn.fkj233.ui.activity.data

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class DataBinding {

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

    data class BindingData(
        val defValue: Any,
        val binding: Binding,
        val bindingSend: Binding.Send,
        val recvCallbacks: (View, Int, Any) -> Unit
        )

    inner class Binding(val recvCallbacks: (View, Int, Any) -> Unit) {
        var data: ArrayList<Recv> = arrayListOf()

        inner class Send {
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