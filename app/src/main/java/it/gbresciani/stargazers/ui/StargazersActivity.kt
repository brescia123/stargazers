package it.gbresciani.stargazers.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView
import com.jakewharton.rxbinding.view.RxView
import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.Observable
import it.gbresciani.stargazers.R
import it.gbresciani.stargazers.StargazersApp
import it.gbresciani.stargazers.interactors.SearchInteractor
import it.gbresciani.stargazers.interactors.StargazersViewState
import it.gbresciani.stargazers.interactors.StargazersViewState.*
import kotlinx.android.synthetic.main.activity_stargazers.*

class StargazersActivity : Activity(), StargazersView {
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var stargazersAdapter: StargazersAdapter

    private var title = ""
    private var presenter: StargazersPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stargazers)
        title = getString(R.string.app_name)
        initStargazersRecyclerView()
        initAppBar()
        presenter = StargazersPresenter(this, SearchInteractor(StargazersApp.getStargazersService()))
    }

    private fun initStargazersRecyclerView() {
        stargazersRecyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        stargazersRecyclerView.layoutManager = layoutManager
        stargazersAdapter = StargazersAdapter()
        stargazersRecyclerView.adapter = stargazersAdapter
    }

    override fun searchIntent(): Observable<Pair<String, String>> = RxJavaInterop.toV2Observable(RxView.clicks(searchFAB).map { true })
            .map { ownerEditText.text.toString() to repoEditText.text.toString() }
            .doOnNext { (user, repo) ->
                if (user.isEmpty()) showMissingTermSnackbar(R.string.msg_insert_username)
                else if (repo.isEmpty()) showMissingTermSnackbar(R.string.msg_insert_repo)
            }
            .filter { (user, repo) -> user.isNotEmpty() && repo.isNotEmpty() }
            .doOnNext { (user, repo) -> title = user + "/" + repo }
            .doOnNext { closeKeyboard() }
            .map { it }

    override fun loadNextPageIntent(): Observable<Boolean> = RxJavaInterop.toV2Observable(RxRecyclerView.scrollStateChanges(stargazersRecyclerView))
            .filter { it == RecyclerView.SCROLL_STATE_IDLE }
            .filter { layoutManager.findLastVisibleItemPosition() == stargazersAdapter.itemCount - 1 }
            .map { true }


    override fun render(viewState: StargazersViewState): Boolean {
        Log.d("render", viewState.toString())
        return when (viewState) {
            is FirstRun -> showFirstRun()
            is Loading -> showLoading()
            is EmptyResults -> showEmptyResults()
            is Results -> showResults(viewState)
            is Error -> showError(viewState)
            is NextPageIsLoading -> showNextPageIsLoading()
            is NextPageHasError -> showNextPageHasError(viewState)
        }
    }

    private fun showFirstRun(): Boolean {
        stargazersRecyclerView.visibility = View.INVISIBLE
        firstLayout.visibility = View.VISIBLE
        emptyLayout.visibility = View.GONE
        return true
    }

    private fun showLoading(): Boolean = true

    private fun showEmptyResults(): Boolean {
        stargazersRecyclerView.visibility = View.INVISIBLE
        firstLayout.visibility = View.GONE
        emptyLayout.visibility = View.VISIBLE
        return true
    }

    fun showResults(viewState: Results): Boolean {
        stargazersRecyclerView.visibility = View.VISIBLE
        firstLayout.visibility = View.GONE
        emptyLayout.visibility = View.GONE
        stargazersAdapter.showLoadingFooter(false)
        stargazersAdapter.setStargazers(viewState.stargazers)
        return true
    }

    fun showError(viewState: Error): Boolean {
        Snackbar.make(mainLayout, viewState.throwable.message ?: "Error", Snackbar.LENGTH_SHORT).show()
        Log.e("Activity", viewState.throwable.message, viewState.throwable)
        return true
    }

    private fun showNextPageIsLoading(): Boolean {
        stargazersAdapter.showLoadingFooter(true)
        return true
    }

    private fun showNextPageHasError(viewState: NextPageHasError): Boolean {
        stargazersAdapter.showLoadingFooter(false)
        Snackbar.make(mainLayout, viewState.throwable.message ?: "Error", Snackbar.LENGTH_SHORT).show()
        return true
    }

    private fun showMissingTermSnackbar(msg: Int) {
        Snackbar.make(stargazersRecyclerView, msg, Snackbar.LENGTH_SHORT).show()
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun initAppBar() {
        // Enable appbar expansion on click when collapsed
        appBarLayout.setOnClickListener { appBarLayout.setExpanded(true, true) }
        // Listener that hides the collapsingToolbarLayout title when expanded
        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            internal var isShow = false
            internal var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.title = title
                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = " "
                    isShow = false
                }
            }
        })
    }


}
