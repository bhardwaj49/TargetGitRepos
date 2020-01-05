package co.avinash.targetgitrepos.datahandler;

import android.content.Context;
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

    private static final String TAG = TrendingRepoDataHandler.class.getSimpleName();

    private Context mContext;
    private TrendingRepoApiResponseModel trendingRepoApiResponseModel;
    private NetworkRequestQueueHandler mRequetQueue;
    private TrendingRepoDataFetchListener trendingRepoDataFetchListener;

    public TrendingRepoDataHandler(Context context, TrendingRepoDataFetchListener repoDataFetchListener) {
        mContext = context;
        trendingRepoDataFetchListener = repoDataFetchListener;
    }

    public void fetchTrendingRepoData(boolean forceFetch, Date dataDownloadTimestamp) {
        boolean dataFetched;
        boolean isDataExpired = TrendingRepoUtils.isDataExpired(dataDownloadTimestamp);
        if (!forceFetch && !isDataExpired) {
            dataFetched = fetchCachedData();
            if (dataFetched) {
                return;
            }
        }

        if (mContext != null && TrendingRepoUtils.isNetworkAvailable(mContext)) {
            NetworkRequestObject requestHandler = new NetworkRequestObject(
                    TrendingRepoConstants.TRENDING_REPO_URL, Request.Method.GET, null, this, this,
                    new TrendingRepoApiResponseModel());
            if (mRequetQueue == null) {
                mRequetQueue = NetworkRequestQueueHandler.getInstance(mContext);
            }
            mRequetQueue.addToRequestQueue(requestHandler);
        } else {
            if (!forceFetch) {
                trendingRepoDataFetchListener.trendingRepoDataFetchFailed();
            } else {
                dataFetched = fetchCachedData();
                if (!dataFetched) {
                    trendingRepoDataFetchListener.trendingRepoDataFetchFailed();
                }
            }
        }
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
            trendingRepoApiResponseModel = (TrendingRepoApiResponseModel) response;
            if (trendingRepoDataFetchListener != null) {
                if (trendingRepoApiResponseModel != null
                        && trendingRepoApiResponseModel.getTrendingRepoModels() != null
                        && trendingRepoApiResponseModel.getTrendingRepoModels().size() > 0) {
                    cacheDownloadedData(trendingRepoApiResponseModel);
                    TrendingRepoUtils.saveDataDownloadTimestamp(mContext);
                    trendingRepoDataFetchListener.trendingRepoDataFecthed(trendingRepoApiResponseModel.getTrendingRepoModels());
                } else {
                    trendingRepoDataFetchListener.trendingRepoDataFetchFailed();
                }
            }
        }
    }

    private void cacheDownloadedData(TrendingRepoApiResponseModel trendingRepoApiResponseModel) {
        String jsonString = new Gson().toJson(trendingRepoApiResponseModel.getTrendingRepoModels());
        try {
            if (!TextUtils.isEmpty(jsonString)) {
                JSONFileOperator.saveData(mContext, jsonString);
            }
        } catch (Exception exception) {
        }
    }

    public void sortDataByStars(TrendingRepoDataSortListener trendingRepoDataSortListener) {
        if (trendingRepoApiResponseModel != null && trendingRepoApiResponseModel.getTrendingRepoModels() != null) {
            Collections.sort(trendingRepoApiResponseModel.getTrendingRepoModels(), new Comparator<TrendingRepoModel>() {
                @Override
                public int compare(TrendingRepoModel repo1, TrendingRepoModel repo2) {
                    return repo1.getStars().compareTo(repo2.getStars());
                }
            });

            if (trendingRepoDataSortListener != null) {
                trendingRepoDataSortListener.trendingRepoDataSorted(trendingRepoApiResponseModel.getTrendingRepoModels());
            }
        }
    }

    public void sortDataByName(TrendingRepoDataSortListener trendingRepoDataSortListener) {
        if (trendingRepoApiResponseModel != null && trendingRepoApiResponseModel.getTrendingRepoModels() != null) {
            List<TrendingRepoModel> modelList = trendingRepoApiResponseModel.getTrendingRepoModels();
            Collections.sort(modelList, new Comparator<TrendingRepoModel>() {
                @Override
                public int compare(TrendingRepoModel repo1, TrendingRepoModel repo2) {
                    String firstName = repo1.getName();
                    String secondName = repo2.getName();
                    if(firstName.contains("-")) {
                        firstName = firstName.split("-")[0];
                    }
                    if(secondName.contains("-")) {
                        secondName = secondName.split("-")[0];
                    }
                    Log.d("AVINASH", "AVINASH: " + firstName + " " + secondName);
                    return firstName.compareToIgnoreCase(secondName);
                }
            });

            if (trendingRepoDataSortListener != null) {
                trendingRepoApiResponseModel.setTrendingRepoModels(modelList);
                trendingRepoDataSortListener.trendingRepoDataSorted(modelList);
            }
        }
    }

    @TestOnly
    public List<TrendingRepoModel> getTrendingRepos() {
        return trendingRepoApiResponseModel == null ? null : trendingRepoApiResponseModel.getTrendingRepoModels();
    }

    @TestOnly
    public TrendingRepoDataFetchListener getTrendingRepoDataFetchListener() {
        return trendingRepoDataFetchListener;
    }
}
