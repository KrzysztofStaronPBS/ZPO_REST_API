package com.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.model.Projekt;
import com.project.model.Student;
import com.project.repository.ProjektRepository;
import com.project.repository.StudentRepository;

import jakarta.transaction.Transactional;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	public StudentServiceImpl(StudentRepository studentRepo, ProjektRepository projektRepo) {
		this.studentRepository = studentRepo;
	}
	
	@Override
	public Optional<Student> getStudent(Integer studentId) {
		return studentRepository.findById(studentId);
	}

	@Override
	public Student setStudent(Student student) {
		return studentRepository.save(student);
	}

	@Override
	@Transactional
	public void deleteStudent(Integer studentId) {
		Optional<Student> studentOptional = studentRepository.findById(studentId);
		
	    if (studentOptional.isPresent()) {
	        Student student = studentOptional.get();
        
        for (Projekt projekt : student.getProjekty()) {
            projekt.getStudenci().remove(student);
        }
        
        studentRepository.delete(student);
        }
	}

	@Override
	public Page<Student> getStudenci(Pageable pageable) {
		return studentRepository.findAll(pageable);
	}

	@Override
	public Optional<Student> findByNrIndeksu(String nrIndeksu) {
		return studentRepository.findByNrIndeksu(nrIndeksu);
	}

	@Override
	public Page<Student> findByNrIndeksuStartsWith(String prefix, Pageable pageable) {
		return studentRepository.findByNrIndeksuStartsWith(prefix, pageable);
	}
	
	@Override
	public Page<Student> findByNazwiskoStartsWithIgnoreCase(String nazwisko, Pageable pageable) {
		return studentRepository.findByNazwiskoStartsWithIgnoreCase(nazwisko, pageable);
	}

	@Override
	public List<Projekt> findProjektyStudenta(Integer studentId) {
		return studentRepository.findProjektyStudenta(studentId);
	}
}