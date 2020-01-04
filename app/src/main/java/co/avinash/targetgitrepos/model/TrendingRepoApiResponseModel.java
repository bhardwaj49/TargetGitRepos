package co.avinash.targetgitrepos.model;

import java.util.List;

public class TrendingRepoApiResponseModel implements IDataModel {

    private List<TrendingRepoModel> trendingRepoModels;

    public List<TrendingRepoModel> getTrendingRepoModels() {
        return trendingRepoModels;
    }

    public void setTrendingRepoModels(List<TrendingRepoModel> trendingRepoModels) {
        this.trendingRepoModels = trendingRepoModels;
    }
}
