package co.avinash.targetgitrepos.listeners;

import java.util.List;
import co.avinash.targetgitrepos.model.TrendingRepoModel;

public interface TrendingRepoDataSortListener {

    void trendingRepoDataSorted(List<TrendingRepoModel> trendingRepoModels);
}