package com.msi.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.entity.ProjectEntity;
import com.msi.android.ui.adapter.ProjectAdapter;
import com.msi.android.ui.view.MainViewModel;

import java.util.List;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private MainViewModel viewModel;
    private ProjectAdapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ❗ ВАЖНО: обычный Dagger — без AndroidInjection
        ((App) getApplication()).getAppComponent().inject(this);

        setContentView(R.layout.activity_main);

        initUi();
        initViewModel();
        observeProjects();

        viewModel.loadProjects();
    }

    private void initUi() {
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        adapter = new ProjectAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(MainViewModel.class);
    }

    private void observeProjects() {
        viewModel.getProjects().observe(this, result -> {
            switch (result.getStatus()) {

                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    updateList(result.getData());
                    break;

                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void updateList(List<ProjectEntity> projects) {
        adapter.submitList(projects);
    }
}


