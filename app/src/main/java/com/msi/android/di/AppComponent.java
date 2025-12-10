package com.msi.android.di;

import android.content.Context;

import com.msi.android.ui.activity.MainActivity;
import com.msi.android.ui.view.ViewModelModule;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, ViewModelModule.class, NetworkModule.class, TokenModule.class})
public interface AppComponent {
    void inject(MainActivity activity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder applicationContext(Context context);

        AppComponent build();
    }
}
