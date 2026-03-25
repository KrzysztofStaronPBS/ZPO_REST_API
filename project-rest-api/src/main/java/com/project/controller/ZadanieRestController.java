package com.project.controller;

import com.project.dto.ZadanieDTO;
import com.project.mapper.ZadanieMapper;
import com.project.model.Zadanie;
import com.project.service.ZadanieService;
import com.project.validation.ValidationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
@Tag(name = "Zadanie")
public class ZadanieRestController {

    private final ZadanieService zadanieService;
    private final ZadanieMapper zadanieMapper;
    private final ValidationService<Zadanie> validator;

    @Autowired
    public ZadanieRestController(ZadanieService zadanieService, ZadanieMapper zadanieMapper, ValidationService<Zadanie> validator) {
        this.zadanieService = zadanieService;
        this.zadanieMapper = zadanieMapper;
        this.validator = validator;
    }

    @GetMapping("/zadania/{zadanieId}")
    public ResponseEntity<EntityModel<ZadanieDTO>> getZadanie(@PathVariable Integer zadanieId) {
        return zadanieService.getZadanie(zadanieId)
                .map(zadanieMapper::zadanieToZadanieDTO)
                .map(this::addHateoasLinks)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/zadania")
    public ResponseEntity<EntityModel<ZadanieDTO>> createZadanie(@Valid @RequestBody ZadanieDTO zadanieDto) {
        Zadanie zadanie = zadanieMapper.zadanieDTOToZadanie(zadanieDto);
        validator.validate(zadanie);
        Zadanie created = zadanieService.setZadanie(zadanie);

        ZadanieDTO responseDto = zadanieMapper.zadanieToZadanieDTO(created);
        EntityModel<ZadanieDTO> entityModel = addHateoasLinks(responseDto);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @PutMapping("/zadania/{zadanieId}")
    public ResponseEntity<EntityModel<ZadanieDTO>> updateZadanie(@Valid @RequestBody ZadanieDTO zadanieDto,
                                                                 @PathVariable Integer zadanieId) {
        return zadanieService.getZadanie(zadanieId)
                .map(existing -> {
                    Zadanie zadanie = zadanieMapper.zadanieDTOToZadanie(zadanieDto);
                    zadanie.setZadanieId(zadanieId);
                    validator.validate(zadanie);
                    Zadanie updated = zadanieService.setZadanie(zadanie);
                    return ResponseEntity.ok(addHateoasLinks(zadanieMapper.zadanieToZadanieDTO(updated)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/zadania/{zadanieId}")
    public ResponseEntity<Void> deleteZadanie(@PathVariable Integer zadanieId) {
        return zadanieService.getZadanie(zadanieId)
                .map(z -> {
                    zadanieService.deleteZadanie(zadanieId);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/zadania")
    public ResponseEntity<CollectionModel<EntityModel<ZadanieDTO>>> getZadania(@ParameterObject Pageable pageable) {
        Page<Zadanie> page = zadanieService.getZadania(pageable);
        return ResponseEntity.ok(convertToCollectionModel(page, pageable));
    }

    @GetMapping(value = "/zadania", params = "nazwa")
    public ResponseEntity<CollectionModel<EntityModel<ZadanieDTO>>> getZadaniaByNazwa(
            @RequestParam(name = "nazwa") String nazwa, Pageable pageable) {

        Page<Zadanie> page = zadanieService.searchByNazwa(nazwa, pageable);
        return ResponseEntity.ok(convertToCollectionModel(page, pageable));
    }

    private CollectionModel<EntityModel<ZadanieDTO>> convertToCollectionModel(Page<Zadanie> page, Pageable pageable) {
        List<EntityModel<ZadanieDTO>> dtos = page.getContent().stream()
                .map(zadanieMapper::zadanieToZadanieDTO)
                .map(this::addHateoasLinks)
                .toList();

        return CollectionModel.of(dtos,
                linkTo(methodOn(ZadanieRestController.class).getZadania(pageable)).withSelfRel());
    }

    private EntityModel<ZadanieDTO> addHateoasLinks(ZadanieDTO dto) {
        EntityModel<ZadanieDTO> model = EntityModel.of(dto);

        model.add(linkTo(methodOn(ZadanieRestController.class).getZadanie(dto.getZadanieId())).withSelfRel());

        if (dto.getProjektId() != null) {
            model.add(linkTo(methodOn(ProjektRestController.class).getProjekt(dto.getProjektId())).withRel("projekt"));
        }

        model.add(linkTo(methodOn(ZadanieRestController.class).deleteZadanie(dto.getZadanieId())).withRel("usun_zadanie"));

        return model;
    }
}