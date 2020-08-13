package com.example.vali.pubgithub.ui.repoList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.vali.pubgithub.data.GitHubRepository
import com.example.vali.pubgithub.data.entity.RepoEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class RepoListViewModel(private val githubRepository: GitHubRepository) : ViewModel() {

    val repos = MutableLiveData<List<RepoEntity>>()
    val reposLoadError = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    val clearRepos = MutableLiveData<Boolean>()
    val emptyRepos = MutableLiveData<Boolean>()

    private var disposeBag: CompositeDisposable = CompositeDisposable()

    fun getAllRepos(since: Long, withLoading: Boolean) {
        if (withLoading) {
            loading.value = true
        }
        disposeBag.add(
            githubRepository.getAllRepos(since)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(
                    { repoList ->
                        if (since == 0L) {
                            emptyRepos.value = repoList.body()?.size ?: 0 == 0
                            clearRepos.value = true
                        }

                        repos.value = repoList.body()

                        loading.value = false

                    },
                    { tr ->
                        tr.localizedMessage?.let { it ->
                            reposLoadError.value = it
                        }

                        loading.value = false

                    }

                )
        )
    }

    fun getSearchRepos(query: String, page: Long) {
        loading.value = true
        disposeBag.add(
            githubRepository.getSearchRepos(query, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .map { list ->
                    list.body()?.let {
                        it.items?.map { item ->
                            item.totalPages = it.totalCount
                            item.page = page
                        }
                    }
                    return@map list
                }
                .subscribe({ repoList ->
                    if (page == 0L) {
                        emptyRepos.value = repoList.body()?.items?.size ?: 0 == 0
                        clearRepos.value = true
                    }

                    repos.value = repoList.body()?.items

                    loading.value = false

                },
                    { tr ->
                        tr.localizedMessage?.let { it ->
                            reposLoadError.value = it
                        }
                        loading.value = false
                        emptyRepos.value = true

                    })
        )

    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
    }
}
