package com.msi.android.data.mapper;

import com.msi.android.data.dto.PhotoDto;
import com.msi.android.data.entity.PhotoEntity;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PhotoMapper {
    PhotoMapper INSTANCE = Mappers.getMapper(PhotoMapper.class);

    PhotoEntity dtoToEntity(PhotoDto dto);
    PhotoDto entityToDto(PhotoEntity entity);
}
