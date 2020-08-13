package com.example.vali.pubgithub.ui.chooseLanguage

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.ProgrammingLanguage
import com.example.vali.pubgithub.utils.setSafeOnClickListener

class LanguageFilterAdapter(val languageSelectedListener: LanguageSelectedListener) : RecyclerView.Adapter<LanguageFilterAdapter.ViewHolder>() {

    private lateinit var languageList: ArrayList<ProgrammingLanguage>

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var languageName: TextView = itemView.findViewById(R.id.languageName)
        var selectedLanguageImageView: ImageView = itemView.findViewById(R.id.selectedLanguageImageView)
        var languageContainer: ConstraintLayout = itemView.findViewById(R.id.languageContainer)

        fun bind(obj: ProgrammingLanguage) {
            languageName.text = obj.name
            if (obj.selected){
                selectedLanguageImageView.visibility = VISIBLE
            }else {
                selectedLanguageImageView.visibility = GONE
            }
        }
    }

    fun setDataSource(dataSource: ArrayList<ProgrammingLanguage>) {
        languageList = dataSource
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.language_list_item, parent, false)

        return ViewHolder(
            v
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = languageList[position]

        holder.languageContainer.setSafeOnClickListener {
            languageSelectedListener.languageSelected(obj, position)
        }

        holder.bind(obj)
    }

    override fun getItemId(index: Int): Long {
        return getItemId(index)
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    interface LanguageSelectedListener {
        fun languageSelected(language: ProgrammingLanguage, position: Int)
    }
}

