@file:Suppress("DEPRECATION")

package cn.fkj233.xposed.statusbarlyric.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.fkj233.xposed.statusbarlyric.view.adapter.ItemAdapter
import cn.fkj233.xposed.statusbarlyric.view.data.DataHelper
import cn.fkj233.xposed.statusbarlyric.view.data.DataItem
import cn.fkj233.xposed.statusbarlyric.view.data.Item

class NewSettingsFragment : Fragment() {

    private lateinit var adapter: ItemAdapter
    private val itemList = arrayListOf<Item>()
    private lateinit var dataItem: DataItem

    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        itemList.addAll(DataHelper.getItems(dataItem))
        val recyclerView = RecyclerView(context)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        adapter = ItemAdapter(itemList)
        recyclerView.adapter = adapter
        return recyclerView
    }

    fun setDataItem(mDataItem: DataItem): NewSettingsFragment {
        dataItem = mDataItem
        return this
    }

}