package com.project.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity //Indeksujemy kolumny, które są najczęściej wykorzystywane do wyszukiwania studentów
@Table(name = "student",
indexes = { 
		@Index(name = "idx_nazwisko", columnList = "nazwisko"),
		@Index(name = "idx_email", columnList = "email", unique = true),
		@Index(name = "idx_nr_indeksu", columnList = "nr_indeksu", unique = true) })
public class Student implements UserDetails {
	@Id
	@GeneratedValue
	@Column(name = "student_id")
	private Integer studentId;
	
	@NotBlank(message = "Pole imie nie może być puste!")
	@Size(min = 3, max = 50, message = "Imię musi zawierać od {min} do {max} znaków!")
	@Column(nullable = false, length = 50)
	private String imie;
	
	@NotBlank(message = "Pole nazwisko nie może być puste!")
	@Size(min = 3, max = 100, message = "Nazwisko musi zawierać od {min} do {max} znaków!")
	@Column(nullable = false, length = 100)
	private String nazwisko;
	
	@NotBlank(message = "Pole Numer indeksu nie może być puste!")
	@Size(min = 3, max = 20, message = "Numer indeksu musi zawierać od {min} do {max} znaków!")
	@Column(name = "nr_indeksu", nullable = false, unique = true, length = 20)
	private String nrIndeksu;

	@Email(message = "Niepoprawny format adresu e-mail")
	@NotBlank(message = "Pole email nie może być puste!")
	@Size(min = 3, max = 50, message = "Email musi zawierać od {min} do {max} znaków!")
	@Column(unique = true, nullable = false, length = 50)
	private String email;
	
	@Column(nullable = false)
	private Boolean stacjonarny;

	@JsonIgnoreProperties({"studenci"})
	@ManyToMany(mappedBy = "studenci")
	private Set<Projekt> projekty;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Size(min=8, max=64, message="Hasło musi składać się z przynajmniej {min} i nie przekraczać {max} znaków")
	private String password;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "student_role",
			joinColumns = {@JoinColumn(name="student_id")},
			inverseJoinColumns = {@JoinColumn(name = "role_id")})
	private Set<Role> roles;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles
				.stream()
				.map(r -> new SimpleGrantedAuthority(r.getName()))
				.collect(Collectors.toList());
	}

	@Override
	public String getUsername() {
		return this.email; //zakładamy, że e-mail będzie wykorzystywany przy logowaniu
	}
}