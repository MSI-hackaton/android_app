package com.msi.android.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.msi.android.R;
import com.msi.android.ui.activity.MainActivity;
import com.msi.android.ui.activity.SecondActivity;

public class NavigationBar extends Fragment {

    private static final String PREFS_NAME = "BottomNavPrefs";
    private static final String ACTIVE_BUTTON_KEY = "activeButton";

    private View homeCircle, catalogCircle, chatCircle, profileCircle;
    private TextView homeText, catalogText, chatText, profileText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация элементов
        homeCircle = view.findViewById(R.id.homeCircle);
        catalogCircle = view.findViewById(R.id.catalogCircle);
        chatCircle = view.findViewById(R.id.chatCircle);
        profileCircle = view.findViewById(R.id.profileCircle);

        homeText = view.findViewById(R.id.homeText);
        catalogText = view.findViewById(R.id.catalogText);
        chatText = view.findViewById(R.id.chatText);
        profileText = view.findViewById(R.id.profileText);

        // Обработка нажатия на кнопку "Главная"
        view.findViewById(R.id.homeButton).setOnClickListener(v -> {
            setActiveButton("home");
            saveActiveButton("home");
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        // Обработка нажатия на кнопку "Каталог"
        view.findViewById(R.id.catalogButton).setOnClickListener(v -> {
            setActiveButton("catalog");
            saveActiveButton("catalog");
            // Логика для кнопки "Каталог"
        });

        // Обработка нажатия на кнопку "Чат"
        view.findViewById(R.id.chatButton).setOnClickListener(v -> {
            setActiveButton("chat");
            saveActiveButton("chat");
            // Логика для кнопки "Чат"
        });

        // Обработка нажатия на кнопку "Личный кабинет"
        view.findViewById(R.id.profileButton).setOnClickListener(v -> {
            setActiveButton("profile");
            saveActiveButton("profile");
            Intent intent = new Intent(getActivity(), SecondActivity.class);
            startActivity(intent);
        });

        // Восстановить активную кнопку
        String activeButton = getActiveButton();
        if (activeButton.isEmpty()) {
            activeButton = "home"; // По умолчанию активна кнопка "Главная"
        }
        setActiveButton(activeButton);
    }

    // Метод для установки активной кнопки
    private void setActiveButton(String activeButton) {
        // Сбросить все кнопки в неактивное состояние
        homeCircle.setBackgroundResource(R.drawable.rounded_gray_background);
        catalogCircle.setBackgroundResource(R.drawable.rounded_gray_background);
        chatCircle.setBackgroundResource(R.drawable.rounded_gray_background);
        profileCircle.setBackgroundResource(R.drawable.rounded_gray_background);

        homeText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        catalogText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        chatText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        profileText.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Установить активную кнопку
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

    // Метод для сохранения активной кнопки
    private void saveActiveButton(String buttonName) {
        if (getActivity() != null) {
            getActivity().getSharedPreferences(PREFS_NAME, 0)
                    .edit()
                    .putString(ACTIVE_BUTTON_KEY, buttonName)
                    .apply();
        }
    }

    // Метод для получения активной кнопки
    private String getActiveButton() {
        if (getActivity() != null) {
            return getActivity().getSharedPreferences(PREFS_NAME, 0)
                    .getString(ACTIVE_BUTTON_KEY, "");
        }
        return "";
    }
}
