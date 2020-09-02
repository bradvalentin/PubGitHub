package com.example.vali.pubgithub.data.entity

import android.os.Parcelable
import com.example.vali.pubgithub.utils.safeLet
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class RepoEntity(

    var id: Long,
    var page: Long?,
    var totalPages: Long?,
    var name: String?,
    @SerializedName("full_name")
    var fullName: String?,
    @SerializedName("html_url")
    var htmlUrl: String?,
    var owner: Owner?,
    @SerializedName("stargazers_count")
    var starsCount: Long?,
    var language: String?

) : Parcelable {

    fun isLastPage(): Boolean {
        safeLet(page, totalPages) { p, tp ->
            return p >= tp
        }

        return true
    }

}
