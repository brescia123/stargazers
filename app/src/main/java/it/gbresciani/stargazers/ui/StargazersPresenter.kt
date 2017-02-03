package it.gbresciani.stargazers.ui

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.gbresciani.stargazers.interactors.PartialStargazersViewState
import it.gbresciani.stargazers.interactors.SearchInteractor
import it.gbresciani.stargazers.interactors.StargazersViewState
import it.gbresciani.stargazers.interactors.StargazersViewState.FirstRun
import it.gbresciani.stargazers.interactors.stargazersViewStateReduce


class StargazersPresenter(val view: StargazersView,
                          val searchInteractor: SearchInteractor,
                          val viewScheduler: Scheduler = AndroidSchedulers.mainThread(),
                          val jobScheduler: Scheduler = Schedulers.io()) : MVIPresenter<StargazersView>() {

    var currentState: StargazersViewState = FirstRun

    override fun onAttach(v: StargazersView) {
        super.onAttach(v)
        val searchObservable: Observable<StargazersViewState> = view.searchIntent()
                .doOnNext { Log.d("StargazersPresenter", "Intent: search") }
                .filter { currentState !is StargazersViewState.Loading }
                .observeOn(viewScheduler)
                .flatMap { searchInteractor.search(it).subscribeOn(jobScheduler) }

        val loadNextPageObservable = view.loadNextPageIntent()
                .doOnNext { Log.d("StargazersPresenter", "Intent: loadNextPage") }
                .filter { currentState !is StargazersViewState.NextPageIsLoading }
                .filter { searchInteractor.thereAreMorePages() }
                .flatMap { searchInteractor.loadMoreResults().subscribeOn(jobScheduler) }
                .map { reducer(currentState, it) }

        Observable.merge(searchObservable, loadNextPageObservable)
                .startWith(currentState)
                .observeOn(viewScheduler)
                .doOnNext { Log.d("StargazersPresenter", it.toString()) }
                .doOnNext { currentState = it }
                .subscribe({ view.render(it) }, { Log.e("StargazersPresenter", it.message, it) })
    }

    override fun onDetach() {
        super.onDetach()
    }
}

fun reducer(previous: StargazersViewState, new: PartialStargazersViewState): StargazersViewState =
        stargazersViewStateReduce(previous, new)


