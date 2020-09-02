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

    val repos: LiveData<List<RepoEntity>> = MutableLiveData<List<RepoEntity>>()
    private fun setRepos(r: List<RepoEntity>?) {
        (repos as MutableLiveData).value = r
    }

    val reposLoadError: LiveData<String> = MutableLiveData<String>()
    private fun setReposLoadError(err: String) {
        (reposLoadError as MutableLiveData).value = err
    }

    val loading: LiveData<Boolean> = MutableLiveData<Boolean>()
    private fun setLoading(load: Boolean) {
        (loading as MutableLiveData).value = load
    }

    val clearRepos: LiveData<Boolean> = MutableLiveData<Boolean>()
    private fun setClearRepos(clear: Boolean) {
        (clearRepos as MutableLiveData).value = clear
    }

    val emptyRepos: LiveData<Boolean> = MutableLiveData<Boolean>()
    private fun setEmptyRepos(empty: Boolean) {
        (emptyRepos as MutableLiveData).value = empty
    }

    private var disposeBag: CompositeDisposable = CompositeDisposable()

    fun getAllRepos(since: Long, withLoading: Boolean) {
        if (withLoading) {
            setLoading(true)
        }
        disposeBag.add(
            githubRepository.getAllRepos(since)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(
                    { repoList ->
                        if (since == 0L) {
                            setEmptyRepos(repoList.body()?.size ?: 0 == 0)
                            setClearRepos(true)
                        }

                        setRepos(repoList.body())

                        setLoading(false)

                    },
                    { tr ->
                        tr.localizedMessage?.let { it ->
                            setReposLoadError(it)
                        }

                        setLoading(false)

                    }

                )
        )
    }

    fun getSearchRepos(query: String, page: Long) {
        setLoading(true)
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
                        setEmptyRepos(repoList.body()?.items?.size ?: 0 == 0)
                        setClearRepos(true)
                    }

                    setRepos(repoList.body()?.items)

                    setLoading(false)
                },
                    { tr ->
                        tr.localizedMessage?.let { it ->
                            setReposLoadError(it)
                        }
                        setLoading(false)
                        setEmptyRepos(true)

                    })
        )

    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
    }
}
