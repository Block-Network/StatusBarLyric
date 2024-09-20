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
    override fun describeContents(): Int {
        return 0
    }

    constructor() : this(Parcel.obtain())
    constructor(textViewClassName: String, textViewId: Int, parentViewClassName: String, parentViewId: Int, isRepeat: Boolean, index: Int, size: Float) : this() {
        this.textViewClassName = textViewClassName
        this.textViewId = textViewId
        this.parentViewClassName = parentViewClassName
        this.parentViewId = parentViewId
        this.isRepeat = isRepeat
        this.index = index
        this.textSize = size
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
        return "Data(textViewClassName='$textViewClassName', textViewId=$textViewId, parentViewClassName='$parentViewClassName', parentViewId=$parentViewId, isRepeat=$isRepeat, index=$index, textSize=$textSize)"
    }
}