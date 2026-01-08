package com.msi.android.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.api.ApiService;
import com.msi.android.data.api.TokenManager;
import com.msi.android.data.dto.ConstructionStageResponseDto;
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

public class ProfileFragment extends Fragment {

    @Inject
    ApiService apiService;

    @Inject
    TokenManager tokenManager;

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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Загрузить информацию о проекте
        loadProjectInfo(view);

        // Настройка кнопок навигации
        StageNavigationHelper.setupStageButtons(this, view);
    }

    private void loadProjectInfo(View view) {
        String userId = tokenManager.getUserId();
        if (userId == null) {
            showNoProject(view);
            return;
        }

        LinearLayout projectInfoContainer = view.findViewById(R.id.project_info_container);
        TextView tvNoProject = view.findViewById(R.id.tv_no_project);
        TextView tvProjectName = view.findViewById(R.id.tv_project_name);
        TextView tvProjectDescription = view.findViewById(R.id.tv_project_description);
        TextView tvProjectStatus = view.findViewById(R.id.tv_project_status);
        TextView tvProjectDates = view.findViewById(R.id.tv_project_dates);
        LinearLayout progressContainer = view.findViewById(R.id.progress_container);
        TextView tvProgressPercent = view.findViewById(R.id.tv_progress_percent);
        LinearProgressIndicator progressBar = view.findViewById(R.id.progress_bar);

        apiService.getConstructionStagesByCustomer(userId)
                .enqueue(new Callback<List<ConstructionStageResponseDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ConstructionStageResponseDto>> call,
                                           @NonNull Response<List<ConstructionStageResponseDto>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            ConstructionStageResponseDto stage = response.body().get(0);



                            projectInfoContainer.setVisibility(View.VISIBLE);
                            tvNoProject.setVisibility(View.GONE);
                            progressContainer.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);

                            tvProjectName.setText(stage.getName());
                            if (stage.getDescription() != null && !stage.getDescription().isEmpty()) {
                                tvProjectDescription.setText(stage.getDescription());
                                tvProjectDescription.setVisibility(View.VISIBLE);
                            } else {
                                tvProjectDescription.setVisibility(View.GONE);
                            }

                            tvProjectStatus.setText(formatStatus(stage.getStatus()));
                            tvProjectDates.setText(formatDates(stage.getStartDate(), stage.getEndDate()));

                            // Установить прогресс (пример)
                            int progress = calculateProgress("zero");
                            tvProgressPercent.setText(progress + "%");
                            progressBar.setProgress(progress);
                        } else {
                            showNoProject(view);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ConstructionStageResponseDto>> call, @NonNull Throwable t) {
                        Log.e("ProfileFragment", "Error loading project info", t);
                        showNoProject(view);
                    }
                });
    }

    private void showNoProject(View view) {
        view.findViewById(R.id.project_info_container).setVisibility(View.GONE);
        view.findViewById(R.id.tv_no_project).setVisibility(View.VISIBLE);
        view.findViewById(R.id.progress_container).setVisibility(View.GONE);
        view.findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }

    private int calculateProgress(String status) {
        if (status == null) return 0;
        switch (status) {
            case "PLANNED":
                return 10;
            case "IN_PROGRESS":
                return 50;
            case "COMPLETED":
                return 100;
            default:
                return 0;
        }
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
            Log.e("ProfileFragment", "Error parsing dates", e);
        }

        return startDate + " – " + endDate;
    }
}
