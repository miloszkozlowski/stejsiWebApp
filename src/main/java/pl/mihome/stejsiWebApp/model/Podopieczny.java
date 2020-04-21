package pl.mihome.stejsiWebApp.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.boot.context.properties.bind.DefaultValue;


@Entity
@Table(name = "uzytkownicy")
public class Podopieczny {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message = "Pole imię nie może być puste")
	@Size(min = 3)
	private String imie;
	
	@NotEmpty(message = "Pole nazwisko nie może być puste")
	@Size(min = 3)
	private String nazwisko;
	
	@NotEmpty(message = "Pole email nie może być puste")
	@Email(message = "Pole e-email musi zawierać prawidłowy adres e-mail")
	private String email;
	
	
	private Date dataRejestracji;
	
	private boolean aktywny = false;
	
	private boolean usuniety = false;
	
	public Podopieczny()
	{
		this.dataRejestracji = new Date();
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
	public boolean isUsuniety() {
		return usuniety;
	}
	public void setUsuniety(boolean usuniety) {
		this.usuniety = usuniety;
	}
	public Date getDataRejestracji() {
		return dataRejestracji;
	}
	public void setDataRejestracji(Date dataRejestracji) {
		this.dataRejestracji = dataRejestracji;
	}
	
	
	

}
