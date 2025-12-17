package com.msi.android.ui.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.msi.android.data.api.TokenManager;
import com.msi.android.data.dto.ResultDto;
import com.msi.android.data.dto.TokenResponseDto;
import com.msi.android.data.repository.AuthRepository;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;
    private final TokenManager tokenManager;

    private final MutableLiveData<ResultDto<Void>> codeState = new MutableLiveData<>();
    private final MutableLiveData<ResultDto<TokenResponseDto>> loginState = new MutableLiveData<>();

    /** Запрос кода на почту/телефон */
    public void requestCode(String identifier) {
        codeState.setValue(ResultDto.loading());

        repository.requestCode(identifier)
                .observeForever(result -> codeState.setValue(result));
        // ⚠️ В будущем можно переписать репозиторий под suspend/корутины или callback
    }

    /** Вход с кодом */
    public void login(String identifier, String code) {
        loginState.setValue(ResultDto.loading());

        repository.login(identifier, code)
                .observeForever(result -> {
                    if (result.getStatus() == ResultDto.Status.SUCCESS && result.getData() != null) {
                        TokenResponseDto token = result.getData();

                        // Сохраняем токены
                        tokenManager.saveAccessToken(token.getAccessToken());

                        // Пока refresh token в ответе нет, но метод готов
                        if (token.getRefreshToken() != null) {
                            tokenManager.saveRefreshToken(token.getRefreshToken());
                        }

                        // Отправляем SUCCESS в UI
                        loginState.setValue(ResultDto.success(token));
                    } else if (result.getStatus() == ResultDto.Status.ERROR) {
                        loginState.setValue(ResultDto.error(result.getMessage()));
                    }
                });
    }

    public LiveData<ResultDto<Void>> getCodeState() {
        return codeState;
    }

    public LiveData<ResultDto<TokenResponseDto>> getLoginState() {
        return loginState;
    }
}
