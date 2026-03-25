package com.project.service;

import com.project.model.Projekt;
import com.project.model.Student;
import com.project.repository.ProjektRepository;
import com.project.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjektServiceUnitTest {

    @Mock
    private ProjektRepository projektRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private ProjektServiceImpl projektService;

    private Projekt testProjekt;

    @BeforeEach
    void setUp() {
        testProjekt = new Projekt();
        testProjekt.setProjektId(1);
        testProjekt.setNazwa("Testowy Projekt");
        testProjekt.setOpis("Opis");
        testProjekt.setDataOddania(LocalDate.now().plusDays(7));
        testProjekt.setStudenci(new HashSet<>());
    }

    @Test
    @DisplayName("Powinien zwrócić projekt, gdy ID istnieje")
    void getProjekt_whenIdExists_shouldReturnProject() {
        // Given
        when(projektRepository.findById(1)).thenReturn(Optional.of(testProjekt));

        // When
        Optional<Projekt> result = projektService.getProjekt(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Testowy Projekt", result.get().getNazwa());
        verify(projektRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Powinien zwrócić pusty Optional, gdy ID nie istnieje (odpowiednik 404)")
    void getProjekt_whenIdDoesNotExist_shouldReturnEmpty() {
        // Given
        when(projektRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Projekt> result = projektService.getProjekt(999);

        // Then
        assertFalse(result.isPresent());
        verify(projektRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Powinien ustawić datę utworzenia, jeśli jest nullem podczas zapisu")
    void setProjekt_shouldSetCreationDate_whenNull() {
        // Given
        testProjekt.setDataCzasUtworzenia(null);
        when(projektRepository.save(any(Projekt.class))).thenReturn(testProjekt);

        // When
        Projekt saved = projektService.setProjekt(testProjekt);

        // Then
        assertNotNull(saved.getDataCzasUtworzenia());
        verify(projektRepository, times(1)).save(testProjekt);
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek przy pobieraniu studentów z nieistniejącego projektu")
    void getStudenciFromProjekt_whenProjectNotExists_shouldThrowException() {
        // Given
        when(projektRepository.findById(2)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projektService.getStudenciFromProjekt(2));

        assertEquals("Projekt nie istnieje", exception.getMessage());
    }

    @Test
    @DisplayName("Powinien poprawnie dodać studenta do projektu")
    void addStudentToProjekt_whenBothExist_shouldAddSuccessfully() {
        // Given
        Student student = new Student();
        student.setStudentId(10);

        when(projektRepository.findById(1)).thenReturn(Optional.of(testProjekt));
        when(studentRepository.findById(10)).thenReturn(Optional.of(student));

        // When
        projektService.addStudentToProjekt(1, 10);

        // Then
        assertTrue(testProjekt.getStudenci().contains(student));
        verify(projektRepository, times(1)).save(testProjekt);
    }
}