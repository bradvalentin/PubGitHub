package com.example.vali.pubgithub.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProgrammingLanguage (
    var name:String,
    var selected:Boolean = false
): Parcelable, Comparable<ProgrammingLanguage> {

    override fun compareTo(other: ProgrammingLanguage): Int = this.name.compareTo(other.name)

}