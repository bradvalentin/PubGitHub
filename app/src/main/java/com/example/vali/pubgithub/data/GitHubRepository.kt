package com.example.vali.pubgithub.data

import com.example.vali.pubgithub.data.entity.RepoEntity
import com.example.vali.pubgithub.data.remote.RemoteDataSourceInterface
import com.example.vali.pubgithub.data.remote.model.GitHubApiResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.util.ArrayList
import javax.inject.Inject

class GitHubRepository(): RemoteDataSourceInterface {

    private lateinit var remoteDataSource: RemoteDataSourceInterface

    @Inject
    constructor(
        remoteDataSource: RemoteDataSourceInterface
    ): this() {
        this.remoteDataSource = remoteDataSource
    }

    override fun getSearchRepos(query: String, page: Long): Observable<Response<GitHubApiResponse>> {
        return remoteDataSource.getSearchRepos(query, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getAllRepos(since: Long): Observable<Response<ArrayList<RepoEntity>>> {
        return remoteDataSource.getAllRepos(since)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}