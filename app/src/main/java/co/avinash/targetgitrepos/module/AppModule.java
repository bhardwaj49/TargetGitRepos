package co.avinash.targetgitrepos.module;

import co.avinash.targetgitrepos.views.TrendingRepoHomeActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AppModule {

    @ContributesAndroidInjector
    abstract TrendingRepoHomeActivity contributeActivityInjector();
}
