package com.msi.android.di;

import android.content.Context;

import com.msi.android.ui.activity.MainActivity;
import com.msi.android.ui.fragment.AcceptanceFragment;
import com.msi.android.ui.fragment.AuthFragment;
import com.msi.android.ui.fragment.ChatFragment;
import com.msi.android.ui.fragment.ConstructionFragment;
import com.msi.android.ui.fragment.DocumentsFragment;
import com.msi.android.ui.fragment.FinalReportFragment;
import com.msi.android.ui.fragment.NavigationBar;
import com.msi.android.ui.fragment.PreparationStageFragment;
import com.msi.android.ui.fragment.ProfileFragment;
import com.msi.android.ui.fragment.OrderProjectFragment;
import com.msi.android.ui.fragment.ProjectDetailsFragment;
import com.msi.android.ui.fragment.ProjectsListFragment;
import com.msi.android.ui.fragment.SignActFragment;
import com.msi.android.ui.fragment.VideoStreamFragment;
import com.msi.android.ui.fragment.WarrantyFragment;

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

    void inject(PreparationStageFragment fragment);

    void inject(ConstructionFragment fragment);

    void inject(VideoStreamFragment fragment);

    void inject(AcceptanceFragment fragment);

    void inject(WarrantyFragment fragment);

    void inject(FinalReportFragment fragment);

    void inject(DocumentsFragment fragment);

    void inject(SignActFragment fragment);

    void inject(ProfileFragment fragment);

    void inject(ChatFragment fragment);
    void inject(OrderProjectFragment fragment);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder applicationContext(Context context);

        AppComponent build();
    }
}
