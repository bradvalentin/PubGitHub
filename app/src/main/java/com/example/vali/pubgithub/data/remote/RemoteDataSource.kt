package com.example.vali.pubgithub.data.remote

import com.example.vali.pubgithub.data.entity.RepoEntity
import com.example.vali.pubgithub.data.remote.api.GitHubApiService
import com.example.vali.pubgithub.data.remote.model.GitHubApiResponse
import io.reactivex.Observable
import retrofit2.Response
import java.util.ArrayList
import javax.inject.Inject

const val QUERY_SORT = "stars"
const val QUERY_ORDER = "desc"

class RemoteDataSource(): RemoteDataSourceInterface {

    lateinit var gitHubApiService: GitHubApiService

    @Inject
    constructor(
        gitHubApiService: GitHubApiService
    ): this() {
        this.gitHubApiService = gitHubApiService
    }

    override fun getSearchRepos(query: String, page: Long):  Observable<Response<GitHubApiResponse>> {
        return gitHubApiService.fetchSearchRepositories(query, QUERY_SORT, QUERY_ORDER, page)
    }

    override fun getAllRepos(since: Long): Observable<Response<ArrayList<RepoEntity>>> {
        return gitHubApiService.fetchAllRepositories(since)
    }
}