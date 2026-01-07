package com.msi.android.di;

import android.content.Context;

import com.msi.android.ui.activity.MainActivity;
import com.msi.android.ui.fragment.AuthFragment;
import com.msi.android.ui.fragment.NavigationBar;
import com.msi.android.ui.fragment.ProfileFragment;
import com.msi.android.ui.fragment.ProjectDetailsFragment;
import com.msi.android.ui.fragment.ProjectsListFragment;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, ViewModelModule.class, NetworkModule.class, TokenModule.class})
public interface AppComponent {

    void inject(MainActivity activity);
    void inject(NavigationBar bar);
    void inject(AuthFragment fragment);
    void inject(ProjectDetailsFragment fragment);
    void inject(ProjectsListFragment fragment);

    void inject(ProfileFragment fragment);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder applicationContext(Context context);

        AppComponent build();
    }
}
