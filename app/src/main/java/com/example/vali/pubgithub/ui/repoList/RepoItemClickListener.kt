package com.example.vali.pubgithub.ui.repoList

import com.example.vali.pubgithub.data.entity.RepoEntity

interface RepoItemClickListener {
    fun itemPressed(repoEntity: RepoEntity)
}