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
    var textSize: Float = 0f
    var idName: String = ""
    var viewTree: String = ""
    override fun describeContents(): Int {
        return 0
    }

    constructor() : this(Parcel.obtain())
    constructor(
        textViewClassName: String,
        textViewId: Int,
        textSize: Float,
        idName: String,
        viewTree: String = ""
    ) : this() {
        this.textViewClassName = textViewClassName
        this.textViewId = textViewId
        this.textSize = textSize
        this.idName = idName
        this.viewTree = viewTree
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeString(textViewClassName)
            writeInt(textViewId)
            writeFloat(textSize)
            writeString(idName)
            writeString(viewTree)
        }
    }

    init {
        textViewClassName = parcel.readString().toString()
        textViewId = parcel.readInt()
        textSize = parcel.readFloat()
        idName = parcel.readString().toString()
        viewTree = parcel.readString().toString()
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
        return "Data(textViewClassName='$textViewClassName', textViewId=$textViewId, textSize=$textSize, idName=$idName)"
    }
}