package com.msi.android.data.dto;

import lombok.Data;

@Data
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String id;
    private String email;
    private String phone;
}
