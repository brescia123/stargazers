package it.gbresciani.stargazers.ui;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.gbresciani.stargazers.network.LinkHeaderParser;
import it.gbresciani.stargazers.network.Stargazer;
import it.gbresciani.stargazers.network.StargazersService;
import retrofit2.adapter.rxjava.Result;

public class StargazersPresenter {

    private StargazersService stargazersService;
    private StargazersView view;
    private Scheduler viewScheduler;
    private Scheduler jobScheduler;

    private String nextPageUrl;

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

    public void onDataEntered(String user, String repo) {
        stargazersService.getStargazers(user, repo)
                .subscribeOn(jobScheduler)
                .observeOn(viewScheduler)
                .subscribe(this::onResponse, this::onError);
    }

    public void endOfListReached() {
        if (nextPageUrl == null) return;
        stargazersService.getStargazers(nextPageUrl);
    }

    private void onResponse(Result<List<Stargazer>> result) {
        List<Stargazer> stargazers = result.response().body();
        if (stargazers.isEmpty()) {
            view.showEmptyView();
            return;
        }
        String linkHeader = result.response().headers().get("Link");
        nextPageUrl = LinkHeaderParser.getNextPageUrl(linkHeader);
        view.showStargazers(stargazers, nextPageUrl != null);
    }

    private void onError(Throwable t) {
        view.showError();
    }
}
