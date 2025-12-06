package com.msi.android.data.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String phone;
    private String otpCode;
}
