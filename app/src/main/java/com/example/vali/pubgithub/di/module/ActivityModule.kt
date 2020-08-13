package com.example.vali.pubgithub.di.module

import com.example.vali.pubgithub.ui.login.LoginActivity
import com.example.vali.pubgithub.ui.repoList.RepoListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeRepoListActivity(): RepoListActivity

    @ContributesAndroidInjector
    abstract fun contributeLoginActivity(): LoginActivity
}