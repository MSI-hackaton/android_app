package com.msi.android.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.ui.view.AuthViewModel;
import com.msi.android.data.dto.ResultDto;

import javax.inject.Inject;

public class AuthFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private AuthViewModel viewModel;

    private EditText identifierInput;
    private EditText codeInput;
    private Button actionButton;
    private ProgressBar progressBar;

    private boolean codeRequested = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) requireActivity().getApplication())
                .getAppComponent()
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        identifierInput = view.findViewById(R.id.identifierInput);
        codeInput = view.findViewById(R.id.codeInput);
        actionButton = view.findViewById(R.id.actionButton);
        progressBar = view.findViewById(R.id.progressBar);

        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(AuthViewModel.class);

        actionButton.setOnClickListener(v -> {
            String identifier = identifierInput.getText().toString().trim();
            if (TextUtils.isEmpty(identifier)) {
                Toast.makeText(getContext(), "Введите email или телефон", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!codeRequested) {
                viewModel.requestCode(identifier);
            } else {
                String code = codeInput.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(getContext(), "Введите код", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.login(identifier, code);
            }
        });

        observeViewModel();
    }

    private void observeViewModel() {

        // Наблюдаем за состоянием запроса кода
        viewModel.getCodeState().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.getStatus()) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    codeRequested = true;
                    codeInput.setVisibility(View.VISIBLE);
                    actionButton.setText("Отправить");
                    Toast.makeText(getContext(), "Код отправлен", Toast.LENGTH_SHORT).show();
                    break;

                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), result.getMessage() != null ? result.getMessage() : "Ошибка запроса кода", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // Наблюдаем за состоянием логина
        viewModel.getLoginState().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.getStatus()) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Успешный вход", Toast.LENGTH_SHORT).show();

                    // Переход на профиль и удаление AuthFragment из back stack
                    NavHostFragment.findNavController(this)
                            .navigate(
                                    R.id.profileFragment,
                                    null,
                                    new NavOptions.Builder()
                                            .setPopUpTo(R.id.authFragment, true)
                                            .build()
                            );
                    break;

                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), result.getMessage() != null ? result.getMessage() : "Ошибка входа", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
