//package com.project.rest;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.hasSize;
//import static org.hamcrest.Matchers.is;
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import com.project.controller.StudentRestController;
//import com.project.model.Projekt;
//import com.project.model.Student;
//import com.project.service.StudentService;
//
//@ExtendWith(MockitoExtension.class)
//public class StudentRestControllerUnitTest {
//
//	@Mock
//	private StudentService mockStudentService;
//
//	@InjectMocks
//	private StudentRestController studentRestController;
//
//	@Test
//	@DisplayName("Should return student object given valid ID")
//	void getStudent_whenValidId_shouldReturnGivenStudent() {
//		Student student = new Student("Jan", "Kowalski", "123456", true);
//		when(mockStudentService.getStudent(student.getStudentId())).
//		thenReturn(Optional.of(student));
//
//		ResponseEntity<Student> responseEntity
//		= studentRestController.getStudent(student.getStudentId());
//
//		assertAll(
//				() -> assertEquals(HttpStatus.OK.value(),
//						responseEntity.getStatusCode().value()),
//				() -> assertEquals(student, responseEntity.getBody())
//			);
//	}
//
//	@Test
//	@DisplayName("Should return the page containing students")
//	void getStudents_shouldReturnPageWithStudents() {
//		List<Student> list = List.of(
//				new Student("Jan", "Kowalski", "123456", true),
//				new Student("Anna", "Nowak", "654321", true),
//				new Student("Jerzy", "Fabrykiewicz", "456789", false)
//				);
//
//		PageRequest pageable = PageRequest.of(1, 5);
//		Page<Student> page = new PageImpl<>(list, pageable, 5);
//
//		when(mockStudentService.getStudenci(pageable)).thenReturn(page);
//
//		Page<Student> pageWithStudents = studentRestController.getStudenci(pageable);
//
//		assertNotNull(pageWithStudents);
//		List<Student> students = pageWithStudents.getContent();
//		assertNotNull(students);
//
//		assertThat(students, hasSize(3));
//		assertAll(
//				() -> assertTrue(students.contains(list.get(0))),
//				() -> assertTrue(students.contains(list.get(1))),
//				() -> assertTrue(students.contains(list.get(2)))
//		);
//	}
//
//    @Test
//    @DisplayName("Should create the student entity providing valid data")
//    void createStudent_whenValidData_shouldCreateStudent() {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
//
//		Student student = new Student(1, "Jan", "Kowalski", "123456", "jankow000@pbs.edu.pl", true);
//
//        when(mockStudentService.setStudent(any(Student.class))).thenReturn(student);
//
//        ResponseEntity<Void> responseEntity = studentRestController.createStudent(student);
//
//        assertThat(responseEntity.getStatusCode().value(),
//        		is(HttpStatus.CREATED.value()));
//        assertThat(responseEntity.getHeaders().getLocation()
//        		.getPath(), is("/" + student.getStudentId()));
//    }
//
//    @Test
//    @DisplayName("Should update student entity providing valid data")
//    void updateStudent_whenValidData_shouldUpdateStudent() {
//		Student student = new Student(1, "Jan", "Kowalski", "123456", "jankow000@pbs.edu.pl", true);
//
//        when(mockStudentService.getStudent(student.getStudentId()))
//        .thenReturn(Optional.of(student));
//
//        ResponseEntity<Void> responseEntity = studentRestController
//        		.updateStudent(student, student.getStudentId());
//
//        assertThat(responseEntity.getStatusCode().value(), is(HttpStatus.OK.value()));
//    }
//
//	@Test
//	@DisplayName("Should delete student entity providing valid data")
//	void deleteStudent_whenValidId_shouldDeleteStudent() {
//		Student student = new Student("Jan", "Kowalski", "123456", true);
//
//		when(mockStudentService.getStudent(student.getStudentId()))
//		.thenReturn(Optional.of(student));
//
//		ResponseEntity<Void> responseEntity = studentRestController
//				.deleteStudent(student.getStudentId());
//
//		assertThat(responseEntity.getStatusCode().value(), is(HttpStatus.OK.value()));
//	}
//}
