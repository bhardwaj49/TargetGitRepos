package co.avinash.targetgitrepos;

import android.app.Application;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.avinash.targetgitrepos.component.DaggerApplicationComponent;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

@Singleton
public class TrendingRepoApplication extends Application implements HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerApplicationComponent.create().inject(this);
    }

    @Override
    public DispatchingAndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}
