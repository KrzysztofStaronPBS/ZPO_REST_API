package com.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.model.Projekt;
import com.project.model.Student;
import com.project.model.Zadanie;
import com.project.repository.ProjektRepository;
import com.project.repository.StudentRepository;
import com.project.repository.ZadanieRepository;

import jakarta.transaction.Transactional;

@Service
public class ProjektServiceImpl implements ProjektService {

	private final ProjektRepository projektRepository;
	private final ZadanieRepository zadanieRepository;
	private final StudentRepository studentRepository;

	@Autowired
	public ProjektServiceImpl(ProjektRepository projektRepository,
							  ZadanieRepository zadanieRepo, StudentRepository studentRepo) {
		this.projektRepository = projektRepository;
		this.zadanieRepository = zadanieRepo;
		this.studentRepository = studentRepo;
}

	@Override
	public Optional<Projekt> getProjekt(Integer projektId) {
		return projektRepository.findById(projektId);
	}

	@Override
	public Projekt setProjekt(Projekt projekt) {
		if (projekt.getDataCzasUtworzenia() == null) {
			projekt.setDataCzasUtworzenia(LocalDateTime.now());
		}
		return projektRepository.save(projekt);
	}

	@Override
	@Transactional
	public void deleteProjekt(Integer projektId) {
        zadanieRepository.deleteAll(zadanieRepository.findZadaniaProjektu(projektId));
		projektRepository.deleteById(projektId);
	}

	@Override
	public Page<Projekt> getProjekty(Pageable pageable) {
		return projektRepository.findAll(pageable);
	}
	
	@Override
	public Page<Projekt> searchByNazwa(String nazwa, Pageable pageable) {
		return projektRepository.findByNazwaContainingIgnoreCase(nazwa, pageable);
	}
	
	@Override
	@Transactional
	public void addStudentToProjekt(Integer projektId, Integer studentId) {
		Optional<Projekt> projektOpt = projektRepository.findById(projektId);
		Optional<Student> studentOpt = studentRepository.findById(studentId);

		if (projektOpt.isPresent() && studentOpt.isPresent()) {
			Projekt projekt = projektOpt.get();
			Student student = studentOpt.get();

			projekt.getStudenci().add(student);
			projektRepository.save(projekt);
		}
	}

	@Override
	@Transactional
	public void removeStudentFromProjekt(Integer projektId, Integer studentId) {
		Optional<Projekt> projektOpt = projektRepository.findById(projektId);
		Optional<Student> studentOpt = studentRepository.findById(studentId);

		if (projektOpt.isPresent() && studentOpt.isPresent()) {
			Projekt projekt = projektOpt.get();
			Student student = studentOpt.get();

			projekt.getStudenci().remove(student);
			projektRepository.save(projekt);
		}
	}

	@Override
	public Set<Student> getStudenciFromProjekt(Integer projektId) {
		return projektRepository.findById(projektId)
			.map(Projekt::getStudenci)
			.orElseThrow(() -> new IllegalArgumentException("Projekt nie istnieje"));
	}

	@Override
	public List<Zadanie> getZadaniaByProjekt(Integer projektId) {
		return zadanieRepository.findZadaniaProjektu(projektId);
	}

	@Override
	@Transactional
	public Zadanie addZadanieToProjekt(Integer projektId, Zadanie zadanie) {
		Projekt projekt = projektRepository.findById(projektId)
			.orElseThrow(() -> new IllegalArgumentException("Projekt nie istnieje"));

		zadanie.setProjekt(projekt);
		return zadanieRepository.save(zadanie);
	}

	@Override
	public boolean isProjektNameUnique(String nazwa) {
		return !projektRepository.existsByNazwaIgnoreCase(nazwa);
	}

	@Override
	public boolean isStudentAssignedToProjekt(Integer projektId, Integer studentId) {
		return projektRepository.findById(projektId)
			.map(Projekt::getStudenci)
			.map(studenci -> studenci.stream().
					anyMatch(s -> s.getStudentId().equals(studentId)))
			.orElse(false);
	}
}