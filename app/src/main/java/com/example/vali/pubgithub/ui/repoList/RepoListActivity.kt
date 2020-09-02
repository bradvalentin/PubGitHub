package com.example.vali.pubgithub.ui.repoList

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
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
import com.example.vali.pubgithub.databinding.ActivityRepoListBinding
import com.example.vali.pubgithub.ui.chooseLanguage.LanguageFilterDialog
import com.example.vali.pubgithub.ui.login.LoginActivity
import com.example.vali.pubgithub.utils.SharedPreferencesHelper
import com.example.vali.pubgithub.utils.safeLet
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_repo_list.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

const val SKELETON_ITEMS = 12
const val FIRST_GIT_PAGE = 0L
const val NEXT_GIT_PAGE = 1L
const val LANGUAGE_DIALOG_TAG = "languageFilterDialog"
const val EMPTY_STRING = ""

class RepoListActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    RepoItemClickListener,
    LanguageFilterDialog.OnFragmentInteractionListener {

    private lateinit var binding: ActivityRepoListBinding
    private var resetList= false
    private var isFirstTime = true
    private val reposLayoutManager: RecyclerView.LayoutManager by lazy { GridLayoutManager(
        this,
        1,
        RecyclerView.VERTICAL,
        false
    ) }
    private val reposAdapter: RepoListAdapter by lazy { RepoListAdapter(this) }
    private var searchFilter: String = ""
    private val fm: FragmentManager by lazy { supportFragmentManager }
    private val allProgrammingLanguage: ArrayList<ProgrammingLanguage> by lazy { initSearchDialogData() }
    private var owner: Owner? = null
    private val skeleton: Skeleton by lazy { repositoriesRecycler.applySkeleton(R.layout.repository_list_item, SKELETON_ITEMS) }
    private val repoListViewModel: RepoListViewModel by lazy { ViewModelProvider(this, this.viewModeFactory).get(RepoListViewModel::class.java) }

    @Inject
    lateinit var viewModeFactory: RepoListViewModelFactory
    private lateinit var searchQuery: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_repo_list)
        binding.lifecycleOwner = this

        binding.repoViewModel = repoListViewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val json = SharedPreferencesHelper.getOwner(this)
        owner =  Gson().fromJson(json, Owner::class.java)

        binding.repositoriesRecycler.apply {
            layoutManager = reposLayoutManager
            adapter = reposAdapter
        }

        skeleton.showSkeleton()

        initScrollListener()

        binding.fab.setOnClickListener {
            showLanguageFilterDialog(allProgrammingLanguage)
        }

        setupActionBarToggle()

        setupNavigationView()

        repoListViewModel.getAllRepos(FIRST_GIT_PAGE, withLoading = false)

        observeViewModel()
    }

    private fun setupNavigationView() {
        binding.navView.setNavigationItemSelectedListener(this)

        val headerView = binding.navView.getHeaderView(0)
        headerView.usernameTextView.text = owner?.login

        Glide.with(this)
            .load(owner?.avatarUrl)
            .circleCrop()
            .placeholder(R.drawable.baseline_account_box_white_48)
            .into(headerView.imageViewProfile)
    }

    private fun setupActionBarToggle() {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
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

    override fun itemPressed(repoEntity: RepoEntity) {
        openRepoDetailsInBrowser(repoEntity.htmlUrl)
    }

    private fun onError(errorMessage: String) {
        Toast.makeText(this, getString(R.string.some_error).plus(getString(R.string.error_text_separator)).plus(errorMessage), Toast.LENGTH_LONG).show()
    }

    private fun initScrollListener() {
        binding.repositoriesRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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
        safeLet(last.totalPages, last.page) { _, p ->
            if(!last.isLastPage()) {
                repoListViewModel.getSearchRepos("$searchQuery$searchFilter", p.plus(NEXT_GIT_PAGE))
            }

        } ?:run {
            repoListViewModel.getAllRepos(last.id.plus(NEXT_GIT_PAGE), withLoading = true)
        }
    }

    private fun dismissFragment() {
        val prev = fm.findFragmentByTag(LANGUAGE_DIALOG_TAG) as? LanguageFilterDialog?
        prev?.dismissAllowingStateLoss()
    }

    private fun showLanguageFilterDialog(languageFilterList: ArrayList<ProgrammingLanguage>) {

        val ft = fm.beginTransaction()

        fm.findFragmentByTag(LANGUAGE_DIALOG_TAG)?.let {
            ft.remove(it)
        }

        val bundle = Bundle().apply {
            putParcelableArrayList(getString(R.string.language_list_text), languageFilterList)
        }

        LanguageFilterDialog().apply {
            arguments = bundle
            show(fm, LANGUAGE_DIALOG_TAG)
        }

        ft.commitAllowingStateLoss()
    }

    override fun onCloseDialog() {
        dismissFragment()
    }

    override fun languageSelected(language: ProgrammingLanguage, position: Int) {

        if(language.selected) {
            allProgrammingLanguage.apply {
                first().selected = false
                sort()
            }
            searchFilter = EMPTY_STRING
        } else {
            allProgrammingLanguage.apply {
                first().selected = false
                this[position] = allProgrammingLanguage.first()
                removeAt(0)
                sort()
                add(0, language)
            }
            language.selected = true
            searchFilter = getString(R.string.language_querry_param).plus(language.name)
        }

        if(::searchQuery.isInitialized && searchQuery.isNotEmpty()){
            repoListViewModel.getSearchRepos("$searchQuery$searchFilter", 0)
        }

        dismissFragment()

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
        menuInflater.inflate(R.menu.main_search, menu)

        setupSearchView(menu)
        return true
    }

    private fun setupSearchView(menu: Menu) {
        val item = menu.findItem(R.id.menu_search)

        val searchView = item.actionView as SearchView
        searchView.findViewById<EditText>(R.id.search_src_text).apply {
            hint = getString(R.string.search_view_hint)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String): Boolean {

                if(query.isNotEmpty()) {
                    searchQuery = query.toLowerCase(Locale.getDefault())
                    repoListViewModel.getSearchRepos("$searchQuery$searchFilter", FIRST_GIT_PAGE)
                    resetList = true
                }

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if(newText.isEmpty() && resetList){
                    resetList = false
                    searchQuery = newText
                    repoListViewModel.getAllRepos(FIRST_GIT_PAGE, withLoading = true)
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

                val intent = Intent(this, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
