package com.example.vali.pubgithub.di.component

import android.app.Application
import com.example.vali.pubgithub.AppController
import com.example.vali.pubgithub.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApiModule::class,
    PersistenceModule::class,
    ViewModelModule::class,
    ActivityModule::class,
    AndroidSupportInjectionModule::class
])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(appController: AppController)
}