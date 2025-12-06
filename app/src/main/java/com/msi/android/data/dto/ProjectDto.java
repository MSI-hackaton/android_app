package com.msi.android.data.dto;

import lombok.Data;

@Data
public class ProjectDto {
    public String id;
    public String title;
    public String description;
    public String imageUrl;
    public int price;
}
