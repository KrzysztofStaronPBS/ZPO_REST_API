package com.project.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="zadanie")
public class Zadanie {
	@Id
	@GeneratedValue
	@Column(name="zadanie_id")
	private Integer zadanieId;
	
	@NotBlank(message = "Pole nazwa nie może być puste!")
	@Size(min = 3, max = 50, message = "Nazwa musi zawierać od {min} do {max} znaków!")
	@Column(nullable = false, length = 50)
	private String nazwa;

	@Column(nullable = true)
	private Integer kolejnosc;
	
	@Column(nullable = true, length = 1000)
	@Size(max = 1000, message = "Pole opis może zawierać maksymalnie {max} znaków!")
	private String opis;
	
	@CreatedDate
	@Column(name = "dataczas_dodania", nullable = false)
	private LocalDateTime dataCzasDodania;
	
	@ManyToOne
	@JsonIgnoreProperties({"zadania"})
	@JoinColumn(name = "projekt_id")
	private Projekt projekt;

	public Integer getZadanieId() {
		return zadanieId;
	}

	public void setZadanieId(Integer zadanieId) {
		this.zadanieId = zadanieId;
	}

	public String getNazwa() {
		return nazwa;
	}

	public void setNazwa(String nazwa) {
		this.nazwa = nazwa;
	}

	public Integer getKolejnosc() {
		return kolejnosc;
	}

	public void setKolejnosc(Integer kolejnosc) {
		this.kolejnosc = kolejnosc;
	}

	public String getOpis() {
		return opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}

	public LocalDateTime getDataCzasDodania() {
		return dataCzasDodania;
	}

	public void setDataCzasDodania(LocalDateTime dataCzasDodania) {
		this.dataCzasDodania = dataCzasDodania;
	}
	
	public Projekt getProjekt() {
		return projekt;
	}

	public void setProjekt(Projekt projekt) {
		this.projekt = projekt;
	}
	
	public Zadanie() {
		super();
	}

	public Zadanie(String nazwa, Integer kolejnosc, String opis) {
		super();
		this.nazwa = nazwa;
		this.kolejnosc = kolejnosc;
		this.opis = opis;
	}

	public Zadanie(Integer zadanieId, String nazwa, Integer kolejnosc, String opis) {
		super();
		this.zadanieId = zadanieId;
		this.nazwa = nazwa;
		this.kolejnosc = kolejnosc;
		this.opis = opis;
	}
}