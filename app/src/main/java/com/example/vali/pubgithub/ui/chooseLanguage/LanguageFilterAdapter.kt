package com.example.vali.pubgithub.ui.chooseLanguage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.ProgrammingLanguage
import com.example.vali.pubgithub.databinding.LanguageListItemBinding


class LanguageFilterAdapter(private val languageSelectedListener: LanguageSelectedListener) : RecyclerView.Adapter<LanguageFilterAdapter.LanguageViewHolder>() {

    private lateinit var languageList: ArrayList<ProgrammingLanguage>

    class LanguageViewHolder(val view: LanguageListItemBinding) : RecyclerView.ViewHolder(view.root)

    fun setDataSource(dataSource: ArrayList<ProgrammingLanguage>) {
        languageList = dataSource
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<LanguageListItemBinding>(inflater, R.layout.language_list_item, parent, false)

        return LanguageViewHolder(view)

    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.view.apply {
            lang = languageList[position]
            listener = languageSelectedListener
            pos = position
            executePendingBindings()
        }
    }

    override fun getItemId(index: Int): Long = getItemId(index)

    override fun getItemCount(): Int = languageList.size

    interface LanguageSelectedListener {
        fun languageSelected(language: ProgrammingLanguage, position: Int)
    }
}

