package com.example.vali.pubgithub.di.module

import com.example.vali.pubgithub.data.GitHubRepository
import com.example.vali.pubgithub.ui.login.LoginViewModelFactory
import com.example.vali.pubgithub.ui.repoList.RepoListViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    @Provides
    fun provideLoginViewModelFactory(firebaseAuth: FirebaseAuth): LoginViewModelFactory {
        return LoginViewModelFactory(
            firebaseAuth
        )
    }

    @Provides
    fun provideRepoListViewModelFactory(repository: GitHubRepository): RepoListViewModelFactory {
        return RepoListViewModelFactory(
            repository
        )
    }
}