package co.avinash.targetgitrepos;

import android.app.Application;

import co.avinash.targetgitrepos.component.ApplicationComponent;
import co.avinash.targetgitrepos.component.DaggerApplicationComponent;
import co.avinash.targetgitrepos.module.AppModule;

public class TrendingRepoApplication extends Application {

    ApplicationComponent applicationComponent = DaggerApplicationComponent.builder().appModule(new AppModule()).build();

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
