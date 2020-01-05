package co.avinash.targetgitrepos;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.avinash.targetgitrepos.datahandler.TrendingRepoDataHandler;
import co.avinash.targetgitrepos.listeners.TrendingRepoDataFetchListener;
import co.avinash.targetgitrepos.model.TrendingRepoApiResponseModel;
import co.avinash.targetgitrepos.model.TrendingRepoModel;
import co.avinash.targetgitrepos.utils.TrendingRepoUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TrendingRepoUnitTest {

    private Context mContext;

    private TrendingRepoDataHandler mTrendingRepoDataHandler;

    @Before
    public void setup() {
        this.mContext = Mockito.mock(Context.class);
        this.mTrendingRepoDataHandler = new TrendingRepoDataHandler(mContext, new TrendingRepoDataFetchListener() {
            @Override
            public void trendingRepoDataFecthed(List<TrendingRepoModel> trendingRepoModels) {

            }

            @Override
            public void trendingRepoDataFetchFailed() {

            }
        });
    }

    @Test
    public void testIsCachedDataExpired() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date dataDonwloadedAt = calendar.getTime();

        assertTrue("Cached data is expired ", TrendingRepoUtils.isDataExpired(dataDonwloadedAt));
    }

    @Test
    public void testIsCachedDataNotExpired() {
        Date dataDownloadTimestamp = getDataDownloadTimestamp();

        assertFalse("Cached data is not expired ", TrendingRepoUtils.isDataExpired(dataDownloadTimestamp));
    }

    @Test
    public void testIsCachedDataExpiredWithInvalidDownloadDataTimestamp() {
        assertFalse("There's no cached data ", TrendingRepoUtils.isDataExpired(null));
    }

    @Test
    public void testDataFetchedFailed() {
        boolean forceFetchData = false;

        Date dataDownloadTimestamp = getDataDownloadTimestamp();

        mTrendingRepoDataHandler.fetchTrendingRepoData(forceFetchData, dataDownloadTimestamp);
        assertTrue("Fetching Trending repo data failed ", mTrendingRepoDataHandler.getTrendingRepos() == null);
    }

    private Date getDataDownloadTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -1);
        Date dataDownloadTimestamp = calendar.getTime();
        return dataDownloadTimestamp;
    }

    @Test
    public void testDataFetched() {
        TrendingRepoApiResponseModel apiResponseModel = getTrendingRepoMockApiData();

        Date dataDownloadTimestamp = getDataDownloadTimestamp();

        mTrendingRepoDataHandler.fetchTrendingRepoData(true, dataDownloadTimestamp);

        try {
            Thread.sleep(2000);
            mTrendingRepoDataHandler.onResponse(apiResponseModel);
        } catch (InterruptedException e) {
            Log.e("", "Exception received while waiting for response");
        }

        assertTrue("Trending  repo list is not empty ", mTrendingRepoDataHandler.getTrendingRepos() != null);
        assertTrue("Trending repo list lenght is 1 ", mTrendingRepoDataHandler.getTrendingRepos().size() == 1);
    }

    private TrendingRepoApiResponseModel getTrendingRepoMockApiData() {
        String dataString = "[{\n" +
                "    \"author\": \"github\",\n" +
                "    \"name\": \"CodeSearchNet\",\n" +
                "    \"avatar\": \"https://github.com/github.png\",\n" +
                "    \"url\": \"https://github.com/github/CodeSearchNet\",\n" +
                "    \"description\": \"Datasets, tools, and benchmarks for representation learning of code.\",\n" +
                "    \"language\": \"Jupyter Notebook\",\n" +
                "    \"languageColor\": \"#DA5B0B\",\n" +
                "    \"stars\": 393,\n" +
                "    \"forks\": 49,\n" +
                "    \"currentPeriodStars\": 106,\n" +
                "    \"builtBy\": [\n" +
                "      {\n" +
                "        \"username\": \"hamelsmu\",\n" +
                "        \"href\": \"https://github.com/hamelsmu\",\n" +
                "        \"avatar\": \"https://avatars3.githubusercontent.com/u/1483922\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"username\": \"raubitsj\",\n" +
                "        \"href\": \"https://github.com/raubitsj\",\n" +
                "        \"avatar\": \"https://avatars0.githubusercontent.com/u/1832511\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"username\": \"staceysv\",\n" +
                "        \"href\": \"https://github.com/staceysv\",\n" +
                "        \"avatar\": \"https://avatars1.githubusercontent.com/u/45573465\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"username\": \"shawnlewis\",\n" +
                "        \"href\": \"https://github.com/shawnlewis\",\n" +
                "        \"avatar\": \"https://avatars2.githubusercontent.com/u/499383\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"username\": \"bzz\",\n" +
                "        \"href\": \"https://github.com/bzz\",\n" +
                "        \"avatar\": \"https://avatars3.githubusercontent.com/u/5582506\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }]";

        List<TrendingRepoModel> trendingRepoModelList =
                Arrays.asList(new Gson().fromJson(dataString, TrendingRepoModel[].class));
        final TrendingRepoApiResponseModel apiResponseModel = new TrendingRepoApiResponseModel();
        apiResponseModel.setTrendingRepoModels(trendingRepoModelList);
        return apiResponseModel;
    }

}