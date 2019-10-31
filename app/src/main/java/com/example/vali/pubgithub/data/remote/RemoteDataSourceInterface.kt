package com.example.vali.pubgithub.data.remote

import com.example.vali.pubgithub.data.entity.RepoEntity
import com.example.vali.pubgithub.data.remote.model.GitHubApiResponse
import io.reactivex.Observable
import retrofit2.Response
import java.util.ArrayList

interface RemoteDataSourceInterface {

    fun getSearchRepos(query: String, page: Long):  Observable<Response<GitHubApiResponse>>
    fun getAllRepos(since: Long):  Observable<Response<ArrayList<RepoEntity>>>

}