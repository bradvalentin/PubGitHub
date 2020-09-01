package com.example.vali.pubgithub.ui.repoList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.RepoEntity
import com.example.vali.pubgithub.databinding.RepositoryListItemBinding

class RepoListAdapter(private val repoItemClickListener: RepoItemClickListener) : RecyclerView.Adapter<RepoListAdapter.RepoViewHolder>() {

    var repos: MutableList<RepoEntity> = ArrayList()

    class RepoViewHolder(val view: RepositoryListItemBinding) : RecyclerView.ViewHolder(view.root)

    fun setDataSource(dataSource: List<RepoEntity>?) {
        dataSource?.let {ds ->
            val pos = repos.size
            val count = ds.size
            repos.addAll(ds)

            notifyItemRangeInserted(pos, count)
        }
    }

    fun clearDataSource() {
        repos.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<RepositoryListItemBinding>(inflater, R.layout.repository_list_item, parent, false)

        return RepoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {

        holder.view.repo = repos[position]
        holder.view.listener = repoItemClickListener
        holder.view.executePendingBindings()

    }

    override fun getItemId(index: Int): Long {
        return getItemId(index)
    }

    override fun getItemCount(): Int = repos.size

}

