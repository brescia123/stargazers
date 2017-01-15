package it.gbresciani.stargazers.ui;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import it.gbresciani.stargazers.network.Stargazer;
import it.gbresciani.stargazers.network.StargazersService;
import okhttp3.Headers;
import retrofit2.Response;
import retrofit2.adapter.rxjava.Result;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StargazersPresenterTest {

    private StargazersService stargazersServiceMock;
    private StargazersView stargazersViewMock;

    private StargazersPresenter stargazersPresenter;

    private String owner = "reactivex";
    private String repo = "rxjava";
    private String linkHeader = "<https://api.github.com/repositories/7508411/stargazers?page=2>; rel=\"next\", <https://api.github.com/repositories/7508411/stargazers?page=682>; rel=\"last\"";
    private List<Stargazer> stargazers = Arrays.asList(
            new Stargazer("user1", 1, "avatarUrl1", "htmlUrl1"),
            new Stargazer("user2", 2, "avatarUrl2", "htmlUrl2"),
            new Stargazer("user3", 3, "avatarUrl3", "htmlUrl3"),
            new Stargazer("user4", 4, "avatarUrl4", "htmlUrl4"));

    @Before
    public void setUp() throws Exception {
        stargazersViewMock = Mockito.mock(StargazersView.class);
        stargazersServiceMock = Mockito.mock(StargazersService.class);
        stargazersPresenter = new StargazersPresenter(stargazersViewMock, stargazersServiceMock, Schedulers.trampoline(), Schedulers.trampoline());
    }

    @Test
    public void onDataEntered_shouldCallRightViewMethods_whenNetworkResponseIsSuccessful() throws Exception {
        // When...
        when(stargazersServiceMock.getStargazers(owner, repo))
                .thenReturn(Observable.just(Result.response(Response.success(stargazers))));

        // Return...
        stargazersPresenter.onDataEntered(owner, repo);

        // Assert...
        verify(stargazersViewMock).showStargazers(stargazers, false);
    }

    @Test
    public void onDataEntered_shouldCallRightViewMethods_whenNetworkResponseIsSuccessfulAndItIsNotTheLastResultsPage() throws Exception {
        // When...
        when(stargazersServiceMock.getStargazers(owner, repo))
                .thenReturn(Observable.just(Result.response(Response.success(stargazers, Headers.of("Link", linkHeader)))));

        // Return...
        stargazersPresenter.onDataEntered(owner, repo);

        // Assert...
        verify(stargazersViewMock).showStargazers(stargazers, true);
    }

    @Test
    public void onDataEntered_shouldCallRightViewMethods_whenNetworkResponseIsSuccessfulAndTheListIsEmpty() throws Exception {
        // When...
        List<Stargazer> stargazers = Collections.emptyList();
        when(stargazersServiceMock.getStargazers(owner, repo))
                .thenReturn(Observable.just(Result.response(Response.success(stargazers))));

        // Return...
        stargazersPresenter.onDataEntered(owner, repo);

        // Assert...
        verify(stargazersViewMock).showEmptyView();
    }

    @Test
    public void onDataEntered_shouldCallRightViewMethods_whenThereIsAnError() throws Exception {
        // When...
        when(stargazersServiceMock.getStargazers(owner, repo))
                .thenReturn(Observable.just(Result.error(new Exception())));

        // Return...
        stargazersPresenter.onDataEntered(owner, repo);

        // Assert...
        verify(stargazersViewMock).showError();
    }

    @Test
    public void endOfListReached_shouldDoNothing_whenThereIsNoNextPage() throws Exception {
        // When...
        when(stargazersServiceMock
                .getStargazers(owner, repo)).thenReturn(Observable.just(Result.response(Response.success(stargazers))));
        stargazersPresenter.onDataEntered(owner, repo);

        // Return...
        stargazersPresenter.endOfListReached();

        // Assert...
        verify(stargazersServiceMock, never()).getStargazers(anyString());
    }

    @Test
    public void endOfListReached_shouldLoadNextPage_whenThereIsANextPage() throws Exception {
        // When...
        when(stargazersServiceMock.getStargazers(owner, repo))
                .thenReturn(Observable.just(Result.response(Response.success(stargazers, Headers.of("Link", linkHeader)))));
        stargazersPresenter.onDataEntered(owner, repo);

        // Return...
        stargazersPresenter.endOfListReached();

        // Assert...
        verify(stargazersServiceMock, times(1)).getStargazers("https://api.github.com/repositories/7508411/stargazers?page=2");
    }


}