package pl.mihome.stejsiWebApp.model;

import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;



@Entity
@Table(name = "uzytkownicy")
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id",
		scope = Podopieczny.class)
public class Podopieczny extends AuditBase {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Pole imię nie może być puste")
	@Size(min = 3, max = 30)
	private String imie;
	
	@NotEmpty(message = "Pole nazwisko nie może być puste")
	@Size(min = 3, max = 50)
	private String nazwisko;
	
	@NotEmpty(message = "Pole email nie może być puste")
	@Email(message = "Pole e-email musi zawierać prawidłowy adres e-mail")
	@Size(max = 200)
	private String email;	
	
	private int phoneNumber;
	
	private boolean aktywny;	
	
	@OneToMany(mappedBy = "owner")
	//@JsonManagedReference
	private Set<PakietTreningow> trainingPackages;
	
	@OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
	private Set<TipComment> comments;
	
	@ManyToMany
	@JoinTable(name = "tip_read_status",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "tip_id"))
	private Set<Tip> tipsRead;
	
	@OneToMany(mappedBy = "owner")
	private Set<Token> tokens;
	
		
	public Podopieczny() {
	}

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getImie() {
		return imie;
	}
	public void setImie(String imie) {
		this.imie = imie;
	}
	public String getNazwisko() {
		return nazwisko;
	}
	public void setNazwisko(String nazwisko) {
		this.nazwisko = nazwisko;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isAktywny() {
		return aktywny;
	}
	public void setAktywny(boolean aktywny) {
		this.aktywny = aktywny;
	}
	
	public int getPhoneNumber() {
		return phoneNumber;
	}

	public Set<PakietTreningow> getTrainingPackages() {
		return trainingPackages;
	}

	public void setPhoneNumber(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setTrainingPackages(Set<PakietTreningow> trainingPackages) {
		this.trainingPackages = trainingPackages;
	}
	
	public Set<TipComment> getComments() {
		return comments;
	}

	public Set<Tip> getTipsRead() {
		return tipsRead;
	}	

	public Set<Token> getTokens() {
		return tokens;
	}


	public void setTokens(Set<Token> tokens) {
		this.tokens = tokens;
	}


	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(obj instanceof Podopieczny) {
			var p = (Podopieczny)obj;
			if(this.id == p.getId())
				return true;
			return false;
		}
		return false;	
	}

}
