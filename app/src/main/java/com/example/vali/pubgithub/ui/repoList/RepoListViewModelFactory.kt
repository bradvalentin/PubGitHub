package com.example.vali.pubgithub.ui.repoList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vali.pubgithub.data.GitHubRepository
import javax.inject.Inject

class RepoListViewModelFactory @Inject constructor(private val repository: GitHubRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RepoListViewModel::class.java!!)) {
            RepoListViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}