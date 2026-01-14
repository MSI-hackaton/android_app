package com.msi.android.data.mapper;

import com.msi.android.data.dto.PhotoDto;
import com.msi.android.data.entity.PhotoEntity;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PhotoMapper {
    PhotoMapper INSTANCE = Mappers.getMapper(PhotoMapper.class);

    default PhotoEntity dtoToEntity(PhotoDto dto) {
        if (dto == null) return null;

        PhotoEntity entity = new PhotoEntity();
        entity.setId(dto.getId());
        entity.setDescription(dto.getDescription());
        entity.setSortOrder(dto.getSortOrder());
        entity.setUrl("http://10.0.2.2:8080/" + dto.getUrl()); // полный путь для эмулятора
        return entity;
    }
    PhotoDto entityToDto(PhotoEntity entity);
}
