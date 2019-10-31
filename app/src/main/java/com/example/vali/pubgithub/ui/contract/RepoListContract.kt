package com.example.vali.pubgithub.ui.contract

import com.example.vali.pubgithub.data.entity.RepoEntity
import com.example.vali.pubgithub.ui.presenter.BasePresenter
import com.example.vali.pubgithub.ui.presenter.BaseView

class RepoListContract  {

    interface View : BaseView {
        fun showRepos(repos: ArrayList<RepoEntity>?)
        fun clearRepoList()

        fun showEmptyRepoList()
        fun hideEmptyRepoList()

        fun showLoading()
        fun hideLoading()
    }

    interface Presenter : BasePresenter<View> {
        fun getAllRepos(since: Long)
        fun getSearchRepos(query: String, page: Long)

    }

}