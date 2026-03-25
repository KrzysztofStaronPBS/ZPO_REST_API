package com.project.mapper;

import com.project.dto.ZadanieDTO;
import com.project.model.Zadanie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ZadanieMapper {

    @Mapping(target = "projektId", source = "projekt.projektId")
    ZadanieDTO zadanieToZadanieDTO(Zadanie zadanie);

    @Mapping(target = "projekt.projektId", source = "projektId")
    Zadanie zadanieDTOToZadanie(ZadanieDTO zadanieDTO);
}