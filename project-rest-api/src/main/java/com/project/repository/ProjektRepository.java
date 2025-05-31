package com.project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.model.Projekt;
import com.project.model.Zadanie;

public interface ProjektRepository extends JpaRepository<Projekt, Integer> {
	@Query("SELECT p FROM Projekt p WHERE upper(p.nazwa) LIKE upper(%:nazwa%)")
	Page<Projekt> findByNazwaContainingIgnoreCase(String nazwa, Pageable pageable);
	
	@Query("SELECT p FROM Projekt p WHERE upper(p.nazwa) LIKE upper(%:nazwa%)")
	List<Projekt> findByNazwaContainingIgnoreCase(String nazwa);
	
	@Query("SELECT z FROM Zadanie z WHERE z.projekt.projektId = :projektId")
	List<Zadanie> findZadaniaProjektu(@Param("projektId") Integer projektId);
	
	boolean existsByNazwaIgnoreCase(String nazwa);
}