package com.example.vali.pubgithub.di.module

import com.example.vali.pubgithub.ui.activity.LoginActivity
import com.example.vali.pubgithub.ui.activity.RepoListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeRepoListActivity(): RepoListActivity

    @ContributesAndroidInjector
    abstract fun contributeLoginActivity(): LoginActivity
}