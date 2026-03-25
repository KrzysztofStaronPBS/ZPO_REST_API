package com.project.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Relation(collectionRelation = "projekty", itemRelation = "projekt")
public class ProjektDTO extends RepresentationModel<ProjektDTO> {
    private Integer projektId;
    private String nazwa;
    private String opis;
    private LocalDateTime dataCzasUtworzenia;
    private LocalDate dataOddania;
}