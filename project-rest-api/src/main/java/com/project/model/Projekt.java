package com.project.model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="projekt",
indexes = { 
		@Index(name = "idx_projekt_id", columnList = "projekt_id", unique = true),
		@Index(name = "idx_nazwa", columnList = "nazwa", unique = false),
		@Index(name = "idx_data_oddania", columnList = "data_oddania", unique = false)
	})
public class Projekt {
	@Id
	@GeneratedValue
	@Column(name="projekt_id") //tylko jeżeli nazwa kolumny w bazie danych ma być inna od nazwy zmiennej
	private Integer projektId;

	@NotBlank(message = "Pole nazwa nie może być puste!")
	@Size(min = 3, max = 50, message = "Nazwa musi zawierać od {min} do {max} znaków!")
	@Column(nullable = false, length = 50)
	private String nazwa;
	
	@Column(nullable = true, length = 1000)
	@Size(max = 1000, message = "Pole opis może zawierać maksymalnie {max} znaków!")
	private String opis;
	
	@CreatedDate
	@Column(name="dataczas_utworzenia", nullable = false)
	private LocalDateTime dataCzasUtworzenia;
	
	@Column(name="data_oddania", nullable = true)
	private LocalDate dataOddania;
	
	@OneToMany(mappedBy = "projekt")
	@JsonIgnoreProperties({"projekt"})
	private List<Zadanie> zadania;
	
	@ManyToMany
	@JsonIgnoreProperties({"projekty"})
	@JoinTable(name = "projekt_student",
	joinColumns = {@JoinColumn(name="projekt_id")},
	inverseJoinColumns = {@JoinColumn(name="student_id")})
	private Set<Student> studenci;
	

	public Integer getProjektId() {
		return projektId;
	}

	public void setProjektId(Integer projektId) {
		this.projektId = projektId;
	}

	public String getNazwa() {
		return nazwa;
	}

	public void setNazwa(String nazwa) {
		this.nazwa = nazwa;
	}

	public String getOpis() {
		return opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}

	public LocalDateTime getDataCzasUtworzenia() {
		return dataCzasUtworzenia;
	}

	public void setDataCzasUtworzenia(LocalDateTime dataCzasUtworzenia) {
		this.dataCzasUtworzenia = dataCzasUtworzenia;
	}

	public LocalDate getDataOddania() {
		return dataOddania;
	}

	public void setDataOddania(LocalDate dataOddania) {
		this.dataOddania = dataOddania;
	}
	
	public List<Zadanie> getZadania() {
		return zadania;
	}

	public void setZadania(List<Zadanie> zadania) {
		this.zadania = zadania;
	}
	public Set<Student> getStudenci() {
		return studenci;
	}

	public void setStudenci(Set<Student> studenci) {
		this.studenci = studenci;
	}
	
	public Projekt() {
		super();
	}

	public Projekt(
			@NotBlank(message = "Pole nazwa nie może być puste!") @Size(min = 3, max = 50, message = "Nazwa musi zawierać od {min} do {max} znaków!") String nazwa,
			String opis) {
		super();
		this.nazwa = nazwa;
		this.opis = opis;
	}

	public Projekt(Integer projektId,
			@NotBlank(message = "Pole nazwa nie może być puste!") @Size(min = 3, max = 50, message = "Nazwa musi zawierać od {min} do {max} znaków!") String nazwa,
			String opis,
			@NotBlank(message = "Pole Dataczas utworzenia nie może być puste!") LocalDateTime dataCzasUtworzenia,
			LocalDate dataOddania) {
		super();
		this.projektId = projektId;
		this.nazwa = nazwa;
		this.opis = opis;
		this.dataCzasUtworzenia = dataCzasUtworzenia;
		this.dataOddania = dataOddania;
	}

	public Projekt(
			@NotBlank(message = "Pole nazwa nie może być puste!") @Size(min = 3, max = 50, message = "Nazwa musi zawierać od {min} do {max} znaków!") String nazwa,
			String opis, LocalDate dataOddania) {
		super();
		this.nazwa = nazwa;
		this.opis = opis;
		this.dataOddania = dataOddania;
	}

	public Projekt(Integer projektId,
			@NotBlank(message = "Pole nazwa nie może być puste!") @Size(min = 3, max = 50, message = "Nazwa musi zawierać od {min} do {max} znaków!") String nazwa,
			String opis, LocalDate dataOddania) {
		super();
		this.projektId = projektId;
		this.nazwa = nazwa;
		this.opis = opis;
		this.dataOddania = dataOddania;
	}
}