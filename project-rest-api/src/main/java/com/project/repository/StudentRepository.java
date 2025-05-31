package com.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.model.Projekt;
import com.project.model.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
	Optional<Student> findByNrIndeksu(String nrIndeksu);

	@Query("SELECT s FROM Student s WHERE s.nrIndeksu LIKE :nrIndeksu%")
	Page<Student> findByNrIndeksuStartsWith(String nrIndeksu, Pageable pageable);

	@Query("SELECT s FROM Student s WHERE upper(s.nazwisko) LIKE upper(:nazwisko%)")
	Page<Student> findByNazwiskoStartsWithIgnoreCase(String nazwisko, Pageable pageable);
	
	@Query("SELECT p FROM Projekt p JOIN p.studenci s WHERE s.studentId = :studentId")
	List<Projekt> findProjektyStudenta(@Param("studentId") Integer studentId);
}