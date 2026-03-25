package com.project.controller;

import com.project.dto.ProjektDTO;
import com.project.mapper.ProjektMapper;
import com.project.model.Projekt;
import com.project.service.ProjektService;
import com.project.validation.ValidationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Projekt")
public class ProjektRestController {

    private final ProjektService projektService;
    private final ProjektMapper projektMapper;
    private final ValidationService<Projekt> validator;

    @Autowired
    public ProjektRestController(ProjektService projektService, ProjektMapper projektMapper, ValidationService<Projekt> validator) {
        this.projektService = projektService;
        this.projektMapper = projektMapper;
        this.validator = validator;
    }

    @GetMapping("/projekty/{projektId}")
    public ResponseEntity<EntityModel<ProjektDTO>> getProjekt(@PathVariable Integer projektId) {
        return projektService.getProjekt(projektId)
                .map(projektMapper::projektToProjektDTO)
                .map(this::addHateoasLinks)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/projekty")
    public ResponseEntity<EntityModel<ProjektDTO>> createProjekt(@Valid @RequestBody ProjektDTO projektDto) {
        Projekt projekt = projektMapper.projektDTOToProjekt(projektDto);
        validator.validate(projekt);
        Projekt createdProjekt = projektService.setProjekt(projekt);

        ProjektDTO responseDto = projektMapper.projektToProjektDTO(createdProjekt);
        EntityModel<ProjektDTO> entityModel = addHateoasLinks(responseDto);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @PutMapping("/projekty/{projektId}")
    public ResponseEntity<EntityModel<ProjektDTO>> updateProjekt(@Valid @RequestBody ProjektDTO projektDto,
                                                                 @PathVariable Integer projektId) {
        return projektService.getProjekt(projektId)
                .map(p -> {
                    Projekt projekt = projektMapper.projektDTOToProjekt(projektDto);
                    projekt.setProjektId(projektId);

                    validator.validate(projekt);

                    Projekt updated = projektService.setProjekt(projekt);
                    return ResponseEntity.ok(addHateoasLinks(projektMapper.projektToProjektDTO(updated)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/projekty/{projektId}")
    public ResponseEntity<Void> deleteProjekt(@PathVariable Integer projektId) {
        return projektService.getProjekt(projektId)
                .map(p -> {
                    projektService.deleteProjekt(projektId);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/projekty")
    public ResponseEntity<CollectionModel<EntityModel<ProjektDTO>>> getProjekty(Pageable pageable) {
        Page<Projekt> projektyPage = projektService.getProjekty(pageable);

        List<EntityModel<ProjektDTO>> dtos = projektyPage.getContent().stream()
                .map(projektMapper::projektToProjektDTO)
                .map(this::addHateoasLinks)
                .toList();

        // kolekcja z linkiem do samej siebie (paginacja)
        CollectionModel<EntityModel<ProjektDTO>> collectionModel = CollectionModel.of(dtos);
        collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProjektRestController.class)
                .getProjekty(pageable)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(value = "/projekty", params = "nazwa")
    public ResponseEntity<CollectionModel<EntityModel<ProjektDTO>>> getProjektyByNazwa(
            @RequestParam(name = "nazwa") String nazwa, Pageable pageable) {

        Page<Projekt> projektyPage = projektService.searchByNazwa(nazwa, pageable);

        List<EntityModel<ProjektDTO>> dtos = projektyPage.getContent().stream()
                .map(projektMapper::projektToProjektDTO)
                .map(this::addHateoasLinks)
                .toList();

        CollectionModel<EntityModel<ProjektDTO>> collectionModel = CollectionModel.of(dtos);
        collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProjektRestController.class)
                .getProjektyByNazwa(nazwa, pageable)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/projekty/{projektId}/details")
    public ResponseEntity<EntityModel<ProjektDTO>> getProjektDetails(@PathVariable Integer projektId) {
        return getProjekt(projektId);
    }

    // metoda pomocnicza do budowania linków hateoas
    private EntityModel<ProjektDTO> addHateoasLinks(ProjektDTO dto) {
        return EntityModel.of(dto,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProjektRestController.class)
                        .getProjekt(dto.getProjektId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ZadanieRestController.class)
                        .getZadania(Pageable.unpaged())).withRel("wszystkie_zadania"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProjektRestController.class)
                        .deleteProjekt(dto.getProjektId())).withRel("usun_projekt")
        );
    }
}
