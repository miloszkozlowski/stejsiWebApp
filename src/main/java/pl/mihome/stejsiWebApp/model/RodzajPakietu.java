package pl.mihome.stejsiWebApp.model;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "rodzaje_pakietow")
public class RodzajPakietu extends AuditBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Column(name = "nazwa")
	@Size(max = 30)
	private String title;
	
	@Column(name = "opis", columnDefinition = "TEXT")
	@Size(max = 65535)
	private String description;
	
	@NotNull
	@Column(name = "ilosc_treningow")
	@Min(1)
	private Integer amountOfTrainings;
	
	@Column(name = "dlugosc_minuty")
	private Integer lengthMinutes;
	
	@Column(name = "cena")
	@Min(0)
	private BigDecimal pricePLN;
	
	@Column(name = "aktywny")
	private boolean active;
	
	private Integer daysValid;
	
	@OneToMany(mappedBy = "packageType")
	@JsonIgnoreProperties
	private Set<PakietTreningow> pakiety;
	
	public RodzajPakietu() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String decription) {
		this.description = decription;
	}

	public Integer getLengthMinutes() {
		return lengthMinutes;
	}

	public BigDecimal getPricePLN() {
		return pricePLN;
	}

	public void setLengthMinutes(Integer lengthMinutes) {
		this.lengthMinutes = lengthMinutes;
	}

	public void setPricePLN(BigDecimal pricePLN) {
		this.pricePLN = pricePLN;
	}

	Set<PakietTreningow> getPakiety() {
		return pakiety;
	}

	public void setPakiety(Set<PakietTreningow> pakiety) {
		this.pakiety = pakiety;
	}

	public Long getId() {
		return id;
	}

	public int getAmountOfTrainings() {
		return amountOfTrainings;
	}

	public void setAmountOfTrainings(Integer amountOfTrenings) {
		this.amountOfTrainings = amountOfTrenings;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	

	public Integer getDaysValid() {
		return daysValid;
	}

	public void setDaysValid(Integer daysValid) {
		this.daysValid = daysValid;
	}
	
	
}
