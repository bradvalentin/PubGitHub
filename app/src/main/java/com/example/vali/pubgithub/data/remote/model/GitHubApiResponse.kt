package com.example.vali.pubgithub.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.example.vali.pubgithub.data.entity.RepoEntity
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class GitHubApiResponse(
    @SerializedName("total_count")
    var totalCount: Long?,
    var items: ArrayList<RepoEntity>?
): Parcelable {

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(this.totalCount)
        dest.writeTypedList(this.items)
    }

    constructor(parcel: Parcel): this(
        parcel.readLong(),
        parcel.createTypedArrayList<RepoEntity>(RepoEntity.CREATOR)
    )

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<GitHubApiResponse> = object : Parcelable.Creator<GitHubApiResponse> {
            override fun createFromParcel(source: Parcel): GitHubApiResponse = GitHubApiResponse(source)
            override fun newArray(size: Int): Array<GitHubApiResponse?> = arrayOfNulls(size)
        }
    }
}
