package com.msi.android.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectRequestBody {
    private String fullName;
    private String email;
    private String phone;
    private String comment;
}
