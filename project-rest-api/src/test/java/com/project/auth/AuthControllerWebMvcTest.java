package com.project.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.config.JwtService;
import com.project.model.Student;
import com.project.validation.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

// ładuje tylko infrastrukturę dla tego jednego kontrolera
@WebMvcTest(AuthController.class)
public class AuthControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserDetailsService userDetailsService;

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
                .accessToken("access")
                .refreshToken("refresh")
                .build();
    }

    // symuluje uwierzytelnionego użytkownika, by przejść przez filtry Security
    @Test
    @WithMockUser
    void register_shouldWorkWithWebMvcTest() throws Exception {
        mockMvc.perform(post("/api/register")
                        // w testach MVC często trzeba dodać obsługę CSRF, jeśli Security jest włączone
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudent)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void login_shouldWorkWithWebMvcTest() throws Exception {
        when(authService.authenticate(any(Credentials.class))).thenReturn(testTokens);

        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCredentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    @WithMockUser
    void refresh_shouldWorkWithWebMvcTest() throws Exception {
        when(authService.refreshTokens(any(Tokens.class))).thenReturn(testTokens);

        mockMvc.perform(post("/api/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTokens)))
                .andExpect(status().isOk());
    }
}