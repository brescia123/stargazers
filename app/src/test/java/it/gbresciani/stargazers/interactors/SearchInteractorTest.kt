package it.gbresciani.stargazers.interactors

import com.jakewharton.retrofit2.adapter.rxjava2.Result
import io.reactivex.Observable
import it.gbresciani.stargazers.interactors.PartialStargazersViewState.*
import it.gbresciani.stargazers.interactors.StargazersViewState.*
import it.gbresciani.stargazers.network.Stargazer
import it.gbresciani.stargazers.network.StargazersService
import okhttp3.Headers
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import retrofit2.Response

class SearchInteractorTest {


    private lateinit var stargazersServiceMock: StargazersService
    private lateinit var searchInteactor: SearchInteractor
    private val user = "user"
    private val repo = "repo"
    private val search = user to repo
    private val nextPageUrl = "http://test.url"
    private val stargazers = listOf(
            Stargazer("user1", 1, "avatarUrl1", "htmlUrl1"),
            Stargazer("user2", 2, "avatarUrl2", "htmlUrl2"),
            Stargazer("user3", 3, "avatarUrl3", "htmlUrl3"),
            Stargazer("user4", 4, "avatarUrl4", "htmlUrl4"))
    private val firstPageResult = Result.response(Response.success(stargazers, Headers.of("Link", "<$nextPageUrl>; rel=\"next\", <https://api.github.com/repositories/7508411/stargazers?page=698>; rel=\"last\"")))

    @Before
    @Throws(Exception::class)
    fun setUp() {
        stargazersServiceMock = Mockito.mock(StargazersService::class.java)
        searchInteactor = SearchInteractor(stargazersServiceMock)
    }

    @Test
    fun search_emitsCorrectStates_whenASearchIsCompletedWithNoErrors() {
        `when`(stargazersServiceMock.getStargazers(user, repo)).thenReturn(Observable.just(firstPageResult))

        searchInteactor
                .search(search)
                .test()
                .assertValues(Loading(search), Results(search, stargazers))
    }

    @Test
    fun search_emitsCorrectStates_whenASearchIsCompletedWithNoErrorsAndEmptyResults() {
        `when`(stargazersServiceMock.getStargazers(user, repo)).thenReturn(Observable.just(Result.response(Response.success(emptyList<Stargazer>()))))

        searchInteactor
                .search(search)
                .test()
                .assertValues(Loading(search), EmptyResults(search))
    }

    @Test
    fun search_emitsCorrectStates_whenASearchIsCompletedWithErrors() {
        val searchInteractor = searchInteactor

        val exception = Exception()
        `when`(stargazersServiceMock.getStargazers(user, repo)).thenReturn(Observable.error(exception))
        searchInteactor.
                search(search)
                .test()
                .assertValues(Loading(search), Error(search, exception))

        `when`(stargazersServiceMock.getStargazers(user, repo)).thenReturn(Observable.just(Result.error(exception)))
        searchInteactor
                .search(search)
                .test()
                .assertValues(Loading(search), Error(search, exception))

    }

    @Test
    fun loadMoreResults_doesNotEmit_whenTheFirstPageWasNotRequested() {
        searchInteactor
                .loadMoreResults()
                .test()
                .assertNoValues()
                .onComplete()
    }

    @Test
    fun loadMoreResults_emitsCorrectPartialStates_whenTheNextPageIsLoadedWithNoErrors() {
        `when`(stargazersServiceMock.getStargazers(user, repo)).thenReturn(Observable.just(firstPageResult))
        `when`(stargazersServiceMock.getStargazers(nextPageUrl)).thenReturn(Observable.just(firstPageResult))

        searchInteactor.search(search).test()
        searchInteactor
                .loadMoreResults()
                .test()
                .assertValues(NextPageLoading, NextPageResults(stargazers))
    }

    @Test
    fun loadMoreResults_emitsCorrectPartialStates_whenTheNextPageIsLoadedWithError() {
        `when`(stargazersServiceMock.getStargazers(user, repo)).thenReturn(Observable.just(firstPageResult))
        val exception = Exception()
        `when`(stargazersServiceMock.getStargazers(nextPageUrl)).thenReturn(Observable.error(exception))

        searchInteactor.search(search).test()
        searchInteactor
                .loadMoreResults()
                .test()
                .assertValues(NextPageLoading, NextPageError(exception))
    }

}