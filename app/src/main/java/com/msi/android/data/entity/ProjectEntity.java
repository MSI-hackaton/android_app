package com.msi.android.data.entity;

import java.util.List;

import lombok.Data;

@Data
public class ProjectEntity {
    private String id;
    private String title;
    private String description;
    private double area;
    private int floors;
    private int constructionTime;

    private double price;
    private String status;

    private List<PhotoEntity> photos;

    public String getPreviewPhoto() {
        return (photos != null && !photos.isEmpty())
                ? photos.get(0).getUrl()
                : null;
    }

}
