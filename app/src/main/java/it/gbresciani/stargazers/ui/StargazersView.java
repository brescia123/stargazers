package it.gbresciani.stargazers.ui;

import java.util.List;

import it.gbresciani.stargazers.network.Stargazer;

interface StargazersView {

    /**
     * Method that shows the list of stargazers.
     *
     * @param stargazers      the list to be shown.
     * @param showLoadingMore whether to show that there are more elements to load.
     */
    void showStargazers(List<Stargazer> stargazers, boolean showLoadingMore);

    /**
     * Method that enables or disables the search action.
     */
    void enableAction(boolean show);

    void showError();

    void showEmptyView();
}


