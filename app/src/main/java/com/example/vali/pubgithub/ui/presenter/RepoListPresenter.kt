package com.example.vali.pubgithub.ui.presenter

import com.example.vali.pubgithub.data.GitHubRepository
import com.example.vali.pubgithub.ui.contract.RepoListContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RepoListPresenter(): RepoListContract.Presenter {

    private var view: RepoListContract.View? = null

    private lateinit var gitHubRepo: GitHubRepository
    private var disposeBag: CompositeDisposable = CompositeDisposable()

    @Inject
    constructor(
        repository: GitHubRepository
    ): this() {
        this.gitHubRepo = repository
    }

    override fun setView(view: RepoListContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun getAllRepos(since: Long) {
        view?.showLoading()
        val getUserDisposable = gitHubRepo.getAllRepos(since)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .subscribe ({ repoList ->
                if(since == 0L) {
                    view?.clearRepoList()
                }
                if(repoList.body()?.size ?: 0 > 0) {
                    view?.hideEmptyRepoList()
                    view?.showRepos(repoList.body())

                }else {
                    view?.showEmptyRepoList()
                }
                view?.hideLoading()
            },
                { tr ->
                    view?.onError(tr.localizedMessage)
                    view?.hideLoading()
                    view?.showEmptyRepoList()

                })

        disposeBag.add(getUserDisposable)

    }

    override fun getSearchRepos(query: String, page: Long) {
        view?.showLoading()
        val getUserDisposable =  gitHubRepo.getSearchRepos(query, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .map{ list ->
                list.body()?.let{
                    it.items?.map { item ->
                        item.totalPages = it.totalCount
                        item.page = page
                    }
                }
                return@map list
            }
            .subscribe ({ repoList ->
                if(page == 0L) {
                    view?.clearRepoList()
                }
                if(repoList.body()?.items?.size ?: 0 > 0) {
                    view?.hideEmptyRepoList()
                    view?.showRepos(repoList.body()?.items)

                }else {
                    view?.showEmptyRepoList()
                }
                view?.hideLoading()
            },
                { tr ->
                    view?.onError(tr.localizedMessage)
                    view?.hideLoading()
                    view?.showEmptyRepoList()

                })

        disposeBag.add(getUserDisposable)

    }

    fun dispose() {
        if(!disposeBag.isDisposed) {
            disposeBag.dispose()
        }
    }
}