package com.example.vali.pubgithub.ui.chooseLanguage

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.ProgrammingLanguage
import kotlinx.android.synthetic.main.fragment_language_list.view.*

class LanguageFilterDialog: DialogFragment(),
    LanguageFilterAdapter.LanguageSelectedListener{

    private var listener: OnFragmentInteractionListener? = null
    private val languageAdapter: LanguageFilterAdapter by lazy {LanguageFilterAdapter(this)}
    private val languageLayoutManager: RecyclerView.LayoutManager by lazy { LinearLayoutManager(
        this.activity,
        RecyclerView.VERTICAL,
        false
    ) }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

    }

    override fun onResume() {
        super.onResume()
        val args = arguments
        args?.getParcelableArrayList<ProgrammingLanguage>(getString(R.string.language_list_text))?.let {languageList ->
            languageAdapter.setDataSource(languageList)
        }
        arguments?.clear()

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_language_list, null, false)

        view.recyclerLanguageFilter.apply {
            layoutManager = languageLayoutManager
            adapter = languageAdapter
        }

        builder.setView(view)

        return builder.create()
    }

    override fun languageSelected(language: ProgrammingLanguage, position: Int) {
        listener?.languageSelected(language, position)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener?.onCloseDialog()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onCloseDialog()
        fun languageSelected(language: ProgrammingLanguage, position: Int)
    }
}
