package co.avinash.targetgitrepos.module;

import co.avinash.targetgitrepos.adapter.TrendingRepoDataAdapter;
import co.avinash.targetgitrepos.presenter.TrendingRepoPresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    public TrendingRepoPresenter provideTrendingRepoPresenter() {
        return new TrendingRepoPresenter();
    }

    @Provides
    public TrendingRepoDataAdapter provideTrendingRepoAdapter() {
        return new TrendingRepoDataAdapter();
    }
}
