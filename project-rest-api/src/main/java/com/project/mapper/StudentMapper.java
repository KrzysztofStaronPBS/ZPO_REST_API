package com.project.mapper;

import com.project.dto.StudentDTO;
import com.project.model.Role;
import com.project.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { ProjektMapper.class })
public interface StudentMapper {
    StudentDTO studentToStudentDTO(Student student);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "projekty", ignore = true)
    Student studentDTOToStudent(StudentDTO studentDTO);

    default String map(Role role) {
        return role != null ? role.getName() : null;
    }
}