package com.example.vali.pubgithub.di.module

import com.example.vali.pubgithub.data.GitHubRepository
import com.example.vali.pubgithub.ui.activity.RepoListViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
class PresenterModule {

    @Provides
    fun provideLoginPresenter(firebaseAuth: FirebaseAuth): LoginPresenter {
        return LoginPresenter(firebaseAuth)
    }

    @Provides
    fun provideRepoListViewModelFactory(repository: GitHubRepository): RepoListViewModelFactory {
        return RepoListViewModelFactory(repository)
    }
}