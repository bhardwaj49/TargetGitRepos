package co.avinash.targetgitrepos.component;

import javax.inject.Singleton;

import co.avinash.targetgitrepos.module.AppModule;
import co.avinash.targetgitrepos.views.TrendingRepoHomeActivity;
import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface ApplicationComponent {

    void inject(TrendingRepoHomeActivity homeActivity);
}
