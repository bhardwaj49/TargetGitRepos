package co.avinash.targetgitrepos.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import javax.inject.Inject;

import co.avinash.targetgitrepos.R;
import co.avinash.targetgitrepos.adapter.TrendingRepoDataAdapter;
import co.avinash.targetgitrepos.listeners.TrendingRepoUIUpdateListener;
import co.avinash.targetgitrepos.model.TrendingRepoModel;
import co.avinash.targetgitrepos.presenter.TrendingRepoPresenter;
import dagger.android.AndroidInjection;

public class TrendingRepoHomeActivity extends AppCompatActivity implements TrendingRepoUIUpdateListener,
        TrendingRepoDataAdapter.TrendingRepoNameFilterListener, TrendingRepoDataAdapter.TrendingRepoItemClickListener {

    @Inject
    protected TrendingRepoPresenter mTrendingRepoPresenter;

    @Inject
    protected TrendingRepoDataAdapter mTrendingRepoDataAdapter;

    private RecyclerView mTrendingRepoRecyclerVw;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mPullToRefresh;
    private RelativeLayout mErrorLayout;
    private TextView mRetry;
    private PopupMenu mPopupMenu;
    private EditText mSearchBox;
    private RelativeLayout mNoReposLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
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
                mSearchBox.setText("");
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
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorLayout = findViewById(R.id.errorLayout);
        mNoReposLayout = findViewById(R.id.no_repos_lyt);
        mSearchBox = findViewById(R.id.searchBox);
        mSearchBox.setVisibility(View.GONE);
        mSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence newText, int start, int before, int count) {
                try {
                    if (mTrendingRepoDataAdapter != null) {
                        mTrendingRepoDataAdapter.getFilter().filter(newText);
                    }
                } catch (Exception e) {
                    Log.e("AVINASH", e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mRetry = findViewById(R.id.retry);
        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchTrendingRepoData(false);
            }
        });

        mTrendingRepoRecyclerVw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        hideKeyboard();
                        break;
                }
                return false;
            }
        });
    }

    private void fetchTrendingRepoData(boolean forceFetch) {
        if(mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        if (mTrendingRepoPresenter != null) {
            mTrendingRepoPresenter.fetchTrendingRepoData(forceFetch);
        }
    }

    @Override
    public void updateRepoDataOnUi(List<TrendingRepoModel> trendingRepoModels) {
        if (trendingRepoModels != null) {
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.GONE);
            }

            mPullToRefresh.setVisibility(View.VISIBLE);
            mErrorLayout.setVisibility(View.GONE);

            updateAdapterAndDisplayData(trendingRepoModels);
        }
    }

    private void updateAdapterAndDisplayData(List<TrendingRepoModel> trendingRepoModels) {
        mTrendingRepoDataAdapter.setContext(this);
        mTrendingRepoDataAdapter.setNameFilterListener(this);
        mTrendingRepoDataAdapter.setTrendingRepoItemClickListener(this);
        mTrendingRepoDataAdapter.setTrendingRepoModels(trendingRepoModels);
        mTrendingRepoRecyclerVw.setAdapter(mTrendingRepoDataAdapter);
        mSearchBox.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateDataFetchFailedOnUi() {
        mErrorLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mPullToRefresh.setVisibility(View.GONE);
    }

    @Override
    public void showNoReposLayout(boolean show) {
        if(show) {
            mNoReposLayout.setVisibility(View.VISIBLE);
            mPullToRefresh.setVisibility(View.GONE);
        } else {
            mNoReposLayout.setVisibility(View.GONE);
            mPullToRefresh.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void handleItemClick(TrendingRepoModel trendingRepoModel) {
        Intent intent = new Intent(TrendingRepoHomeActivity.this, TrendingRepoDetailsActivity.class);
        intent.putExtra("trending_repo_model", trendingRepoModel);
        startActivity(intent);
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

    public void hideKeyboard() {

        if (this == null || isFinishing()) {
            return;
        }

        try {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("AVINASH", e.getMessage());
        }
    }
}
