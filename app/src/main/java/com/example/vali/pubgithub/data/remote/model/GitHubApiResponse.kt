package com.example.vali.pubgithub.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.example.vali.pubgithub.data.entity.RepoEntity
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@Parcelize
class GitHubApiResponse(
    @SerializedName("total_count")
    var totalCount: Long?,
    var items: ArrayList<RepoEntity>?
): Parcelable
