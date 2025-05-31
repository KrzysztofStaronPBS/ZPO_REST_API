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
import com.project.model.Student;
import com.project.service.StudentService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin")
public class StudentRestControllerIntegrationTest {

    private final String apiPath = "/api/studenci";

    @MockitoBean
    private StudentService mockStudentService;

    @Autowired
    private MockMvc mockMvc;

    private JacksonTester<Student> jacksonTester;

    @Test
    public void getStudent_whenValidId_shouldReturnStudent() throws Exception {
        Student student = new Student(2, "Jan", "Kowalski", "jankow000@pbs.edu.pl", "123456", true);
        when(mockStudentService.getStudent(student.getStudentId())).thenReturn(Optional.of(student));

        mockMvc.perform(get(apiPath + "/{studentId}", student.getStudentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(student.getStudentId()))
                .andExpect(jsonPath("$.email").value(student.getEmail()));
        
        verify(mockStudentService, times(1)).getStudent(student.getStudentId());
        verifyNoMoreInteractions(mockStudentService);
    }

    @Test
    public void getStudent_whenInvalidId_shouldReturnNotFound() throws Exception {
    	Integer studentId = 2;
        when(mockStudentService.getStudent(studentId)).thenReturn(Optional.empty());

        mockMvc.perform(get(apiPath + "/{studentId}", studentId).accept(MediaType.APPLICATION_JSON))
			        .andDo(print())
			        .andExpect(status().isNotFound());

		verify(mockStudentService, times(1)).getStudent(studentId);
		verifyNoMoreInteractions(mockStudentService);
    }

    @Test
    public void getStudents_whenTwoAvailable_shouldReturnContentWithPagingParams() throws Exception {
        Student s1 = new Student("Jan", "Kowalski", "jankow000@pbs.edu.pl", "123456", true);
        Student s2 = new Student("Anna", "Nowak", "annnow000@pbs.edu.pl", "567890", true);
        
        Page<Student> page = new PageImpl<>(List.of(s1, s2));
        when(mockStudentService.getStudenci(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(apiPath).contentType(MediaType.APPLICATION_JSON))
		        .andDo(print())
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.content[*]").exists())
		        .andExpect(jsonPath("$.content.length()").value(2))
		        .andExpect(jsonPath("$.content[0].studentId").value(s1.getStudentId()))
		        .andExpect(jsonPath("$.content[1].studentId").value(s2.getStudentId()));
        
		verify(mockStudentService, times(1)).getStudenci(any(Pageable.class));
		verifyNoMoreInteractions(mockStudentService);
    }

    @Test
    public void createStudent_whenValidData_shouldReturnCreatedStatusWithLocation() throws Exception {
        Student student = new Student("Jan", "Kowalski", "jankow000@pbs.edu.pl", "123456", true);
        String jsonStudent = jacksonTester.write(student).getJson();
        student.setStudentId(3);

        when(mockStudentService.setStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post(apiPath)
                .content(jsonStudent)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", containsString(apiPath + "/" + student.getStudentId())));
    }
    
    @Test
    public void createStudent_whenEmptyEmail_shouldReturnNotValidException() throws Exception {
        Student student= new Student("Jan", "Kowalski", "", "123456", true);

        MvcResult result = mockMvc.perform(post(apiPath)
                .content(jacksonTester.write(student).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(mockStudentService, times(0)).setStudent(any(Student.class));

        Exception exception = result.getResolvedException();
        assertNotNull(exception);
        assertTrue(exception instanceof MethodArgumentNotValidException);
        System.out.println(exception.getMessage());
    }

    @Test
    public void updateStudent_whenValidData_shouldReturnOkStatus() throws Exception {
        Student student = new Student(5, "Jan", "Kowalski", "jankow000@pbs.edu.pl", "123456", true);
        String jsonStudent = jacksonTester.write(student).getJson();
        
        when(mockStudentService.getStudent(student.getStudentId())).thenReturn(Optional.of(student));
        when(mockStudentService.setStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(put(apiPath + "/{studentId}", student.getStudentId())
                .content(jsonStudent)
                .contentType(MediaType.APPLICATION_JSON)
		        .accept(MediaType.ALL))
		        .andDo(print())
                .andExpect(status().isOk());
		        
        verify(mockStudentService, times(1)).getStudent(student.getStudentId());
        verify(mockStudentService, times(1)).setStudent(any(Student.class));
        verifyNoMoreInteractions(mockStudentService);        
    }
    
    @Test
    public void getStudentsAndVerifyPagingParams() throws Exception {
        Integer page = 5;
        Integer size = 15;
        String sortProperty = "nazwisko";
        String sortDirection = "desc";

        mockMvc.perform(get(apiPath)
                .param("page", page.toString())
                .param("size", size.toString())
                .param("sort", String.format("%s,%s", sortProperty, sortDirection)))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(mockStudentService, times(1)).getStudenci(pageableCaptor.capture());

        PageRequest pageable = (PageRequest) pageableCaptor.getValue();
        assertEquals(page, pageable.getPageNumber());
        assertEquals(size, pageable.getPageSize());
        assertEquals(sortProperty, pageable.getSort().getOrderFor(sortProperty).getProperty());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor(sortProperty).getDirection());
    }
    
    @BeforeEach
    public void before(TestInfo testInfo) {
        System.out.println("-- METODA -> " + testInfo.getDisplayName());
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
    }

    @AfterEach
    public void after(TestInfo testInfo) {
        System.out.println("<- KONIEC -- " + testInfo.getDisplayName());
    }
}
