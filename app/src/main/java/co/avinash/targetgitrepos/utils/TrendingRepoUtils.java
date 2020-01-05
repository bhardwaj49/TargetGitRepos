package co.avinash.targetgitrepos.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class TrendingRepoUtils {

    private static final String TAG = TrendingRepoUtils.class.getSimpleName();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        return false;
    }

    public static void saveDataDownloadTimestamp(Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(TrendingRepoConstants.TRENDING_REPO_PREF,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TrendingRepoConstants.DATA_DOWNLOAD_TIMESTAMP,
                    Calendar.getInstance().getTime().toString());
            editor.commit();
        } catch (Exception exception) {
        }
    }

    public static Date getDataDownloadedTimestamp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TrendingRepoConstants.TRENDING_REPO_PREF,
                Context.MODE_PRIVATE);
        try {
            return new Date(sharedPreferences.getString(TrendingRepoConstants.DATA_DOWNLOAD_TIMESTAMP, ""));
        } catch (Exception exception) {
            Log.e(TAG, "Received exceptio while getting data download timestamp");
        }
        return null;
    }

    public static boolean isDataExpired(Date dataDownloadedAt) {
        try {
            if(dataDownloadedAt != null) {
                Date currentTimestamp = Calendar.getInstance().getTime();
                long diff = (currentTimestamp.getTime() - dataDownloadedAt.getTime());
                long diffDays = diff / TrendingRepoConstants.CACHED_DATA_DAY_FACTOR;
                long diffHours =  diff / TrendingRepoConstants.CACHED_DATA_HOUR_FACTOR % 24;
                if (diffDays != 0 || diffHours >= 2) {
                    return true;
                }
            }
        } catch (Exception exception) {
            Log.e(TAG, "Received exceptio while checking for data expiry");
        }

        return false;
    }
}
