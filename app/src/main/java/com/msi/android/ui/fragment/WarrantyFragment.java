package com.msi.android.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.api.ApiService;
import com.msi.android.data.api.TokenManager;
import com.msi.android.data.dto.ConstructionStageResponseDto;
import com.msi.android.ui.helper.ProjectInfoHelper;
import com.msi.android.ui.helper.StageNavigationHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WarrantyFragment extends Fragment {

    @Inject
    ApiService apiService;

    @Inject
    TokenManager tokenManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) requireActivity().getApplication())
                .getAppComponent()
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_warranty, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Загрузить информацию о проекте
        loadProjectInfo(view);

        // Карточка "Документы"
        view.findViewById(R.id.card_documents).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Открыть документы", Toast.LENGTH_SHORT).show();
        });

        // Карточка "Чат"
        view.findViewById(R.id.card_chat).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Открыть чат", Toast.LENGTH_SHORT).show();
        });

        // Настройка кнопок навигации
        StageNavigationHelper.setupStageButtons(this, view);
    }

    private void loadProjectInfo(View view) {
        String userId = tokenManager.getUserId();
        if (userId == null) {
            return;
        }

        apiService.getConstructionStagesByCustomer(userId)
                .enqueue(new Callback<List<ConstructionStageResponseDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ConstructionStageResponseDto>> call,
                                           @NonNull Response<List<ConstructionStageResponseDto>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            ConstructionStageResponseDto stage = response.body().get(0);

                            // Загрузить и заполнить карточку с информацией о проекте
                            ProjectInfoHelper.loadAndBindProjectInfo(
                                    view,
                                    stage.getProjectId(),
                                    stage.getStartDate(),
                                    "Гарантия",
                                    viewModelFactory,
                                    WarrantyFragment.this,
                                    getViewLifecycleOwner()
                            );
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ConstructionStageResponseDto>> call, @NonNull Throwable t) {
                        Log.e("WarrantyFragment", "Error loading project info", t);
                    }
                });
    }
}
