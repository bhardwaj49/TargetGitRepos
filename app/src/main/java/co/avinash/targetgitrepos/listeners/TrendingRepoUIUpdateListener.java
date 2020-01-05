package co.avinash.targetgitrepos.listeners;

import java.util.List;

import co.avinash.targetgitrepos.model.TrendingRepoModel;

public interface TrendingRepoUIUpdateListener {

    void updateRepoDataOnUi(List<TrendingRepoModel> trendingRepoModels);

    void updateDataFetchFailedOnUi();
}
