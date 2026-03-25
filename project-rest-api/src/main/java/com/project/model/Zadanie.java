package com.project.model;

import java.time.LocalDateTime;

import lombok.*;
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

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

	@Column()
	private Integer kolejnosc;
	
	@Column(length = 1000)
	@Size(max = 1000, message = "Pole opis może zawierać maksymalnie {max} znaków!")
	private String opis;
	
	@CreatedDate
	@Column(name = "dataczas_dodania", nullable = false)
	private LocalDateTime dataCzasDodania;
	
	@ManyToOne
	@JsonIgnoreProperties({"zadania"})
	@JoinColumn(name = "projekt_id")
	private Projekt projekt;
}