package com.msi.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.api.TokenManager;

import javax.inject.Inject;

public class NavigationBar extends Fragment {

    @Inject
    TokenManager tokenManager;

    private View homeCircle, catalogCircle, chatCircle, profileCircle;
    private TextView homeText, catalogText, chatText, profileText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((App) requireActivity().getApplication())
                .getAppComponent()
                .inject(this);

        homeCircle = view.findViewById(R.id.homeCircle);
        catalogCircle = view.findViewById(R.id.catalogCircle);
        chatCircle = view.findViewById(R.id.chatCircle);
        profileCircle = view.findViewById(R.id.profileCircle);

        homeText = view.findViewById(R.id.homeText);
        catalogText = view.findViewById(R.id.catalogText);
        chatText = view.findViewById(R.id.chatText);
        profileText = view.findViewById(R.id.profileText);

        NavController navController = NavHostFragment.findNavController(
                requireActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment)
        );

        // Главная кнопка
        view.findViewById(R.id.homeButton).setOnClickListener(v -> {
            setActiveButton("home");
            navController.navigate(R.id.projectsListFragment);
        });

        // Каталог (пример, можно добавить навигацию)
        view.findViewById(R.id.catalogButton).setOnClickListener(v -> {
            setActiveButton("catalog");
            // navController.navigate(R.id.catalogFragment);
        });

        // Чат (пример, можно добавить навигацию)
        view.findViewById(R.id.chatButton).setOnClickListener(v -> {
            setActiveButton("chat");
            // navController.navigate(R.id.chatFragment);
        });

        // Профиль / Авторизация
        view.findViewById(R.id.profileButton).setOnClickListener(v -> {
            setActiveButton("profile");
            if (tokenManager.isAuthorized()) {
                navController.navigate(R.id.profileFragment);
            } else {
                navController.navigate(R.id.authFragment);
            }
        });
    }

    private void setActiveButton(@NonNull String activeButton) {
        // Сброс всех кнопок
        homeCircle.setBackgroundResource(R.drawable.rounded_gray_background);
        catalogCircle.setBackgroundResource(R.drawable.rounded_gray_background);
        chatCircle.setBackgroundResource(R.drawable.rounded_gray_background);
        profileCircle.setBackgroundResource(R.drawable.rounded_gray_background);

        homeText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        catalogText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        chatText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        profileText.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Установка активной кнопки
        switch (activeButton) {
            case "home":
                homeCircle.setBackgroundResource(R.drawable.rounded_blue_background);
                homeText.setTextColor(getResources().getColor(R.color.blue));
                break;
            case "catalog":
                catalogCircle.setBackgroundResource(R.drawable.rounded_blue_background);
                catalogText.setTextColor(getResources().getColor(R.color.blue));
                break;
            case "chat":
                chatCircle.setBackgroundResource(R.drawable.rounded_blue_background);
                chatText.setTextColor(getResources().getColor(R.color.blue));
                break;
            case "profile":
                profileCircle.setBackgroundResource(R.drawable.rounded_blue_background);
                profileText.setTextColor(getResources().getColor(R.color.blue));
                break;
        }
    }
}
