package com.msi.android.ui.helper;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.msi.android.R;
import com.msi.android.ui.view.ProjectDetailsViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProjectInfoHelper {

    public static void loadAndBindProjectInfo(
            View view,
            String projectId,
            String startDate,
            String currentStage,
            ViewModelProvider.Factory viewModelFactory,
            ViewModelStoreOwner owner,
            LifecycleOwner lifecycleOwner) {

        if (projectId == null) return;

        // Установить дату начала сразу
        setStartDate(view, startDate);

        // Загрузить проект через ViewModel
        ProjectDetailsViewModel viewModel = new ViewModelProvider(owner, viewModelFactory)
                .get(ProjectDetailsViewModel.class);

        viewModel.getProject().observe(lifecycleOwner, result -> {
            switch (result.getStatus()) {
                case SUCCESS:
                    if (result.getData() != null) {
                        TextView tvProjectName = view.findViewById(R.id.tv_info_project_name);
                        TextView tvProjectArea = view.findViewById(R.id.tv_info_project_area);
                        TextView tvCurrentStage = view.findViewById(R.id.tv_info_current_stage);

                        if (tvProjectName != null) {
                            tvProjectName.setText(result.getData().getTitle());
                        }

                        if (tvProjectArea != null) {
                            tvProjectArea.setText(result.getData().getArea() + " м²");
                        }

                        if (tvCurrentStage != null) {
                            tvCurrentStage.setText(currentStage);
                        }
                    }
                    break;
                case ERROR:
                    Log.e("ProjectInfoHelper", "Error: " + result.getMessage());
                    break;
            }
        });

        viewModel.loadProject(projectId);
    }

    public static void setStartDate(View view, String startDate) {
        TextView tvStartDate = view.findViewById(R.id.tv_info_start_date);
        if (tvStartDate != null) {
            tvStartDate.setText(formatDate(startDate));
        }
    }

    public static String formatDate(String date) {
        if (date == null) return "";

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("ru"));

        try {
            Date parsedDate = inputFormat.parse(date);
            if (parsedDate != null) {
                return outputFormat.format(parsedDate);
            }
        } catch (ParseException e) {
            Log.e("ProjectInfoHelper", "Error parsing date", e);
        }

        return date;
    }
}
