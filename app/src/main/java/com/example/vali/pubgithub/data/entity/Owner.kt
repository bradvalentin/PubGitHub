package com.example.vali.pubgithub.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Owner(
    var login: String?,
    @SerializedName("avatar_url")
    var avatarUrl: String?

) : Parcelable
