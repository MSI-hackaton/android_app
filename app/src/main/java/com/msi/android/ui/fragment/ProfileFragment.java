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

import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.api.ApiService;
import com.msi.android.data.api.TokenManager;
import com.msi.android.data.dto.ConstructionStageResponseDto;

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

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

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

        LinearLayout projectInfoContainer = view.findViewById(R.id.project_info_container);
        TextView tvNoProject = view.findViewById(R.id.tv_no_project);
        TextView tvProjectName = view.findViewById(R.id.tv_project_name);
        TextView tvProjectDescription = view.findViewById(R.id.tv_project_description);
        TextView tvProjectStatus = view.findViewById(R.id.tv_project_status);
        TextView tvProjectDates = view.findViewById(R.id.tv_project_dates);

        String userId = getUserId();
        Log.d("ProfileFragment", "userId: " + userId);

        if (userId != null) {
            loadProjectInfo(userId, projectInfoContainer, tvNoProject, tvProjectName,
                    tvProjectDescription, tvProjectStatus, tvProjectDates);
        } else {
            Log.e("ProfileFragment", "userId is NULL!");
            projectInfoContainer.setVisibility(View.GONE);
            tvNoProject.setVisibility(View.VISIBLE);
        }
    }

    private void loadProjectInfo(String userId, LinearLayout container, TextView tvNoProject,
                                 TextView tvName, TextView tvDescription, TextView tvStatus, TextView tvDates) {
        Log.d("ProfileFragment", "Loading project info for userId: " + userId);

        apiService.getConstructionStagesByCustomer(userId)
                .enqueue(new Callback<List<ConstructionStageResponseDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ConstructionStageResponseDto>> call,
                                           @NonNull Response<List<ConstructionStageResponseDto>> response) {
                        Log.d("ProfileFragment", "Response code: " + response.code());

                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Log.d("ProfileFragment", "Stages count: " + response.body().size());
                            ConstructionStageResponseDto stage = response.body().get(0);

                            container.setVisibility(View.VISIBLE);
                            tvNoProject.setVisibility(View.GONE);

                            tvName.setText(stage.getName());

                            if (stage.getDescription() != null && !stage.getDescription().isEmpty()) {
                                tvDescription.setText(stage.getDescription());
                                tvDescription.setVisibility(View.VISIBLE);
                            } else {
                                tvDescription.setVisibility(View.GONE);
                            }

                            tvStatus.setText(formatStatus(stage.getStatus()));

                            tvDates.setText(formatDates(stage.getStartDate(), stage.getEndDate()));
                        } else {
                            Log.d("ProfileFragment", "Response not successful or empty");
                            container.setVisibility(View.GONE);
                            tvNoProject.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ConstructionStageResponseDto>> call, @NonNull Throwable t) {
                        Log.e("ProfileFragment", "Request failed", t);
                        container.setVisibility(View.GONE);
                        tvNoProject.setVisibility(View.VISIBLE);
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
            Log.e("ProfileFragment", "Error parsing dates", e);
        }

        return startDate + " – " + endDate;
    }


    private String getUserId() {
        return tokenManager.getUserId();
    }
}
