package it.gbresciani.stargazers.ui;

import com.jakewharton.retrofit2.adapter.rxjava2.Result;

import java.util.Collections;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.gbresciani.stargazers.network.LinkHeaderParser;
import it.gbresciani.stargazers.network.Stargazer;
import it.gbresciani.stargazers.network.StargazersService;

public class StargazersPresenter {

    private StargazersService stargazersService;
    private StargazersView view;
    private Scheduler viewScheduler;
    private Scheduler jobScheduler;

    private boolean isLoading = false;
    private String nextPageUrl;
    private List<Stargazer> stargazers = Collections.emptyList();

    /**
     * Default constructor that injects dependencies
     */
    public StargazersPresenter(StargazersView view, StargazersService stargazersService) {
        this.stargazersService = stargazersService;
        this.view = view;
        this.viewScheduler = AndroidSchedulers.mainThread();
        this.jobScheduler = Schedulers.io();
    }

    /**
     * Alternative constructor that injects dependencies including schedulers
     */
    public StargazersPresenter(StargazersView view, StargazersService stargazersService, Scheduler viewScheduler, Scheduler jobScheduler) {
        this.view = view;
        this.stargazersService = stargazersService;
        this.viewScheduler = viewScheduler;
        this.jobScheduler = jobScheduler;
    }

    /**
     * Method called from the View when the user starts a request.
     *
     * @param user the string representing the owner of the repository.
     * @param repo the string representing the repository name.
     */
    public void onDataEntered(String user, String repo) {
        setLoading(true);
        stargazersService.getStargazers(user, repo)
                .subscribeOn(jobScheduler)
                .observeOn(viewScheduler)
                .subscribe((result) -> onResponse(result, true), this::onError);
    }

    /**
     * Method called from the View when the user scrolls to the end of the list. It loads
     * the next page of Stargazers if there is a nextPageUrl.
     */
    public void endOfListReached() {
        if (nextPageUrl == null) return;
        if (isLoading) return;
        setLoading(true);
        stargazersService.getStargazers(nextPageUrl)
                .subscribeOn(jobScheduler)
                .observeOn(viewScheduler)
                .subscribe((result) -> onResponse(result, false), this::onError);
    }

    /**
     * Method that handles a response from the Stargazers service.
     *
     * @param result the result return by the service.
     * @param newSearch whether the result comes from a new search or a next page request.
     */
    private void onResponse(Result<List<Stargazer>> result, boolean newSearch) {
        setLoading(false);
        // If no results are found show empty view
        if (result.response().code() == 404) {
            view.showEmptyView();
            return;
        }
        // If there is some problem show an error
        if (!result.response().isSuccessful()) {
            view.showError();
            return;
        }
        List<Stargazer> newStargazers = result.response().body();
        if (newSearch) {
            stargazers = newStargazers;
        } else {
            stargazers.addAll(newStargazers);
        }
        // If there are no stargazers show empty view
        if (stargazers.isEmpty()) {
            view.showEmptyView();
            return;
        }
        // Get, if present, the next page url
        nextPageUrl = LinkHeaderParser.getNextPageUrl(result.response().headers().get("Link"));
        // Show the list of stargazers
        view.showStargazers(stargazers, nextPageUrl != null);
    }

    private void onError(Throwable t) {
        setLoading(false);
        view.showError();
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
        view.enableAction(!loading);
    }
}
