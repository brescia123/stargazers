package it.gbresciani.stargazers.interactors

import it.gbresciani.stargazers.interactors.StargazersViewState.*
import it.gbresciani.stargazers.network.Stargazer

typealias Search = Pair<String, String>

sealed class StargazersViewState : ViewState {
    object FirstRun : StargazersViewState()
    data class Loading(val search: Search) : StargazersViewState()
    data class EmptyResults(val search: Search) : StargazersViewState()
    data class Results(val search: Search, val stargazers: List<Stargazer>) : StargazersViewState()
    data class Error(val search: Search, val throwable: Throwable) : StargazersViewState()
    data class NextPageIsLoading(val search: Search, val stargazers: List<Stargazer>) : StargazersViewState()
    data class NextPageHasError(val search: Search, val stargazers: List<Stargazer>, val throwable: Throwable) : StargazersViewState()
}

sealed class PartialStargazersViewState : PartialViewState {
    object NextPageLoading : PartialStargazersViewState()
    data class NextPageError(val throwable: Throwable) : PartialStargazersViewState()
    data class NextPageResults(val newStargazers: List<Stargazer>) : PartialStargazersViewState()
}

fun stargazersViewStateReduce(previous: StargazersViewState, partial: PartialStargazersViewState): StargazersViewState = when (partial) {
    PartialStargazersViewState.NextPageLoading -> when (previous) {
        is Results -> NextPageIsLoading(previous.search, previous.stargazers)
        is NextPageHasError -> NextPageIsLoading(previous.search, previous.stargazers)
        else -> previous
    }
    is PartialStargazersViewState.NextPageError -> when (previous) {
        is Results -> NextPageHasError(previous.search, previous.stargazers, partial.throwable)
        is StargazersViewState.NextPageIsLoading -> NextPageHasError(previous.search, previous.stargazers, partial.throwable)
        else -> previous
    }
    is PartialStargazersViewState.NextPageResults -> when (previous) {
        is Results -> Results(previous.search, previous.stargazers.plus(partial.newStargazers))
        is StargazersViewState.NextPageIsLoading -> Results(previous.search, previous.stargazers.plus(partial.newStargazers))
        is NextPageHasError -> Results(previous.search, previous.stargazers.plus(partial.newStargazers))
        else -> previous
    }
}

interface ViewState
interface PartialViewState : ViewState
