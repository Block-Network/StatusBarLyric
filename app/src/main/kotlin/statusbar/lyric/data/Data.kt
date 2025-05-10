/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/Block-Network/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as
 * published by Block-Network contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/Block-Network/StatusBarLyric/blob/main/LICENSE>.
 */

package statusbar.lyric.data

import android.os.Parcel
import android.os.Parcelable

class Data private constructor(parcel: Parcel) : Parcelable {
    var textViewClassName: String = ""
    var textViewId: Int = 0
    var parentViewClassName: String = ""
    var parentViewId: Int = 0
    var isRepeat: Boolean = false
    var index: Int = 0
    var textSize: Float = 0f
    var idName: String = ""
    override fun describeContents(): Int {
        return 0
    }

    constructor() : this(Parcel.obtain())
    constructor(
        textViewClassName: String,
        textViewId: Int,
        parentViewClassName: String,
        parentViewId: Int,
        isRepeat: Boolean,
        index: Int,
        size: Float,
        idName: String
    ) : this() {
        this.textViewClassName = textViewClassName
        this.textViewId = textViewId
        this.parentViewClassName = parentViewClassName
        this.parentViewId = parentViewId
        this.isRepeat = isRepeat
        this.index = index
        this.textSize = size
        this.idName = idName
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeString(textViewClassName)
            writeInt(textViewId)
            writeString(parentViewClassName)
            writeInt(parentViewId)
            writeInt(if (isRepeat) 1 else 0)
            writeInt(index)
            writeFloat(textSize)
            writeString(idName)
        }
    }

    init {
        textViewClassName = parcel.readString().toString()
        textViewId = parcel.readInt()
        parentViewClassName = parcel.readString().toString()
        parentViewId = parcel.readInt()
        isRepeat = parcel.readInt() == 1
        index = parcel.readInt()
        textSize = parcel.readFloat()
        idName = parcel.readString().toString()
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "Data(textViewClassName='$textViewClassName', textViewId=$textViewId, parentViewClassName='$parentViewClassName', parentViewId=$parentViewId, isRepeat=$isRepeat, index=$index, textSize=$textSize idName=$idName)"
    }
}