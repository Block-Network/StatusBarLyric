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

package cn.fkj233.ui.activity.view


import android.content.Context
import android.view.View
import android.widget.Switch
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.OwnSP
import cn.fkj233.ui.activity.data.DataBinding

class SwitchV(val key: String, private val defValue: Boolean = false, private val dataBindingRecv: DataBinding.Binding.Recv? = null, private val dataBindingSend: DataBinding.Binding.Send? = null, private val customOnCheckedChangeListener: ((Boolean) -> Unit)? = null): BaseView() {

    override fun getType(): BaseView = this

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return Switch(context).also {
            dataBindingRecv?.setView(it)
            it.background = null
            it.setThumbResource(R.drawable.switch_thumb)
            it.setTrackResource(R.drawable.switch_track)
            if (!OwnSP.ownSP.all.containsKey(key)) {
                OwnSP.ownSP.edit().run {
                    putBoolean(key, defValue)
                    apply()
                }
            }
            it.isChecked = OwnSP.ownSP.getBoolean(key, defValue)
            it.setOnCheckedChangeListener { _, b ->
                dataBindingSend?.let { send ->
                    send.send(b)
                }
                callBacks?.let { it1 -> it1() }
                customOnCheckedChangeListener?.let { it(b) }
                OwnSP.ownSP.edit().run {
                    putBoolean(key, b)
                    apply()
                }
            }
        }
    }
}