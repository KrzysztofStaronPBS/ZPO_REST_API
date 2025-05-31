package com.project.rest;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.model.Zadanie;
import com.project.service.ZadanieService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin")
public class ZadanieRestControllerIntegrationTest {

    private final String apiPath = "/api/zadania";

    @MockitoBean
    private ZadanieService mockZadanieService;

    @Autowired
    private MockMvc mockMvc;

    private JacksonTester<Zadanie> jacksonTester;

    @Test
    public void getZadanie_whenValidId_shouldReturnZadanie() throws Exception {
        Zadanie zadanie = new Zadanie("Moduł logowania", 1, "Dodanie funkcji logowania");
        zadanie.setZadanieId(1);
        zadanie.setDataCzasDodania(LocalDateTime.now());

        when(mockZadanieService.getZadanie(zadanie.getZadanieId()))
        .thenReturn(Optional.of(zadanie));

        mockMvc.perform(get(apiPath + "/{zadanieId}", zadanie.getZadanieId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zadanieId").value(zadanie.getZadanieId()))
                .andExpect(jsonPath("$.nazwa").value(zadanie.getNazwa()));

        verify(mockZadanieService, times(1)).getZadanie(zadanie.getZadanieId());
        verifyNoMoreInteractions(mockZadanieService);
    }

    @Test
    public void getZadanie_whenInvalidId_shouldReturnNotFound() throws Exception {
        Integer zadanieId = 2;
        when(mockZadanieService.getZadanie(999)).thenReturn(Optional.empty());

        mockMvc.perform(get(apiPath + "/{zadanieId}", zadanieId)
        		.accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(mockZadanieService, times(1)).getZadanie(zadanieId);
        verifyNoMoreInteractions(mockZadanieService);
    }

    @Test
    public void getZadania_whenTwoAvailable_shouldReturnContentWithPagingParams() throws Exception {
        Zadanie z1 = new Zadanie("Moduł X", 1, "Opis 1");
        Zadanie z2 = new Zadanie("Moduł Y", 2, "Opis 2");

        Page<Zadanie> page = new PageImpl<>(List.of(z1, z2));
        when(mockZadanieService.getZadania(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(apiPath).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]").exists())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].zadanieId").value(z1.getZadanieId()))
                .andExpect(jsonPath("$.content[0].zadanieId").value(z2.getZadanieId()));

        verify(mockZadanieService, times(1)).getZadania(any(Pageable.class));
        verifyNoMoreInteractions(mockZadanieService);
    }

    @Test
    public void createZadanie_whenValidData_shouldReturnCreatedStatusWithLocation() throws Exception {
        Zadanie zadanie = new Zadanie("Nowy moduł", 1, "Opis zadania");
        String jsonZadanie = jacksonTester.write(zadanie).getJson();
        zadanie.setZadanieId(10);
        zadanie.setDataCzasDodania(LocalDateTime.now());

        when(mockZadanieService.setZadanie(any(Zadanie.class))).thenReturn(zadanie);

        mockMvc.perform(post(apiPath)
                .content(jsonZadanie)
                .contentType(MediaType.APPLICATION_JSON)
		        .accept(MediaType.ALL))
		        .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location",
                		containsString(apiPath + "/" + zadanie.getZadanieId())));
    }

    @Test
    public void createZadanie_whenEmptyName_shouldReturnNotValidException() throws Exception {
        Zadanie zadanie = new Zadanie("", 1, "Brak nazwy");

        MvcResult result = mockMvc.perform(post(apiPath)
                .content(jacksonTester.write(zadanie).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        
        verify(mockZadanieService, times(0)).setZadanie(any(Zadanie.class));

        Exception exception = result.getResolvedException();
        assertNotNull(exception);
        assertTrue(exception instanceof MethodArgumentNotValidException);
        System.out.println(exception.getMessage());

        verify(mockZadanieService, times(0)).setZadanie(any(Zadanie.class));
    }

    @Test
    public void updateZadanie_whenValid_shouldReturnOk() throws Exception {
        Zadanie zadanie = new Zadanie(7, "Edytowane zadanie", 2, "Zmieniony opis");
        String jsonZadanie = jacksonTester.write(zadanie).getJson();

        when(mockZadanieService.getZadanie(zadanie.getZadanieId()))
        .thenReturn(Optional.of(zadanie));
        when(mockZadanieService.setZadanie(any(Zadanie.class))).thenReturn(zadanie);

        mockMvc.perform(put(apiPath + "/{zadanieId}", zadanie.getZadanieId())
        		.content(jsonZadanie)
                .contentType(MediaType.APPLICATION_JSON)
		        .accept(MediaType.ALL))
		        .andDo(print())
                .andExpect(status().isOk());

        verify(mockZadanieService, times(1)).getZadanie(zadanie.getZadanieId());
        verify(mockZadanieService, times(1)).setZadanie(any(Zadanie.class));
        verifyNoMoreInteractions(mockZadanieService);
    }

    @Test
    public void getZadaniaAndVerifyPagingParams() throws Exception {
        int page = 2;
        int size = 5;
        String sortProperty = "nazwa";
        String sortDirection = "desc";

        mockMvc.perform(get(apiPath)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("sort", String.format("%s,%s", sortProperty, sortDirection)))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(mockZadanieService).getZadania(pageableCaptor.capture());

        PageRequest pageable = (PageRequest) pageableCaptor.getValue();
        assertEquals(page, pageable.getPageNumber());
        assertEquals(size, pageable.getPageSize());
        assertEquals(sortProperty, pageable.getSort()
        		.getOrderFor(sortProperty).getProperty());
        assertEquals(Sort.Direction.DESC, pageable.getSort()
        		.getOrderFor(sortProperty).getDirection());
    }
    
    @BeforeEach
    public void before(TestInfo testInfo) {
        System.out.println("-- METODA -> " + testInfo.getDisplayName());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JacksonTester.initFields(this, mapper);
    }

    @AfterEach
    public void after(TestInfo testInfo) {
        System.out.println("<- KONIEC -- " + testInfo.getDisplayName());
    }
}
