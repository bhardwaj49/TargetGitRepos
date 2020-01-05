package co.avinash.targetgitrepos.component;

import co.avinash.targetgitrepos.TrendingRepoApplication;
import co.avinash.targetgitrepos.module.AppModule;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Component(modules = {AppModule.class, AndroidInjectionModule.class})
public interface ApplicationComponent extends AndroidInjector<TrendingRepoApplication> {
}
