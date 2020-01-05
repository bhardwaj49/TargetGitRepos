package co.avinash.targetgitrepos.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.util.Arrays;
import java.util.Map;

import co.avinash.targetgitrepos.model.IDataModel;
import co.avinash.targetgitrepos.model.TrendingRepoApiResponseModel;
import co.avinash.targetgitrepos.model.TrendingRepoModel;

public class NetworkRequestObject extends Request<IDataModel> {

    private final Map<String, String> headers;
    private final Listener<IDataModel> mListener;
    private IDataModel mDataModel;
    private final Gson mGson;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param headers Map of request headers
     */
    public NetworkRequestObject(String url, int method, Map<String, String> headers,
                                Listener<IDataModel> listener, ErrorListener errorListener,
                                IDataModel dataModel) {
        super(method, url, errorListener);
        this.headers = headers;
        this.mListener = listener;
        mGson = new Gson();
        mDataModel = dataModel;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(IDataModel response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<IDataModel> parseNetworkResponse(NetworkResponse response) {
        try {
            String encoding = response.headers.get("Content-Encoding");
            Reader reader = null;
            String jsonString = new String(response.data);

            if (response.data != null) {
                if (encoding != null && encoding.equals("gzip")) {
                    reader = GZIPUtils.convertReader(response.data);
                } else jsonString = new String(response.data);
            }

            if (mDataModel instanceof TrendingRepoApiResponseModel) {
                TrendingRepoModel[] trendingRepoModelList =
                        mGson.fromJson(jsonString, TrendingRepoModel[].class);
                if (trendingRepoModelList != null) {
                    TrendingRepoApiResponseModel pictionaryApiResponseModel = new TrendingRepoApiResponseModel();
                    pictionaryApiResponseModel.setTrendingRepoModels(Arrays.asList(trendingRepoModelList));
                    return Response.success((IDataModel) pictionaryApiResponseModel, getCacheEntry());
                }
                return Response.success(mDataModel, getCacheEntry());

            }

        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new NetworkError(e));
        }
        return null;
    }
}
