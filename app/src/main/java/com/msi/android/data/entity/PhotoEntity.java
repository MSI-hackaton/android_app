package com.msi.android.data.entity;

import lombok.Data;

@Data
public class PhotoEntity {
    private String id;
    private String description;
    private int sortOrder;
    private String url;
}
