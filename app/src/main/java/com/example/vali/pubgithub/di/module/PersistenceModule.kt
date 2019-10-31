package com.example.vali.pubgithub.di.module

import com.example.vali.pubgithub.data.remote.RemoteDataSource
import com.example.vali.pubgithub.data.remote.RemoteDataSourceInterface
import com.example.vali.pubgithub.data.remote.api.GitHubApiService
import dagger.Module
import dagger.Provides

@Module
class PersistenceModule {

    @Provides
    fun provideRemoteDataSource(gitHubApiService: GitHubApiService): RemoteDataSourceInterface =
        RemoteDataSource(gitHubApiService)

}