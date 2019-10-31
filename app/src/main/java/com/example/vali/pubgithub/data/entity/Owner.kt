package com.example.vali.pubgithub.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Owner(
    var login: String?,
    @SerializedName("avatar_url")
    var avatarUrl: String?

) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.login)
        dest.writeString(this.avatarUrl)
    }


    constructor(parcel: Parcel): this(
        parcel.readString(),
        parcel.readString()
    )

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<Owner> = object : Parcelable.Creator<Owner> {
            override fun createFromParcel(source: Parcel): Owner = Owner(source)

            override fun newArray(size: Int): Array<Owner?> = arrayOfNulls(size)
        }
    }
}
