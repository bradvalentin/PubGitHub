package com.example.vali.pubgithub.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.Owner
import com.example.vali.pubgithub.data.entity.ProgrammingLanguage
import com.example.vali.pubgithub.data.entity.RepoEntity
import com.example.vali.pubgithub.ui.adapter.RepoListAdapter
import com.example.vali.pubgithub.ui.fragment.LanguageFilterDialog
import com.example.vali.pubgithub.utils.AnimationUtils.hideViewAnimated
import com.example.vali.pubgithub.utils.AnimationUtils.showViewAnimated
import com.example.vali.pubgithub.utils.SharedPreferencesHelper
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_repo_list.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class RepoListActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    RepoListAdapter.RepoItemClickListener,
    LanguageFilterDialog.OnFragmentInteractionListener {

    private var resetList= false
    private var isFirstTime = true
    private lateinit var reposLayoutManager: RecyclerView.LayoutManager
    private lateinit var reposAdapter: RepoListAdapter
    private lateinit var searchQuery: String
    private var searchFilter: String = ""
    private lateinit var fm: FragmentManager
    private lateinit var allProgrammingLanguage: ArrayList<ProgrammingLanguage>
    private var owner: Owner? = null
    private lateinit var skeleton: Skeleton;

    lateinit var repoListViewModel: RepoListViewModel
    @Inject
    lateinit var viewModeFactory: RepoListViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_repo_list)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        fm = supportFragmentManager

        val json = SharedPreferencesHelper.getOwner(this)
        owner =  Gson().fromJson(json, Owner::class.java)

        reposLayoutManager = GridLayoutManager(
            this,
            1,
            RecyclerView.VERTICAL,
            false
        )

        repositoriesRecycler.layoutManager = reposLayoutManager

        reposAdapter = RepoListAdapter(this, this)

        repositoriesRecycler.adapter = reposAdapter
        skeleton = repositoriesRecycler.applySkeleton(R.layout.repository_list_item, 12)

        skeleton.showSkeleton();

        initScrollListener()

        allProgrammingLanguage = initSearchDialogData()

        fab.setOnClickListener {
            showLanguageFilterDialog(allProgrammingLanguage)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        val headerView = nav_view.getHeaderView(0)
        headerView.usernameTextView.text = owner?.login
        val ownerImage = owner?.avatarUrl
        Glide.with(this)
            .load(ownerImage)
            .circleCrop()
            .placeholder(R.drawable.baseline_account_box_white_48)
            .into(headerView.imageViewProfile)

        repoListViewModel = ViewModelProvider(this, this.viewModeFactory).get(RepoListViewModel::class.java)
        repoListViewModel.getAllRepos(0, withLoading = false)
        observeViewModel()
    }

    private fun onDataLoaded() {
        skeleton.showOriginal()
    }

    private fun openRepoDetailsInBrowser(urls: String?) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        startActivity(intents)
    }

    private fun observeViewModel() {
        repoListViewModel.repos.observe(this, Observer { repos ->
            repos?.let {
              if(it.isNotEmpty()) {
                  showRepos(it)
              }
            }

        })

        repoListViewModel.emptyRepos.observe(this, Observer { isEmpty ->
            isEmpty?.let {
                if(it) showEmptyRepoList() else hideEmptyRepoList()
            }
        })

        repoListViewModel.clearRepos.observe(this, Observer { clear ->
            clear?.let {
                clearRepoList()
            }
        })

        repoListViewModel.reposLoadError.observe(this, Observer { isError ->
            isError?.let {
                onError(it)
            }
        })

        repoListViewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                if(it) showLoading() else hideLoading()
            }
        })
    }

    private fun showRepos(repos: List<RepoEntity>?) {
        repos?.let {
            reposAdapter.setDataSource(it)
            if(isFirstTime) {
                onDataLoaded()
                isFirstTime = false
            }
        }
    }

    private fun clearRepoList() {
        reposAdapter.clearDataSource()
    }

    private fun showEmptyRepoList() {
        showViewAnimated(noRepositoryFoundTextView, 1.0f, 200, View.VISIBLE)
        hideViewAnimated(repositoriesRecycler, 0f, 200, View.GONE)
    }

    private fun hideEmptyRepoList() {
        hideViewAnimated(noRepositoryFoundTextView, 0f, 200, View.GONE)
        showViewAnimated(repositoriesRecycler, 1.0f, 200, View.VISIBLE)
    }

    private fun showLoading() {
        showViewAnimated(progressBar, 1.0f, 200, View.VISIBLE)
    }

    private fun hideLoading() {
        hideViewAnimated(progressBar, 0f, 200, View.GONE)
    }

    override fun itemPressed(repoEntity: RepoEntity) {
        openRepoDetailsInBrowser(repoEntity.htmlUrl)
    }

    private fun onError(errorMessage: String) {
        Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_LONG).show()
    }

    private fun initScrollListener() {
        repositoriesRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val gridLayoutManager = recyclerView.layoutManager as GridLayoutManager?
                val repoSize =  reposAdapter.repos.size
                if (gridLayoutManager != null && repoSize > 0 && gridLayoutManager.findLastCompletelyVisibleItemPosition() == repoSize - 1) {
                    loadMore()
                }
            }
        })

    }

    private fun loadMore() {
        val last = reposAdapter.repos.last()
        last.totalPages?.let {
            last.page?.let { p->
                if(!last.isLastPage()){
                    repoListViewModel.getSearchRepos("$searchQuery$searchFilter", p.plus(1))
                }
            }

        } ?:run {
            repoListViewModel.getAllRepos(last.id.plus(1), withLoading = true)
        }
    }

    private fun dismissFragment(tag: String) {

        if(!::fm.isInitialized) {
            fm = supportFragmentManager
        }

        val prev = fm.findFragmentByTag(tag) as? LanguageFilterDialog?
        prev?.dismissAllowingStateLoss()

    }

    private fun showLanguageFilterDialog(languageFilterList: ArrayList<ProgrammingLanguage>) {

        if(!::fm.isInitialized) {
            fm = supportFragmentManager
        }

        val ft = fm.beginTransaction()

        val prev = fm.findFragmentByTag("languageFilterDialog")
        if (prev != null) {
            ft.remove(prev)

        }
        val dialogFragment = LanguageFilterDialog()

        val bundle = Bundle()
        bundle.putParcelableArrayList("languageList", languageFilterList)

        dialogFragment.arguments = bundle

        dialogFragment.show(fm, "languageFilterDialog")
        ft.commitAllowingStateLoss()
    }

    override fun onCloseDialog() {
        dismissFragment("languageFilterDialog")
    }

    override fun languageSelected(language: ProgrammingLanguage, position: Int) {

        if(language.selected) {
            allProgrammingLanguage.first().selected = false
            allProgrammingLanguage.sort()
            searchFilter = ""
        } else {
            allProgrammingLanguage.first().selected = false
            allProgrammingLanguage[position] = allProgrammingLanguage.first()
            allProgrammingLanguage.removeAt(0)
            allProgrammingLanguage.sort()
            language.selected = true
            allProgrammingLanguage.add(0, language)
            searchFilter = "+language:${language.name}"
        }

        if(::searchQuery.isInitialized && searchQuery.isNotEmpty()){
            repoListViewModel.getSearchRepos("$searchQuery$searchFilter", 0)
        }

        dismissFragment("languageFilterDialog")

    }

    private fun initSearchDialogData(): ArrayList<ProgrammingLanguage> {
        val items = ArrayList<ProgrammingLanguage>()
        val languagesArray = resources.getStringArray(R.array.languages)
        languagesArray.forEach {
            items.add(ProgrammingLanguage(it))
        }
        items.sort()
        return items
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_search, menu)

        setupSearchView(menu)
        return true
    }

    private fun setupSearchView(menu: Menu) {
        val item = menu.findItem(R.id.menu_search)

        val searchView = item.actionView as SearchView
        val editText = searchView.findViewById<EditText>(R.id.search_src_text)
        editText.hint = getString(R.string.search_view_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String): Boolean {

                if(query.isNotEmpty()) {
                    searchQuery = query.toLowerCase(Locale.getDefault())
                    repoListViewModel.getSearchRepos("$searchQuery$searchFilter", 0)
                    resetList = true
                }

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if(newText.isEmpty() && resetList){
                    resetList = false
                    searchQuery = newText
                    repoListViewModel.getAllRepos(0, withLoading = true)
                }
                return true
            }

        })

    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
            else -> super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                SharedPreferencesHelper.deleteOwner(this)
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
