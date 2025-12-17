package com.msi.android.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.msi.android.data.api.AuthApiService;
import com.msi.android.data.dto.CodeRequestDto;
import com.msi.android.data.dto.LoginRequestDto;
import com.msi.android.data.dto.ResultDto;
import com.msi.android.data.dto.TokenResponseDto;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.RequiredArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AuthRepository {

    private final AuthApiService api;

    public LiveData<ResultDto<Void>> requestCode(String identifier) {
        MutableLiveData<ResultDto<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(ResultDto.loading());

        CodeRequestDto dto = new CodeRequestDto();
        dto.setIdentifier(identifier);

        api.code(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(ResultDto.success(null));
                } else {
                    liveData.setValue(ResultDto.error("Ошибка отправки кода"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                liveData.setValue(ResultDto.error("Сеть недоступна"));
            }
        });

        return liveData;
    }

    public LiveData<ResultDto<TokenResponseDto>> login(String identifier, String code) {
        MutableLiveData<ResultDto<TokenResponseDto>> liveData = new MutableLiveData<>();
        liveData.setValue(ResultDto.loading());

        LoginRequestDto dto = new LoginRequestDto();
        dto.setIdentifier(identifier);
        dto.setCode(code);

        api.login(dto).enqueue(new Callback<TokenResponseDto>() {
            @Override
            public void onResponse(Call<TokenResponseDto> call, Response<TokenResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(ResultDto.success(response.body()));
                } else {
                    liveData.setValue(ResultDto.error("Неверный код"));
                }
            }

            @Override
            public void onFailure(Call<TokenResponseDto> call, Throwable t) {
                liveData.setValue(ResultDto.error("Ошибка сети"));
            }
        });

        return liveData;
    }
}
