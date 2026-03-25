package com.project.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.Student;
import com.project.validation.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private ValidationService<Student> studentValidator;

    private Student testStudent;
    private Credentials testCredentials;
    private Tokens testTokens;

    @BeforeEach
    void setUp() {
        testStudent = Student.builder()
                .studentId(1)
                .imie("Jan")
                .nazwisko("Kowalski")
                .email("jankow000@pbs.edu.pl")
                .password("tajnehaslo123")
                .nrIndeksu("123456")
                .stacjonarny(true)
                .roles(new HashSet<>())
                .build();

        testCredentials = new Credentials("jankow000@pbs.edu.pl", "tajnehaslo123");

        testTokens = Tokens.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .build();
    }

    @Test
    void register_shouldReturnOk_whenDataIsValid() throws Exception {
        // Given
        doNothing().when(studentValidator).validate(any(Student.class));
        doNothing().when(authService).register(any(Student.class));

        // When & Then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudent)))
                .andExpect(status().isOk());

        verify(studentValidator, times(1)).validate(any(Student.class));
        verify(authService, times(1)).register(any(Student.class));
    }

    @Test
    void login_shouldReturnTokens_whenCredentialsAreValid() throws Exception {
        // Given
        when(authService.authenticate(any(Credentials.class))).thenReturn(testTokens);

        // When & Then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCredentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-456"));

        verify(authService, times(1)).authenticate(any(Credentials.class));
    }

    @Test
    void refresh_shouldReturnNewTokens_whenRefreshTokenIsValid() throws Exception {
        // given
        Tokens inputTokens = Tokens.builder().refreshToken("old-refresh-token").build();
        Tokens newTokens = Tokens.builder()
                .accessToken("new-access")
                .refreshToken("new-refresh")
                .build();

        when(authService.refreshTokens(any(Tokens.class))).thenReturn(newTokens);

        // when & then
        mockMvc.perform(post("/api/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputTokens)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"));

        verify(authService, times(1)).refreshTokens(any(Tokens.class));
    }
}