package com.example.vali.pubgithub.data.entity

import android.os.Parcel
import android.os.Parcelable

class ProgrammingLanguage (
    var name:String,
    var selected:Boolean = false
): Parcelable, Comparable<ProgrammingLanguage> {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "Unknown",
        parcel.readByte() != 0.toByte()
    )

    override fun compareTo(other: ProgrammingLanguage): Int = this.name.compareTo(other.name)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeByte(if (selected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProgrammingLanguage> {
        override fun createFromParcel(parcel: Parcel): ProgrammingLanguage = ProgrammingLanguage(parcel)
        override fun newArray(size: Int): Array<ProgrammingLanguage?> = arrayOfNulls(size)
    }

}