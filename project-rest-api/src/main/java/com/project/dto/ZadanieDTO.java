package com.project.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Getter
@Setter
@Relation(collectionRelation = "zadania", itemRelation = "zadanie")
public class ZadanieDTO extends RepresentationModel<ZadanieDTO> {
    private Integer zadanieId;
    private String nazwa;
    private Integer kolejnosc;
    private String opis;
    private LocalDateTime dataCzasDodania;
    private Integer projektId;
}
