package com.example.vali.pubgithub.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

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

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(this.id)
        dest.writeValue(this.page)
        dest.writeValue(this.totalPages)
        dest.writeString(this.name)
        dest.writeString(this.fullName)
        dest.writeString(this.htmlUrl)
        dest.writeParcelable(this.owner, flags)
        dest.writeValue(this.starsCount)
        dest.writeString(this.language)
    }

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable<Owner>(Owner::class.java.classLoader),
        parcel.readLong(),
        parcel.readString()
    )

    fun isLastPage(): Boolean {
        return page?.let { p ->
            totalPages?.let { tp ->
                p >= tp
            } ?: run {
                true
            }
        } ?: run {
            true
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<RepoEntity> = object : Parcelable.Creator<RepoEntity> {
            override fun createFromParcel(source: Parcel): RepoEntity = RepoEntity(source)

            override fun newArray(size: Int): Array<RepoEntity?> = arrayOfNulls(size)
        }
    }
}
