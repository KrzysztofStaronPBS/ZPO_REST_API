package com.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.model.Zadanie;
import com.project.repository.ZadanieRepository;

@Service
public class ZadanieServiceImpl implements ZadanieService {
	
	private final ZadanieRepository zadanieRepository;
	
	@Autowired
	public ZadanieServiceImpl (ZadanieRepository zadanieRepo) {
		this.zadanieRepository = zadanieRepo;
	}

	@Override
	public Optional<Zadanie> getZadanie(Integer zadanieId) {
		return zadanieRepository.findById(zadanieId);
	}

	@Override
	public Zadanie setZadanie(Zadanie zadanie) {
		if (zadanie.getDataCzasDodania() == null) {
			zadanie.setDataCzasDodania(LocalDateTime.now());
		}
		return zadanieRepository.save(zadanie);
	}

	@Override
	public void deleteZadanie(Integer zadanieId) {
		zadanieRepository.deleteById(zadanieId);
	}

	@Override
	public Page<Zadanie> getZadania(Pageable pageable) {
		return zadanieRepository.findAll(pageable);
	}

	@Override
	public Page<Zadanie> searchByNazwa(String nazwa, Pageable pageable) {
		return zadanieRepository.findByNazwaContainingIgnoreCase(nazwa, pageable);
	}

	@Override
	public List<Zadanie> getZadaniaProjektu(Integer projektId) {
		return zadanieRepository.findZadaniaProjektu(projektId);
	}

	@Override
	public boolean isZadanieNameUnique(String nazwa) {
		return zadanieRepository.existsByNazwaIgnoreCase(nazwa);
	}
}