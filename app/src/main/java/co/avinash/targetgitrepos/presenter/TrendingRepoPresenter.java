package co.avinash.targetgitrepos.presenter;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import co.avinash.targetgitrepos.datahandler.TrendingRepoDataHandler;
import co.avinash.targetgitrepos.listeners.TrendingRepoDataFetchListener;
import co.avinash.targetgitrepos.listeners.TrendingRepoDataSortListener;
import co.avinash.targetgitrepos.listeners.TrendingRepoUIUpdateListener;
import co.avinash.targetgitrepos.model.TrendingRepoModel;
import co.avinash.targetgitrepos.utils.TrendingRepoUtils;

public class TrendingRepoPresenter implements TrendingRepoDataFetchListener, TrendingRepoDataSortListener {

    private Context mContext;
    private TrendingRepoDataHandler mTrendingRepoDataHandler;
    private TrendingRepoUIUpdateListener mTrendingRepoUIUpdateListener;

    @Inject
    public TrendingRepoPresenter() {
    }

    public void setContext(Context context) {
        mContext = context;
        initDataHandler();
    }

    private void initDataHandler() {
        mTrendingRepoDataHandler = new TrendingRepoDataHandler(mContext, this);
    }

    public void subscribeToUiUpdateListener(TrendingRepoUIUpdateListener trendingRepoUIUpdateListener) {
        mTrendingRepoUIUpdateListener = trendingRepoUIUpdateListener;
    }

    public void fetchTrendingRepoData(boolean forceFetch) {
        if (mTrendingRepoDataHandler != null) {
            mTrendingRepoDataHandler.fetchTrendingRepoData(forceFetch,
                    TrendingRepoUtils.getDataDownloadedTimestamp(mContext));
        }
    }

    @Override
    public void trendingRepoDataFecthed(List<TrendingRepoModel> trendingRepoModels) {
        if (mTrendingRepoUIUpdateListener != null) {
            mTrendingRepoUIUpdateListener.updateRepoDataOnUi(trendingRepoModels);
        }
    }

    @Override
    public void trendingRepoDataFetchFailed() {
        if (mTrendingRepoUIUpdateListener != null) {
            mTrendingRepoUIUpdateListener.updateDataFetchFailedOnUi();
        }
    }

    public void sortDataByStars() {
        mTrendingRepoDataHandler.sortDataByStars(this);
    }

    public void sortDataByName() {
        mTrendingRepoDataHandler.sortDataByName(this);
    }

    @Override
    public void trendingRepoDataSorted(List<TrendingRepoModel> trendingRepoModels) {
        if (mTrendingRepoUIUpdateListener != null) {
            mTrendingRepoUIUpdateListener.updateRepoDataOnUi(trendingRepoModels);
        }
    }
}
