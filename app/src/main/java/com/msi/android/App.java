package com.msi.android;

import android.app.Application;

import com.msi.android.di.AppComponent;
import com.msi.android.di.DaggerAppComponent;

public class App extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .applicationContext(this)
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}


