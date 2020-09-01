package com.example.vali.pubgithub

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vali.pubgithub.data.GitHubRepository
import com.example.vali.pubgithub.data.entity.Owner
import com.example.vali.pubgithub.data.entity.RepoEntity
import com.example.vali.pubgithub.data.remote.RemoteDataSource
import com.example.vali.pubgithub.data.remote.RemoteDataSourceInterface
import com.example.vali.pubgithub.data.remote.api.GitHubApiService
import com.example.vali.pubgithub.ui.repoList.RepoListViewModel
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class RepoListViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var apiService: GitHubApiService

    private lateinit var remoteDataSource: RemoteDataSourceInterface
    private lateinit var githubRepository: GitHubRepository
    private lateinit var repoListViewModel: RepoListViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        remoteDataSource = RemoteDataSource(apiService)
        githubRepository = GitHubRepository(remoteDataSource)
        repoListViewModel = RepoListViewModel(githubRepository)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun showDataFromApi() {

        val repo = RepoEntity(1, 0, 1, "name", "fullname", "url", Owner("log", "avatar"), 1, "lang")
        Mockito.`when`(apiService.fetchAllRepositories(0))
            .thenReturn(Observable.just(Response.success(arrayListOf(repo))))

        repoListViewModel.getAllRepos(0, false)

        assert(repoListViewModel.repos.value?.get(0)?.fullName == repo.fullName)
    }

}