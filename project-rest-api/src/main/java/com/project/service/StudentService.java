package com.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.model.Projekt;
import com.project.model.Student;

public interface StudentService {
	Optional<Student> getStudent(Integer studentId);
	Student setStudent(Student student);
	void deleteStudent(Integer studentId);
	Page<Student> getStudenci(Pageable pageable);
	
	Optional<Student> findByNrIndeksu(String nrIndeksu);
	Page<Student> findByNrIndeksuStartsWith(String prefix, Pageable pageable);
	Page<Student> findByNazwiskoStartsWithIgnoreCase(String nazwisko, Pageable pageable);
	List<Projekt> findProjektyStudenta(Integer studentId);

	Optional<Student> findByEmail(String email);
}
