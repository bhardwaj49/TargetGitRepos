package co.avinash.targetgitrepos.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import co.avinash.targetgitrepos.model.IDataModel;

public class NetworkRequestQueueHandler {

    private static NetworkRequestQueueHandler mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private NetworkRequestQueueHandler(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized NetworkRequestQueueHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkRequestQueueHandler(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public void addToRequestQueue(Request<IDataModel> req) {
        getRequestQueue().add(req);
    }
}
