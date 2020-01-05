package co.avinash.targetgitrepos.listeners;

import java.util.List;
import co.avinash.targetgitrepos.model.TrendingRepoModel;

public interface TrendingRepoDataFetchListener {

    void trendingRepoDataFecthed(List<TrendingRepoModel> trendingRepoModels);

    void trendingRepoDataFetchFailed();
}
