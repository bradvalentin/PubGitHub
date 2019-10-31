package com.example.vali.pubgithub.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import kotlinx.android.synthetic.main.activity_repo_list.*
import kotlinx.android.synthetic.main.app_bar_main.*

import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vali.pubgithub.data.entity.ProgrammingLanguage
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.Owner
import com.example.vali.pubgithub.data.entity.RepoEntity
import com.example.vali.pubgithub.ui.adapter.RepoListAdapter
import com.example.vali.pubgithub.ui.contract.RepoListContract
import com.example.vali.pubgithub.ui.fragment.LanguageFilterDialog
import com.example.vali.pubgithub.ui.presenter.RepoListPresenter
import com.example.vali.pubgithub.utils.AnimationUtils.hideViewAnimated
import com.example.vali.pubgithub.utils.AnimationUtils.showViewAnimated
import com.ferfalk.simplesearchview.utils.DimensUtils
import com.example.vali.pubgithub.utils.Constants.EXTRA_REVEAL_CENTER_PADDING
import com.example.vali.pubgithub.utils.SharedPreferencesHelper
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.content_main.*
import javax.inject.Inject
import com.ferfalk.simplesearchview.SimpleSearchView
import com.ferfalk.simplesearchview.SimpleSearchView.SearchViewListener
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.nav_header_main.view.*

class RepoListActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    RepoListContract.View,
    RepoListAdapter.RepoItemClickListener,
    LanguageFilterDialog.OnFragmentInteractionListener {

    @Inject
    lateinit var repoListPresenter: RepoListPresenter

    private lateinit var reposLayoutManager: RecyclerView.LayoutManager
    private lateinit var reposAdapter: RepoListAdapter
    private lateinit var searchQuery: String
    private var searchFilter: String = ""
    private lateinit var fm: FragmentManager
    private lateinit var allProgrammingLanguage: ArrayList<ProgrammingLanguage>
    private var owner: Owner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_repo_list)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        fm = supportFragmentManager

        val json = SharedPreferencesHelper.getOwner(this)
        owner =  Gson().fromJson(json, Owner::class.java)

        repoListPresenter.setView(this)
        repoListPresenter.getAllRepos(0)

        reposLayoutManager = GridLayoutManager(
            this,
            1,
            RecyclerView.VERTICAL,
            false
        )

        repositoriesRecycler.layoutManager = reposLayoutManager

        reposAdapter = RepoListAdapter(this, this)

        repositoriesRecycler.adapter = reposAdapter

        initScrollListener()

        searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchQuery = query
                repoListPresenter.getSearchRepos("$searchQuery$searchFilter", 0)

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                if(::searchQuery.isInitialized && searchQuery.isNotEmpty()) {
                    repoListPresenter.getAllRepos( 0)
                    searchQuery = ""
                }
                return false
            }
        })
        searchView.setOnSearchViewListener(object : SearchViewListener {
            override fun onSearchViewShownAnimation() {
            }

            override fun onSearchViewClosedAnimation() {
            }

            override fun onSearchViewShown() {
                if(::searchQuery.isInitialized) {
                    searchView.searchEditText.setText(searchQuery)
                }
            }

            override fun onSearchViewClosed() {
            }
        })

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
    }

    private fun openRepoDetailsInBrowser(urls: String?) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        startActivity(intents)
    }

    override fun showRepos(repos: ArrayList<RepoEntity>?) {
        repos?.let {
            reposAdapter.setDataSource(it)
        }
    }

    override fun clearRepoList() {
        reposAdapter.clearDataSource()
    }

    override fun showEmptyRepoList() {
        showViewAnimated(noRepositoryFoundTextView, 1.0f, 200, View.VISIBLE)
        hideViewAnimated(repositoriesRecycler, 0f, 200, View.GONE)
    }

    override fun hideEmptyRepoList() {
        hideViewAnimated(noRepositoryFoundTextView, 0f, 200, View.GONE)
        showViewAnimated(repositoriesRecycler, 1.0f, 200, View.VISIBLE)
    }

    override fun showLoading() {
        showViewAnimated(progressBar, 1.0f, 200, View.VISIBLE)
    }

    override fun hideLoading() {
        hideViewAnimated(progressBar, 0f, 200, View.GONE)
    }

    override fun itemPressed(repoEntity: RepoEntity) {
        openRepoDetailsInBrowser(repoEntity.htmlUrl)
    }

    override fun onError(errorMessage: String) {
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
                    repoListPresenter.getSearchRepos("$searchQuery$searchFilter", p.plus(1))
                }
            }

        } ?:run {
            repoListPresenter.getAllRepos(last.id.plus(1))
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
            repoListPresenter.getSearchRepos("$searchQuery$searchFilter", 0)
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
        inflater.inflate(R.menu.search_menu, menu)

        setupSearchView(menu)
        return true
    }

    private fun setupSearchView(menu: Menu) {
        val item = menu.findItem(R.id.action_search)
        searchView.setMenuItem(item)

        val revealCenter = searchView.revealAnimationCenter
        revealCenter.x -= DimensUtils.convertDpToPx(EXTRA_REVEAL_CENTER_PADDING, this)
    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
            searchView.onBackPressed() -> return
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

    override fun onDestroy() {
        repoListPresenter.dispose()
        repoListPresenter.detachView()
        super.onDestroy()
    }
}
