package com.project.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.project.controller.ZadanieRestController;
import com.project.model.Zadanie;
import com.project.service.ZadanieService;

@ExtendWith(MockitoExtension.class)
public class ZadanieRestControllerUnitTest {

	@Mock
	private ZadanieService mockZadanieService;
	
	@InjectMocks
	private ZadanieRestController zadanieRestController;
	
	@Test
	@DisplayName("Should return the task object given valid ID")
	void getZadanie_whenValidId_shouldReturnGivenZadanie() {
		Zadanie zadanie = new Zadanie("Zadanie1", 1, "Opis1");
		when(mockZadanieService.getZadanie(zadanie.getZadanieId()))
		.thenReturn(Optional.of(zadanie));
		
		ResponseEntity<Zadanie> responseEntity = zadanieRestController
				.getZadanie(zadanie.getZadanieId());
		
		assertAll(
			() -> assertEquals(HttpStatus.OK.value(), responseEntity
					.getStatusCode().value()),
			() -> assertEquals(zadanie, responseEntity.getBody())
		);
	}
	
    @Test
    @DisplayName("Should create the task entity providing valid data")
    void createZadanie_whenValidData_shouldCreateZadanie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Zadanie zadanie = new Zadanie(1, "Moduł logowania", 1, "Dodanie funkcji logowania");

        when(mockZadanieService.setZadanie(any(Zadanie.class))).thenReturn(zadanie);

        ResponseEntity<Void> responseEntity = zadanieRestController.createZadanie(zadanie);

        assertThat(responseEntity.getStatusCode().value(), is(HttpStatus.CREATED.value()));
        assertThat(responseEntity.getHeaders().getLocation().getPath(),
        		is("/" + zadanie.getZadanieId()));
    }
	
	@Test
	@DisplayName("Should update the task providing valid data")
	void updateZadanie_whenValidData_shouldUpdateZadanie() {
		Zadanie zadanie = new Zadanie("Zadanie1", 1, "Opis1");
		when(mockZadanieService.getZadanie(zadanie.getZadanieId()))
		.thenReturn(Optional.of(zadanie));
		
		ResponseEntity<Void> responseEntity = zadanieRestController
				.updateZadanie(zadanie, zadanie.getZadanieId());
		
		assertThat(responseEntity.getStatusCode().value(),
				is(HttpStatus.OK.value()));
	}
	
	@Test
	@DisplayName("Should return response of task with given ID not found")
	void deleteZadanie_whenInvalidId_shouldReturnNotFound() {
		Integer zadanieId = 1;
		
		ResponseEntity<Void> responseEntity = zadanieRestController
				.deleteZadanie(zadanieId);
		
		assertThat(responseEntity.getStatusCode().value(),
				is(HttpStatus.NOT_FOUND.value()));
	}
	
	@Test
	@DisplayName("Should return the page containing tasks")
	void getZadania_shouldReturnPageWithZadania() {
		List<Zadanie> list = List.of(
			new Zadanie("Zadanie1", 1, "Opis1"),
			new Zadanie("Zadanie2", 2, "Opis2"),
			new Zadanie("Zadanie3", 3, "Opis3"),
			new Zadanie("Zadanie4", 4, "Opis4"),
			new Zadanie("Zadanie5", 5, "Opis5")
		);
				
		PageRequest pageable = PageRequest.of(1, 10);
		Page<Zadanie> page = new PageImpl<>(list, pageable, 5);
		
		when(mockZadanieService.getZadania(pageable)).thenReturn(page);
		
		Page<Zadanie> pageWithZadania = zadanieRestController
				.getZadania(pageable);
		
		assertNotNull(pageWithZadania);
		List<Zadanie> zadania = pageWithZadania.getContent();
		assertNotNull(zadania);
		
		assertThat(zadania, hasSize(5));
		assertAll(
			() -> assertTrue(zadania.contains(list.get(0))),
			() -> assertTrue(zadania.contains(list.get(1))),
			() -> assertTrue(zadania.contains(list.get(2))),
			() -> assertTrue(zadania.contains(list.get(3))),
			() -> assertTrue(zadania.contains(list.get(4)))
		);
	}
}
