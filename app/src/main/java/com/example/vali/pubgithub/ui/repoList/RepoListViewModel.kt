package com.example.vali.pubgithub.ui.repoList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.vali.pubgithub.data.GitHubRepository
import com.example.vali.pubgithub.data.entity.RepoEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class RepoListViewModel(private val githubRepository: GitHubRepository) : ViewModel() {

    private val _repos = MutableLiveData<List<RepoEntity>>()
    val repos: LiveData<List<RepoEntity>>
        get() = _repos
    private val _reposLoadError = MutableLiveData<String>()
    val reposLoadError: LiveData<String>
        get() = _reposLoadError
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading
    private val _clearRepos = MutableLiveData<Boolean>()
    val clearRepos: LiveData<Boolean>
        get() = _clearRepos
    private val _emptyRepos = MutableLiveData<Boolean>()
    val emptyRepos: LiveData<Boolean>
        get() = _emptyRepos

    private var disposeBag: CompositeDisposable = CompositeDisposable()

    fun getAllRepos(since: Long, withLoading: Boolean) {
        if (withLoading) {
            _loading.value = true
        }
        disposeBag.add(
            githubRepository.getAllRepos(since)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(
                    { repoList ->
                        if (since == 0L) {
                            _emptyRepos.value = repoList.body()?.size ?: 0 == 0
                            _clearRepos.value = true
                        }

                        _repos.value = repoList.body()

                        _loading.value = false

                    },
                    { tr ->
                        tr.localizedMessage?.let { it ->
                            _reposLoadError.value = it
                        }

                        _loading.value = false

                    }

                )
        )
    }

    fun getSearchRepos(query: String, page: Long) {
        _loading.value = true
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
                        _emptyRepos.value = repoList.body()?.items?.size ?: 0 == 0
                        _clearRepos.value = true
                    }

                    _repos.value = repoList.body()?.items

                    _loading.value = false

                },
                    { tr ->
                        tr.localizedMessage?.let { it ->
                            _reposLoadError.value = it
                        }
                        _loading.value = false
                        _emptyRepos.value = true

                    })
        )

    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
    }
}
