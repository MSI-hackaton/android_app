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
import androidx.navigation.fragment.NavHostFragment;

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

public class ConstructionFragment extends Fragment {

    @Inject
    ApiService apiService;

    @Inject
    TokenManager tokenManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private String constructionStageId;

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
        return inflater.inflate(R.layout.fragment_construction_stage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Загрузить информацию о проекте
        loadProjectInfo(view);

        // Обработчики быстрых действий
        view.findViewById(R.id.card_documents).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.documentsFragement);
        });

        view.findViewById(R.id.card_video).setOnClickListener(v -> {
            openVideoStream();
        });

        view.findViewById(R.id.card_chat).setOnClickListener(v -> {
            if (constructionStageId != null) {
                Bundle args = new Bundle();
                args.putString("constructionId", constructionStageId);
                NavHostFragment.findNavController(this).navigate(R.id.chatFragment, args);
            } else {
                Toast.makeText(getContext(), "Проект не загружен", Toast.LENGTH_SHORT).show();
            }
        });

        // Обработчик кнопки "Смотреть трансляцию"
        view.findViewById(R.id.btn_watch_stream).setOnClickListener(v -> {
            openVideoStream();
        });

        // Настройка кнопок навигации
        StageNavigationHelper.setupStageButtons(this, view);
    }

    private void openVideoStream() {
        if (constructionStageId != null) {
            Bundle args = new Bundle();
            args.putString("constructionId", constructionStageId);
            NavHostFragment.findNavController(this).navigate(R.id.videoStreamFragment, args);
        } else {
            Toast.makeText(getContext(), "ID проекта не найден", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProjectInfo(View view) {
        String userId = tokenManager.getUserId();
        if (userId == null) {
            return;
        }

        TextView tvProjectName = view.findViewById(R.id.tv_project_name);
        TextView tvProjectDescription = view.findViewById(R.id.tv_project_description);
        TextView tvProjectStatus = view.findViewById(R.id.tv_project_status);
        TextView tvProjectDates = view.findViewById(R.id.tv_project_dates);

        apiService.getConstructionStagesByCustomer(userId)
                .enqueue(new Callback<List<ConstructionStageResponseDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ConstructionStageResponseDto>> call,
                                           @NonNull Response<List<ConstructionStageResponseDto>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            ConstructionStageResponseDto stage = response.body().get(0);
                            constructionStageId = stage.getId();

                            // Загрузить и заполнить карточку с информацией о проекте
                            ProjectInfoHelper.loadAndBindProjectInfo(
                                    view,
                                    stage.getProjectId(),
                                    stage.getStartDate(),
                                    "Строительство",
                                    viewModelFactory,
                                    ConstructionFragment.this,
                                    getViewLifecycleOwner()
                            );

                            // Заполнить данные верхней карточки
                            tvProjectName.setText(stage.getName());
                            if (stage.getDescription() != null && !stage.getDescription().isEmpty()) {
                                tvProjectDescription.setText(stage.getDescription());
                                tvProjectDescription.setVisibility(View.VISIBLE);
                            } else {
                                tvProjectDescription.setVisibility(View.GONE);
                            }

                            tvProjectStatus.setText(formatStatus(stage.getStatus()));
                            tvProjectDates.setText(formatDates(stage.getStartDate(), stage.getEndDate()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ConstructionStageResponseDto>> call, @NonNull Throwable t) {
                        Log.e("ConstructionFragment", "Error loading project info", t);
                    }
                });
    }

    private String formatStatus(String status) {
        if (status == null) return "";
        switch (status) {
            case "PLANNED":
                return "Согласование документации";
            case "IN_PROGRESS":
                return "Строительство";
            case "COMPLETED":
                return "Завершение строительства";
            default:
                return status;
        }
    }

    private String formatDates(String startDate, String endDate) {
        if (startDate == null || endDate == null) {
            return "";
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        try {
            Date start = inputFormat.parse(startDate);
            Date end = inputFormat.parse(endDate);
            if (start != null && end != null) {
                return outputFormat.format(start) + " – " + outputFormat.format(end);
            }
        } catch (ParseException e) {
            Log.e("ConstructionFragment", "Error parsing dates", e);
        }

        return startDate + " – " + endDate;
    }
}
