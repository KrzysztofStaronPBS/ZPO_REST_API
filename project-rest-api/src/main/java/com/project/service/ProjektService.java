package com.project.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.model.Projekt;
import com.project.model.Student;
import com.project.model.Zadanie;

public interface ProjektService {
	Optional<Projekt> getProjekt(Integer projektId);
	Projekt setProjekt(Projekt projekt);
	void deleteProjekt(Integer projektId);
	Page<Projekt> getProjekty(Pageable pageable);
	Page<Projekt> searchByNazwa(String nazwa, Pageable pageable);
	boolean isProjektNameUnique(String nazwa);
	boolean isStudentAssignedToProjekt(Integer projektId, Integer studentId);

	void addStudentToProjekt(Integer projektId, Integer studentId);
	void removeStudentFromProjekt(Integer projektId, Integer studentId);
	Set<Student> getStudenciFromProjekt(Integer projektId);

	List<Zadanie> getZadaniaByProjekt(Integer projektId);
	Zadanie addZadanieToProjekt(Integer projektId, Zadanie zadanie);
}