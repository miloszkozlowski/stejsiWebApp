package pl.mihome.stejsiWebApp.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import pl.mihome.stejsiWebApp.constraints.CheckEmailAvability;
import pl.mihome.stejsiWebApp.constraints.CheckPhoneNumberAvability;
import pl.mihome.stejsiWebApp.model.Podopieczny;

public class PodopiecznyWriteModel {

	@NotBlank(message = "Pole imię nie może być puste")
	@Size(min = 3, max = 30, message = "Imię powinno zawierać od 3 do 30 znaków")
	private String name;
	
	@NotBlank(message = "Pole naziwsko nie może być puste.")
	@Size(min = 3, max = 50, message = "Nazwisko powinno zawierać od 3 do 50 znaków.")
	private String surname;
	
	@NotEmpty(message = "Pole email nie może być puste.")
	@Email(message = "Pole e-email musi zawierać prawidłowy adres e-mail.")
	@Size(max = 200, message = "Pole email musi zawierać nie więcej niż 200 znaków.")
	@CheckEmailAvability
	private String email;
	
	@Pattern(regexp = "[5-8][0-9]{8}", message = "Numer telefonu powinien składać się 9 cyfr")
	@CheckPhoneNumberAvability
	private String phoneNumber;
	
	public PodopiecznyWriteModel() {
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setEmail(String email) {
		this.email = email.toLowerCase();
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public Podopieczny toPodopieczny() {
		var zapis = new Podopieczny();
		zapis.setImie(this.name);
		zapis.setNazwisko(this.surname);
		zapis.setEmail(this.email);
		zapis.setPhoneNumber(Integer.parseInt(this.phoneNumber));
		zapis.setSettingTipNotifications(true);
		return zapis;
	}
}
