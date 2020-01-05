package co.avinash.targetgitrepos.datahandler;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.jetbrains.annotations.TestOnly;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import co.avinash.targetgitrepos.listeners.TrendingRepoDataFetchListener;
import co.avinash.targetgitrepos.listeners.TrendingRepoDataSortListener;
import co.avinash.targetgitrepos.model.IDataModel;
import co.avinash.targetgitrepos.model.TrendingRepoApiResponseModel;
import co.avinash.targetgitrepos.model.TrendingRepoModel;
import co.avinash.targetgitrepos.network.JSONFileOperator;
import co.avinash.targetgitrepos.network.NetworkRequestObject;
import co.avinash.targetgitrepos.network.NetworkRequestQueueHandler;
import co.avinash.targetgitrepos.utils.TrendingRepoConstants;
import co.avinash.targetgitrepos.utils.TrendingRepoUtils;

public class TrendingRepoDataHandler implements Response.Listener<IDataModel>, Response.ErrorListener {

    private Context mContext;
    private TrendingRepoApiResponseModel mTrendingRepoApiResponseModel;
    private NetworkRequestQueueHandler mRequetQueue;
    private TrendingRepoDataFetchListener trendingRepoDataFetchListener;

    public TrendingRepoDataHandler(Context context, TrendingRepoDataFetchListener repoDataFetchListener) {
        mContext = context;
        trendingRepoDataFetchListener = repoDataFetchListener;
    }

    public void fetchTrendingRepoData(final boolean forceFetch, final Date dataDownloadTimestamp) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean isDataExpired = TrendingRepoUtils.isDataExpired(dataDownloadTimestamp);
                if (!forceFetch && !isDataExpired) {
                    boolean dataFetched = fetchCachedData();
                    if (dataFetched) {
                        return true;
                    }
                }

                if (mContext != null && TrendingRepoUtils.isNetworkAvailable(mContext)) {
                    NetworkRequestObject requestHandler = new NetworkRequestObject(
                            TrendingRepoConstants.TRENDING_REPO_URL, Request.Method.GET, null,
                            TrendingRepoDataHandler.this, TrendingRepoDataHandler.this,
                            new TrendingRepoApiResponseModel());
                    if (mRequetQueue == null) {
                        mRequetQueue = NetworkRequestQueueHandler.getInstance(mContext);
                    }
                    mRequetQueue.addToRequestQueue(requestHandler);
                    return true;
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean requestQueued) {
                super.onPostExecute(requestQueued);
                if(!requestQueued) {
                    if (!forceFetch) {
                        trendingRepoDataFetchListener.trendingRepoDataFetchFailed();
                    } else {
                        boolean dataFetched = fetchCachedData();
                        if (!dataFetched) {
                            trendingRepoDataFetchListener.trendingRepoDataFetchFailed();
                        }
                    }
                }
            }
        }.execute();
    }

    private boolean fetchCachedData() {
        try {
            String offlineRepoData = JSONFileOperator.getData(mContext);
            if (!TextUtils.isEmpty(offlineRepoData)) {
                TrendingRepoModel[] trendingRepoModelList =
                        new Gson().fromJson(offlineRepoData, TrendingRepoModel[].class);
                if (trendingRepoModelList != null) {
                    TrendingRepoApiResponseModel pictionaryApiResponseModel = new TrendingRepoApiResponseModel();
                    pictionaryApiResponseModel.setTrendingRepoModels(Arrays.asList(trendingRepoModelList));
                    onResponse(pictionaryApiResponseModel);
                    return true;
                }
            }
        } catch (Exception exception) {
            Log.e("AVINASH", exception.getMessage());
        }
        return false;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error != null && trendingRepoDataFetchListener != null) {
            trendingRepoDataFetchListener.trendingRepoDataFetchFailed();
        }
    }

    @Override
    public void onResponse(IDataModel response) {
        if (response instanceof TrendingRepoApiResponseModel) {
            mTrendingRepoApiResponseModel = (TrendingRepoApiResponseModel) response;
            if (trendingRepoDataFetchListener != null) {
                if (mTrendingRepoApiResponseModel != null
                        && mTrendingRepoApiResponseModel.getTrendingRepoModels() != null
                        && mTrendingRepoApiResponseModel.getTrendingRepoModels().size() > 0) {
                    cacheDownloadedData(mTrendingRepoApiResponseModel);
                    TrendingRepoUtils.saveDataDownloadTimestamp(mContext);
                    trendingRepoDataFetchListener.trendingRepoDataFecthed(mTrendingRepoApiResponseModel.getTrendingRepoModels());
                } else {
                    trendingRepoDataFetchListener.trendingRepoDataFetchFailed();
                }
            }
        }
    }

    private void cacheDownloadedData(final TrendingRepoApiResponseModel trendingRepoApiResponseModel) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                String jsonString = new Gson().toJson(trendingRepoApiResponseModel.getTrendingRepoModels());
                try {
                    if (!TextUtils.isEmpty(jsonString)) {
                        JSONFileOperator.saveData(mContext, jsonString);
                    }
                } catch (Exception exception) {
                    Log.e("AVINASH", exception.getMessage());
                }
                return null;
            }
        }.execute();
    }

    public void sortDataByStars(final TrendingRepoDataSortListener trendingRepoDataSortListener) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                if (mTrendingRepoApiResponseModel != null && mTrendingRepoApiResponseModel.getTrendingRepoModels() != null) {
                    Collections.sort(mTrendingRepoApiResponseModel.getTrendingRepoModels(), new Comparator<TrendingRepoModel>() {
                        @Override
                        public int compare(TrendingRepoModel repo1, TrendingRepoModel repo2) {
                            return repo1.getStars().compareTo(repo2.getStars());
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (trendingRepoDataSortListener != null) {
                    trendingRepoDataSortListener.trendingRepoDataSorted(mTrendingRepoApiResponseModel.getTrendingRepoModels());
                }
            }
        }.execute();
    }

    public void sortDataByName(final TrendingRepoDataSortListener trendingRepoDataSortListener) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                if (mTrendingRepoApiResponseModel != null && mTrendingRepoApiResponseModel.getTrendingRepoModels() != null) {
                    Collections.sort(mTrendingRepoApiResponseModel.getTrendingRepoModels(), new Comparator<TrendingRepoModel>() {
                        @Override
                        public int compare(TrendingRepoModel repo1, TrendingRepoModel repo2) {
                            String firstName = repo1.getName();
                            String secondName = repo2.getName();
                            if (firstName.contains("-")) {
                                firstName = firstName.split("-")[0];
                            }
                            if (secondName.contains("-")) {
                                secondName = secondName.split("-")[0];
                            }
                            Log.d("AVINASH", "AVINASH: " + firstName + " " + secondName);
                            return firstName.compareToIgnoreCase(secondName);
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (trendingRepoDataSortListener != null) {
                    trendingRepoDataSortListener.trendingRepoDataSorted(mTrendingRepoApiResponseModel.getTrendingRepoModels());
                }
            }
        }.execute();
    }

    @TestOnly
    public List<TrendingRepoModel> getTrendingRepos() {
        return mTrendingRepoApiResponseModel == null ? null : mTrendingRepoApiResponseModel.getTrendingRepoModels();
    }

    @TestOnly
    public TrendingRepoDataFetchListener getTrendingRepoDataFetchListener() {
        return trendingRepoDataFetchListener;
    }
}
