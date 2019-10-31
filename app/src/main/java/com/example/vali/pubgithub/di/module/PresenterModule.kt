package com.example.vali.pubgithub.di.module

import com.example.vali.pubgithub.data.GitHubRepository
import com.example.vali.pubgithub.ui.presenter.LoginPresenter
import com.example.vali.pubgithub.ui.presenter.RepoListPresenter
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
class PresenterModule {

    @Provides
    fun provideRepoListPresenter(repository: GitHubRepository): RepoListPresenter {
        return RepoListPresenter(repository)
    }

    @Provides
    fun provideLoginPresenter(firebaseAuth: FirebaseAuth): LoginPresenter {
        return LoginPresenter(firebaseAuth)
    }

}