package co.avinash.targetgitrepos.views;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import javax.inject.Inject;

import co.avinash.targetgitrepos.R;
import co.avinash.targetgitrepos.TrendingRepoApplication;
import co.avinash.targetgitrepos.adapter.TrendingRepoDataAdapter;
import co.avinash.targetgitrepos.component.ApplicationComponent;
import co.avinash.targetgitrepos.listeners.TrendingRepoUIUpdateListener;
import co.avinash.targetgitrepos.model.TrendingRepoModel;
import co.avinash.targetgitrepos.presenter.TrendingRepoPresenter;

public class TrendingRepoHomeActivity extends AppCompatActivity implements TrendingRepoUIUpdateListener {

    @Inject
    protected TrendingRepoPresenter mTrendingRepoPresenter;

    @Inject
    protected TrendingRepoDataAdapter mTrendingRepoDataAdapter;

    ApplicationComponent applicationComponent;

    private RecyclerView mTrendingRepoRecyclerVw;
    private ShimmerFrameLayout mShimmerFrameLayout;
    private SwipeRefreshLayout mPullToRefresh;
    private RelativeLayout mErrorLayout;
    private TextView mRetry;
    private PopupMenu mPopupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applicationComponent = ((TrendingRepoApplication) getApplicationContext()).getApplicationComponent();
        applicationComponent.inject(this);

        setContentView(R.layout.trending_repo_home_activity);
        Toolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        initializePopupMenu(toolbar);
        initPresenter();
        initUi();
        initPullToRefreshView();
        fetchTrendingRepoData(false);
    }

    private void initializePopupMenu(Toolbar toolbar) {
        final View anchor = toolbar.findViewById(R.id.moreOptions);
        anchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupMenu = new PopupMenu(TrendingRepoHomeActivity.this, anchor);
                mPopupMenu.inflate(R.menu.trending_repo_menu);
                mPopupMenu.setOnMenuItemClickListener(new MenuOptionsSelectedListener());
                mPopupMenu.show();
            }
        });
    }

    private void initPullToRefreshView() {
        mPullToRefresh = findViewById(R.id.pullToRefresh);
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTrendingRepoData(true);
                mPullToRefresh.setRefreshing(false);
            }
        });
    }

    private void initPresenter() {
        mTrendingRepoPresenter.setContext(this);
        mTrendingRepoPresenter.subscribeToUiUpdateListener(this);
    }

    private void initUi() {
        mTrendingRepoRecyclerVw = findViewById(R.id.trending_repo_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mTrendingRepoRecyclerVw.setLayoutManager(linearLayoutManager);
        mShimmerFrameLayout = findViewById(R.id.parentShimmerLayout);
        mErrorLayout = findViewById(R.id.errorLayout);
        mRetry = findViewById(R.id.retry);
        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchTrendingRepoData(false);
            }
        });
    }

    private void fetchTrendingRepoData(boolean forceFetch) {
        if (mShimmerFrameLayout != null) {
            mShimmerFrameLayout.startShimmerAnimation();
        }
        if (mTrendingRepoPresenter != null) {
            mTrendingRepoPresenter.fetchTrendingRepoData(forceFetch);
        }
    }

    @Override
    public void updateRepoDataOnUi(List<TrendingRepoModel> trendingRepoModels) {
        if (trendingRepoModels != null) {
            if (mShimmerFrameLayout != null) {
                mShimmerFrameLayout.stopShimmerAnimation();
                mShimmerFrameLayout.setVisibility(View.GONE);
            }

            mPullToRefresh.setVisibility(View.VISIBLE);
            mErrorLayout.setVisibility(View.GONE);

            if (mTrendingRepoDataAdapter != null) {
                mTrendingRepoDataAdapter.setTrendingRepoModels(trendingRepoModels);
                mTrendingRepoDataAdapter.notifyDataSetChanged();
            } else {
                initializeAdapterAndDisplayData(trendingRepoModels);
            }
        }
    }

    private void initializeAdapterAndDisplayData(List<TrendingRepoModel> trendingRepoModels) {
        mTrendingRepoDataAdapter.setContext(this);
        mTrendingRepoDataAdapter.setTrendingRepoModels(trendingRepoModels);
        mTrendingRepoRecyclerVw.setAdapter(mTrendingRepoDataAdapter);
    }

    @Override
    public void updateDataFetchFailedOnUi() {
        mErrorLayout.setVisibility(View.VISIBLE);
        mShimmerFrameLayout.setVisibility(View.GONE);
        mPullToRefresh.setVisibility(View.GONE);
    }

    private class MenuOptionsSelectedListener implements
            PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.sortByStars:
                    mTrendingRepoPresenter.sortDataByStars();
                    return true;
                case R.id.sortByName:
                    mTrendingRepoPresenter.sortDataByName();
                    return true;
            }
            return false;
        }
    }
}
