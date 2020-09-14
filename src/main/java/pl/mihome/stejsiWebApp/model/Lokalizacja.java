package pl.mihome.stejsiWebApp.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "lokalizacje")
public class Lokalizacja extends AuditBase {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;
		
		@NotBlank(message = "Lokalizacja musi mieć nazwę")
		@Column(name = "nazwa", length = 30)
		@Length(max = 30, message = "Nazwa może mieć maksymalnie 30 znaków")
		private String name;
		
		@Column(name = "adres", length = 100)
		@Length(max = 100, message = "Adres może mieć maksymalnie 100 znaków")
		@NotBlank(message = "Lokalizacja musi zawierać adres")
		private String postalAddress;
		
		@Column(name = "domyslne")
		private Boolean defaultLocation;
		
		@OneToMany(mappedBy = "location")
		@JsonIgnore
		Set<Trening> trainings;
		
		public Lokalizacja() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPostalAddress() {
			return postalAddress;
		}

		public void setPostalAddress(String postalAddress) {
			this.postalAddress = postalAddress;
		}

		public Boolean getDefaultLocation() {
			return defaultLocation;
		}

		public void setDefaultLocation(Boolean defaultLocation) {
			this.defaultLocation = defaultLocation;
		}

		public Long getId() {
			return id;
		}

		public Set<Trening> getTrainings() {
			return trainings;
		}

		public void setTrainings(Set<Trening> trainings) {
			this.trainings = trainings;
		}
		
		
		
		
		
}
