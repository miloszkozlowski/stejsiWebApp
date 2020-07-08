package pl.mihome.stejsiWebApp.DTO;

import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import pl.mihome.stejsiWebApp.model.RodzajPakietu;

public class RodzajPakietuWriteModel {

	@NotBlank(message = "Pakiet musi mieć nazwę")
	@Size(max = 30, message = "Nazwa może mieć najwięcej 30 znaków")
	private String title;
	
	private String description;
	
	@NotNull(message = "Podaj ilość reningów w pakiecie")
	@Min(value = 1, message = "Pakiet musi mieć przynajmniej jeden trening")
	@Max(value = 100, message = "Pakiet nie może mieć tak wiele treningów")
	private Integer amount;
	
	@Min(value = 1, message = "Trening musi trwać przynajmniej jedną minutę")
	@Max(value = 600, message = "Nie żartuj, nikt nie wytrzyma tak długo...")
	@NotNull(message = "Podaj długość treningu")
	private Integer length;
	
	@Min(value = 0, message = "Określ długość ważności pakietu (wpisz 0 jeżeli bezterminowo)")
	@NotNull(message = "Określ długość ważności pakietu (wpisz 0 jeżeli bezterminowo)")
	private Integer validity;
	
	@NotNull(message = "Podaj cenę całego pakietu")
	private BigDecimal pricePLN;
	
	public RodzajPakietuWriteModel() {
	}

	
	
	public Integer getValidity() {
		return validity;
	}



	public void setValidity(Integer validity) {
		this.validity = validity;
	}



	public String getTitle() {
		return title;
	}



	public String getDescription() {
		return description;
	}



	public Integer getAmount() {
		return amount;
	}



	public Integer getLength() {
		return length;
	}



	public BigDecimal getPricePLN() {
		return pricePLN;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public void setAmount(Integer amount) {
		this.amount = amount;
	}



	public void setLength(Integer length) {
		this.length = length;
	}



	public void setPricePLN(BigDecimal pricePLN) {
		this.pricePLN = pricePLN;
	}



	public RodzajPakietu toRodzajPakietu() {
		var result = new RodzajPakietu();
		result.setTitle(this.title);
		result.setDescription(this.description);
		result.setAmountOfTrainings(this.amount);
		result.setLengthMinutes(this.length);
		result.setPricePLN(this.pricePLN);
		result.setDaysValid(this.validity);
		return result;
	}
	
}
