package com.msi.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.dto.ProjectRequestBody;
import com.msi.android.ui.view.OrderProjectViewModel;

import javax.inject.Inject;

public class OrderProjectFragment extends Fragment {

    @Inject ViewModelProvider.Factory viewModelFactory;
    private OrderProjectViewModel viewModel;

    private String projectId;

    private EditText inputName, inputPhone, inputEmail, inputComment;
    private CheckBox checkboxAgree;
    private MaterialButton submitButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_project, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((App) requireActivity().getApplication()).getAppComponent().inject(this);

        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(OrderProjectViewModel.class);

        projectId = getArguments().getString("projectId");

        inputName = view.findViewById(R.id.inputName);
        inputPhone = view.findViewById(R.id.inputPhone);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputComment = view.findViewById(R.id.inputComment);

        checkboxAgree = view.findViewById(R.id.checkboxAgree);
        submitButton = view.findViewById(R.id.submitButton);

        checkboxAgree.setOnCheckedChangeListener((btn, checked) -> {
            submitButton.setEnabled(checked);
        });

        submitButton.setOnClickListener(v -> sendRequest());
    }

    private void sendRequest() {

        ProjectRequestBody body = new ProjectRequestBody(
                inputName.getText().toString(),
                inputEmail.getText().toString(),
                inputPhone.getText().toString(),
                inputComment.getText().toString()
        );

        viewModel.sendOrder(projectId, body).observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    submitButton.setEnabled(false);
                    submitButton.setText("Отправка...");
                    break;
                case SUCCESS:
                    Toast.makeText(getContext(), "Заявка отправлена", Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                    break;
                case ERROR:
                    submitButton.setEnabled(true);
                    submitButton.setText("Отправить");
                    Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}
