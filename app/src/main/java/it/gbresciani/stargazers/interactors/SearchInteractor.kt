package it.gbresciani.stargazers.interactors

import com.jakewharton.retrofit2.adapter.rxjava2.Result
import io.reactivex.Observable
import it.gbresciani.stargazers.interactors.PartialStargazersViewState.*
import it.gbresciani.stargazers.interactors.StargazersViewState.Error
import it.gbresciani.stargazers.network.Stargazer
import it.gbresciani.stargazers.network.StargazersService

class SearchInteractor(val stargazersService: StargazersService) {

    private var paginatedLoader: PaginatedLoader? = null

    fun search(search: Pair<String, String>): Observable<StargazersViewState> {
        with(PaginatedLoader(search, stargazersService)) {
            paginatedLoader = this
            return this.getFirstPage()
                    .map {
                        interpretError(it)?.let { return@map Error(search, it) }

                        val stargazers = it.response().body()
                        if (stargazers.isEmpty()) return@map StargazersViewState.EmptyResults(search)

                        return@map StargazersViewState.Results(search, stargazers)
                    }
                    .startWith(StargazersViewState.Loading(search))
                    .onErrorReturn { Error(search, it) }
        }
    }

    fun thereAreMorePages() = paginatedLoader?.nextPageUrl != null

    fun loadMoreResults(): Observable<PartialStargazersViewState> =
            paginatedLoader?.getNextPage()
                    ?.map {
                        val error = interpretError(it)
                        if (error != null)
                            NextPageError(error)
                        else
                            NextPageResults(it.response().body())
                    }
                    ?.startWith(NextPageLoading)
                    ?.onErrorReturn(::NextPageError)
                    ?: Observable.empty()  // No paginatedLoader

    private fun interpretError(result: Result<List<Stargazer>>): Throwable? {
        if (result.isError) return result.error()

        val response = result.response()
        if (response.code() == 404) return Throwable("404")
        if (response.isSuccessful == false) return Throwable("Request not successful (${response.code()})")
        return null
    }
}