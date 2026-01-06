package com.msi.android.data.dto;

import lombok.Data;

@Data
public class PhotoDto {
    private String id;
    private String description;
    private int sortOrder;
    private String url;
}
