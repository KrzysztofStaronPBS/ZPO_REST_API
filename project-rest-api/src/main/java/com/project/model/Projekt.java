package com.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="projekt",
indexes = { 
		@Index(name = "idx_projekt_id", columnList = "projekt_id", unique = true),
		@Index(name = "idx_nazwa", columnList = "nazwa"),
		@Index(name = "idx_data_oddania", columnList = "data_oddania")
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
	
	@Column(length = 1000)
	@Size(max = 1000, message = "Pole opis może zawierać maksymalnie {max} znaków!")
	private String opis;
	
	@CreatedDate
	@Column(name="dataczas_utworzenia", nullable = false)
	private LocalDateTime dataCzasUtworzenia;
	
	@Column(name="data_oddania")
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
}