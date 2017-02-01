package it.gbresciani.stargazers.interactors

import com.jakewharton.retrofit2.adapter.rxjava2.Result
import io.reactivex.Observable
import it.gbresciani.stargazers.network.Stargazer
import it.gbresciani.stargazers.network.StargazersService
import okhttp3.Headers
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.Response


class PaginatedLoaderTest {

    private lateinit var stargazersServiceMock: StargazersService
    private val user = "user"
    private val repo = "repo"
    private val stargazers = listOf(
            Stargazer("user1", 1, "avatarUrl1", "htmlUrl1"),
            Stargazer("user2", 2, "avatarUrl2", "htmlUrl2"),
            Stargazer("user3", 3, "avatarUrl3", "htmlUrl3"),
            Stargazer("user4", 4, "avatarUrl4", "htmlUrl4"))
    private val firstPageResult = Result.response(Response.success(stargazers))

    @Before
    @Throws(Exception::class)
    fun setUp() {
        stargazersServiceMock = mock(StargazersService::class.java)
    }

    @Test
    fun getFirstPage_returnStargazersServiceResults() {
        val paginatedLoader = PaginatedLoader(user to repo, stargazersServiceMock)

        `when`(stargazersServiceMock.getStargazers(user, repo)).thenReturn(Observable.just(firstPageResult))
        paginatedLoader.getFirstPage().test().assertValue(firstPageResult).assertComplete()

        `when`(stargazersServiceMock.getStargazers(user, repo)).thenReturn(Observable.error(Exception("error")))
        paginatedLoader.getFirstPage().test().assertError { true }
    }

    @Test
    fun getNextPage_returnEmptyObservable_whenThereIsNoNextPage() {
        val paginatedLoader = PaginatedLoader(user to repo, stargazersServiceMock)

        `when`(stargazersServiceMock.getStargazers(user, repo)).thenReturn(Observable.just(firstPageResult))
        paginatedLoader.getFirstPage().test()
        paginatedLoader.getNextPage().test().assertNoValues().onComplete()
    }

    @Test
    fun getNextPage_returnStargazersServiceResults_whenThereIsNextPage() {
        val paginatedLoader = PaginatedLoader(user to repo, stargazersServiceMock)

        val nextPageUrl = "https://api.github.com/repositories/7508411/stargazers?page=2"
        val firstResult = Result.response(Response.success(stargazers, Headers.of("Link", "<$nextPageUrl>; rel=\"next\", <https://api.github.com/repositories/7508411/stargazers?page=698>; rel=\"last\"")))
        val nextPageResult = Result.response(Response.success(stargazers))
        `when`(stargazersServiceMock.getStargazers(user, repo)).thenReturn(Observable.just(firstResult))
        `when`(stargazersServiceMock.getStargazers(nextPageUrl)).thenReturn(Observable.just(nextPageResult))
        paginatedLoader.getFirstPage().test()
        paginatedLoader.getNextPage().test().assertValue(nextPageResult).assertComplete()
    }

}
