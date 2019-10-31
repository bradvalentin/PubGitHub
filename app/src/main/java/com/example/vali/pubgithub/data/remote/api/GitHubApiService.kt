package com.example.vali.pubgithub.data.remote.api

import com.example.vali.pubgithub.data.entity.RepoEntity
import com.example.vali.pubgithub.data.remote.model.GitHubApiResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.ArrayList

interface GitHubApiService {
    @GET("search/repositories")
    fun fetchSearchRepositories(
        @Query("q") q: String,
        @Query("sort") sort: String,
        @Query("order") order: String,
        @Query("page") page: Long?
    ): Observable<Response<GitHubApiResponse>>

    @GET("repositories")
    fun fetchAllRepositories(
        @Query("since") since: Long
    ): Observable<Response<ArrayList<RepoEntity>>>
}
