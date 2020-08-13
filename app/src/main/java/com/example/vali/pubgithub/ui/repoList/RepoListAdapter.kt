package com.example.vali.pubgithub.ui.repoList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.RepoEntity
import com.example.vali.pubgithub.utils.setSafeOnClickListener

class RepoListAdapter(val repoItemClickListener: RepoItemClickListener, val context: Context) : RecyclerView.Adapter<RepoListAdapter.ViewHolder>() {

    var repos: MutableList<RepoEntity> = ArrayList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardViewRepo: CardView = itemView.findViewById(R.id.cardViewRepo)
        var profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        var repoNameTextView: TextView = itemView.findViewById(R.id.repoNameTextView)
        var repoOwnerTextView: TextView = itemView.findViewById(R.id.repoOwnerTextView)
        var repoStarsTextView: TextView = itemView.findViewById(R.id.repoStarsTextView)
        var repoLanguageTextView: TextView = itemView.findViewById(R.id.repoLanguageTextView)


        fun bind(obj: RepoEntity?, context: Context) {

            obj?.language?.let {
                repoLanguageTextView.text = it
                repoLanguageTextView.visibility = VISIBLE
            } ?:run {
                repoLanguageTextView.visibility = GONE
            }

            obj?.starsCount?.let {
                repoStarsTextView.text = it.toString()
                repoStarsTextView.visibility = VISIBLE
            } ?:run {
                repoStarsTextView.visibility = GONE
            }

            repoOwnerTextView.text = obj?.owner?.login ?: context.getString(R.string.no_username)
            repoNameTextView.text = obj?.fullName ?: context.getString(R.string.no_repo_name)

            val ownerImage = obj?.owner?.avatarUrl
            Glide.with(itemView.context)
                .load(ownerImage)
                .circleCrop()
                .placeholder(R.drawable.baseline_account_box_white_48)
                .into(profileImageView)
        }
    }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.repository_list_item, parent, false)

        return ViewHolder(
            v
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = repos[position]

        holder.cardViewRepo.setSafeOnClickListener {
            repoItemClickListener.itemPressed(obj)
        }

        holder.bind(obj, context)
    }

    override fun getItemId(index: Int): Long {
        return getItemId(index)
    }

    override fun getItemCount(): Int {
        return repos.size
    }

    interface RepoItemClickListener {
        fun itemPressed(repoEntity: RepoEntity)
    }
}

