package it.gbresciani.stargazers.interactors

import com.jakewharton.retrofit2.adapter.rxjava2.Result
import io.reactivex.Observable
import it.gbresciani.stargazers.network.Stargazer
import it.gbresciani.stargazers.network.StargazersService
import it.gbresciani.stargazers.network.getNextPageUrl

class PaginatedLoader(val search: Pair<String, String>, val stargazersService: StargazersService) {

    var nextPageUrl: String? = null

    fun getFirstPage(): Observable<Result<List<Stargazer>>> {
        val (user, repo) = search
        return stargazersService.getStargazers(user, repo).doOnNext { storeNextPageUrl(it) }
    }

    fun getNextPage(): Observable<Result<List<Stargazer>>> {
        if (nextPageUrl == null) return Observable.empty()
        return stargazersService.getStargazers(nextPageUrl).doOnNext { storeNextPageUrl(it) }
    }

    private fun storeNextPageUrl(result: Result<MutableList<Stargazer>>) {
        if (result.isError) return
        if (result.response().isSuccessful == false) return
        nextPageUrl = getNextPageUrl(result.response().headers().get("Link"))
    }
}

