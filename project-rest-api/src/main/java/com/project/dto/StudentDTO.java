package com.project.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import java.util.Set;

@Getter
@Setter
@Relation(collectionRelation = "studenci", itemRelation = "student")
public class StudentDTO extends RepresentationModel<StudentDTO> {
    private Integer studentId;
    private String imie;
    private String nazwisko;
    private String nrIndeksu;
    private String email;
    private Boolean stacjonarny;
    private Set<ProjektDTO> projekty;
    private String password;
    private Set<String> roles;
}