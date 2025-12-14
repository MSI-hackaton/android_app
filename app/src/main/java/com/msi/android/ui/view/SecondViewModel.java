package com.msi.android.ui.view;

import androidx.lifecycle.ViewModel;

import com.msi.android.data.api.ApiService;

import javax.inject.Inject;

public class SecondViewModel extends ViewModel {
    @Inject
    public SecondViewModel(ApiService apiService) {
        // Конструктор с зависимостями
    }
}
