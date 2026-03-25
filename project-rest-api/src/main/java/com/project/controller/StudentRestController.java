package com.project.controller;

import com.project.dto.StudentDTO;
import com.project.mapper.StudentMapper;
import com.project.model.Student;
import com.project.service.StudentService;
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
@Tag(name = "Student")
public class StudentRestController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @Autowired
    public StudentRestController(StudentService studentService, StudentMapper studentMapper) {
        this.studentService = studentService;
        this.studentMapper = studentMapper;
    }

    @GetMapping("/studenci/{studentId}")
    public ResponseEntity<EntityModel<StudentDTO>> getStudent(@PathVariable Integer studentId) {
        return studentService.getStudent(studentId)
                .map(studentMapper::studentToStudentDTO)
                .map(this::addHateoasLinks)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/studenci")
    public ResponseEntity<EntityModel<StudentDTO>> createStudent(@Valid @RequestBody StudentDTO studentDto) {
        Student student = studentMapper.studentDTOToStudent(studentDto);
        Student createdStudent = studentService.setStudent(student);

        StudentDTO responseDto = studentMapper.studentToStudentDTO(createdStudent);
        EntityModel<StudentDTO> entityModel = addHateoasLinks(responseDto);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @PutMapping("/studenci/{studentId}")
    public ResponseEntity<EntityModel<StudentDTO>> updateStudent(@Valid @RequestBody StudentDTO studentDto,
                                                                 @PathVariable Integer studentId) {
        return studentService.getStudent(studentId)
                .map(existing -> {
                    Student student = studentMapper.studentDTOToStudent(studentDto);
                    student.setStudentId(studentId); // Zapewnienie stałości ID
                    Student updated = studentService.setStudent(student);
                    return ResponseEntity.ok(addHateoasLinks(studentMapper.studentToStudentDTO(updated)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/studenci/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer studentId) {
        return studentService.getStudent(studentId)
                .map(p -> {
                    studentService.deleteStudent(studentId);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT); // 204 No Content jest lepsze dla Delete
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/studenci")
    public ResponseEntity<CollectionModel<EntityModel<StudentDTO>>> getStudenci(Pageable pageable) {
        Page<Student> page = studentService.getStudenci(pageable);
        return ResponseEntity.ok(convertToCollectionModel(page, pageable));
    }

    @GetMapping("/studenci/search")
    public ResponseEntity<CollectionModel<EntityModel<StudentDTO>>> searchStudents(
            @RequestParam(required = false) String nazwisko,
            @RequestParam(required = false) String nrIndeksu,
            Pageable pageable) {

        Page<Student> page;
        if (nazwisko != null && !nazwisko.isBlank()) {
            page = studentService.findByNazwiskoStartsWithIgnoreCase(nazwisko, pageable);
        } else if (nrIndeksu != null && !nrIndeksu.isBlank()) {
            page = studentService.findByNrIndeksuStartsWith(nrIndeksu, pageable);
        } else {
            page = studentService.getStudenci(pageable);
        }

        return ResponseEntity.ok(convertToCollectionModel(page, pageable));
    }

    private CollectionModel<EntityModel<StudentDTO>> convertToCollectionModel(Page<Student> page, Pageable pageable) {
        List<EntityModel<StudentDTO>> dtos = page.getContent().stream()
                .map(studentMapper::studentToStudentDTO)
                .map(this::addHateoasLinks)
                .toList();

        return CollectionModel.of(dtos,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentRestController.class)
                        .getStudenci(pageable)).withSelfRel());
    }

    private EntityModel<StudentDTO> addHateoasLinks(StudentDTO dto) {
        return EntityModel.of(dto,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentRestController.class)
                        .getStudent(dto.getStudentId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentRestController.class)
                        .getStudenci(Pageable.unpaged())).withRel("lista_studentow"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentRestController.class)
                        .deleteStudent(dto.getStudentId())).withRel("usun_studenta")
        );
    }
}