package it.gbresciani.stargazers.ui

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.gbresciani.stargazers.interactors.*
import it.gbresciani.stargazers.interactors.StargazersViewState.FirstRun
import kotlin.reflect.KFunction2


typealias ViewRender = (ViewState) -> Boolean

class StargazersPresenter(val searchInteractor: SearchInteractor,
                          val viewScheduler: Scheduler = AndroidSchedulers.mainThread(),
                          val jobScheduler: Scheduler = Schedulers.io()) : MVIPresenter<StargazersView, StargazersViewState>() {

    var currentState: StargazersViewState = FirstRun


    override val viewStateRender: KFunction2<StargazersView, StargazersViewState, Boolean> = StargazersView::render

    override fun bindIntents() : Observable<StargazersViewState>{

        val searchObservable: Observable<StargazersViewState> = intent(StargazersView::searchIntent)
                .doOnNext { Log.d("StargazersPresenter", "Intent: search") }
                .filter { currentState !is StargazersViewState.Loading }
                .observeOn(viewScheduler)
                .flatMap { searchInteractor.search(it).subscribeOn(jobScheduler) }

        val loadNextPageObservable = intent(StargazersView::loadNextPageIntent)
                .doOnNext { Log.d("StargazersPresenter", "Intent: loadNextPage") }
                .filter { currentState !is StargazersViewState.NextPageIsLoading }
                .filter { searchInteractor.thereAreMorePages() }
                .flatMap { searchInteractor.loadMoreResults().subscribeOn(jobScheduler) }
                .map { reducer(currentState, it) }

        return Observable.merge(searchObservable, loadNextPageObservable)
                .startWith(currentState)
                .observeOn(viewScheduler)
                .doOnNext { Log.d("StargazersPresenter", it.toString()) }
                .doOnNext { currentState = it }
    }


}

fun reducer(previous: StargazersViewState, new: PartialStargazersViewState): StargazersViewState =
        stargazersViewStateReduce(previous, new)


