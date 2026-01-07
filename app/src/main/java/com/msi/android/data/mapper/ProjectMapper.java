package com.msi.android.data.mapper;

import com.msi.android.data.dto.ProjectDto;
import com.msi.android.data.entity.ProjectEntity;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = PhotoMapper.class)
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    ProjectEntity dtoToEntity(ProjectDto dto);
    ProjectDto entityToDto(ProjectEntity entity);
}
