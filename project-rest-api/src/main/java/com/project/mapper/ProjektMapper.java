package com.project.mapper;

import com.project.dto.ProjektDTO;
import com.project.model.Projekt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { StudentMapper.class, ZadanieMapper.class })
public interface ProjektMapper {
    ProjektDTO projektToProjektDTO(Projekt projekt);

    @Mapping(target = "zadania", ignore = true)
    @Mapping(target = "studenci", ignore = true)
    Projekt projektDTOToProjekt(ProjektDTO projektDTO);
}