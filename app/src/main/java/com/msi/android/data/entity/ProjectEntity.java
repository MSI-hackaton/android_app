package com.msi.android.data.entity;

import lombok.Data;

@Data
public class ProjectEntity {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private int price;

}
