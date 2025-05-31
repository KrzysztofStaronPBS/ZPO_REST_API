package com.project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.model.Zadanie;

public interface ZadanieRepository extends JpaRepository<Zadanie, Integer> {
	//dwukropkiem oznacza się parametry zapytania
	@Query("SELECT z FROM Zadanie z WHERE z.projekt.projektId = :projektId")
	Page<Zadanie> findZadaniaProjektu(@Param("projektId") Integer projektId, Pageable pageable);

	@Query("SELECT z FROM Zadanie z WHERE z.projekt.projektId = :projektId")
	List<Zadanie> findZadaniaProjektu(@Param("projektId") Integer projektId);
	
	@Query("SELECT p FROM Zadanie p WHERE upper(p.nazwa) LIKE upper(%:nazwa%)")
	Page<Zadanie> findByNazwaContainingIgnoreCase(String nazwa, Pageable pageable);
	
	boolean existsByNazwaIgnoreCase(String nazwa);

	@Query("SELECT COUNT(p) > 0 FROM Projekt p JOIN p.studenci s WHERE p.projektId = :projektId AND s.studentId = :studentId")
	boolean existsStudentInProjekt(@Param("projektId") Integer projektId, @Param("studentId") Integer studentId);
}