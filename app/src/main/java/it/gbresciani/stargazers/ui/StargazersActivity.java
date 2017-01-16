package it.gbresciani.stargazers.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

import it.gbresciani.stargazers.R;
import it.gbresciani.stargazers.StargazersApp;
import it.gbresciani.stargazers.network.Stargazer;

public class StargazersActivity extends Activity implements StargazersView {

    private LinearLayoutManager layoutManager;
    private StargazersAdapter stargazersAdapter;
    private RecyclerView stargazersRecyclerView;

    private LinearLayout emptyLayout;
    private CoordinatorLayout mainLayout;
    private FloatingActionButton searchFAB;
    private EditText ownerEditText;
    private EditText repoEditText;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private String title = "";
    private StargazersPresenter presenter;

    private View.OnClickListener fabOnClickListener = view -> {
        String user = ownerEditText.getText().toString();
        String repo = repoEditText.getText().toString();
        if (user.isEmpty()) {
            Snackbar.make(stargazersRecyclerView, R.string.msg_insert_username, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (repo.isEmpty()) {
            Snackbar.make(stargazersRecyclerView, R.string.msg_insert_repo, Snackbar.LENGTH_SHORT).show();
            return;
        }
        title = user + "/" + repo;
        closeKeyboard();
        presenter.onDataEntered(user, repo);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stargazers);
        presenter = new StargazersPresenter(this, StargazersApp.getStargazersService());
        title = getString(R.string.app_name);
        initViews();
        setViewListeners();
        initStargazersRecyclerView();
    }

    private void initViews() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        stargazersRecyclerView = (RecyclerView) findViewById(R.id.stargazersRecyclerView);
        emptyLayout = (LinearLayout) findViewById(R.id.emptyLayout);
        mainLayout = (CoordinatorLayout) findViewById(R.id.mainLayout);
        searchFAB = (FloatingActionButton) findViewById(R.id.searchFAB);
        ownerEditText = (EditText) findViewById(R.id.ownerEditText);
        repoEditText = (EditText) findViewById(R.id.repoEditText);
    }

    private void setViewListeners() {
        // Enable appbar expansion on click when collapsed
        appBarLayout.setOnClickListener(view -> appBarLayout.setExpanded(true, true));
        searchFAB.setOnClickListener(fabOnClickListener);
        // Listener that hides the collapsingToolbarLayout title when expanded
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(title);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void initStargazersRecyclerView() {
        stargazersRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        stargazersRecyclerView.setLayoutManager(layoutManager);

        stargazersAdapter = new StargazersAdapter();
        stargazersRecyclerView.setAdapter(stargazersAdapter);
        // Set a listener that notify the presenter when the end of the list is reached.
        stargazersRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleChildCount = recyclerView.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((firstVisibleItemPosition + visibleChildCount) >= totalItemCount) {
                    presenter.endOfListReached();
                }
            }
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void showError() {
        Snackbar.make(mainLayout, R.string.msg_error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showStargazers(List<Stargazer> stargazers, boolean showLoadingMore) {
        stargazersAdapter.setStargazers(stargazers);
        stargazersAdapter.showLoadingFooter(showLoadingMore);
        stargazersRecyclerView.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
    }

    @Override
    public void enableAction(boolean enable) {
        if (enable) {
            searchFAB.setOnClickListener(fabOnClickListener);
        } else {
            searchFAB.setOnClickListener(null);
        }
    }

    @Override
    public void showEmptyView() {
        stargazersRecyclerView.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }
}
