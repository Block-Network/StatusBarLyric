package cn.fkj233.ui.activity.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator


class FragmentData : Parcelable {
    var dataBinding: DataBinding? = null
    var callBacks: (() -> Unit)? = null
    var mDataItem: List<Item> = ArrayList()

    val CREATOR: Creator<FragmentData> = object : Creator<FragmentData> {
        @Suppress("UNCHECKED_CAST")
        override fun createFromParcel(source: Parcel): FragmentData {
            val fragmentData = FragmentData()
            fragmentData.dataBinding = source.readValue(ClassLoader.getSystemClassLoader()) as DataBinding
            fragmentData.callBacks = source.readValue(ClassLoader.getSystemClassLoader()) as () -> Unit
            source.readList(fragmentData.mDataItem, ClassLoader.getSystemClassLoader())
            return fragmentData
        }

        override fun newArray(size: Int): Array<FragmentData?> {
            return arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeValue(dataBinding)
        p0?.writeValue(callBacks)
        p0?.writeValue(mDataItem)
    }
}