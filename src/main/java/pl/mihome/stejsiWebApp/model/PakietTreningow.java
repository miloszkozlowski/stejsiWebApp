package pl.mihome.stejsiWebApp.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "pakiety")
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id",
		scope = PakietTreningow.class)
public class PakietTreningow extends AuditBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private boolean paid;
	
	@NotNull 
	private boolean closed;

	@ManyToOne
	@JoinColumn(name = "owner", referencedColumnName = "id")
	@NotNull
	//@JsonBackReference
	private Podopieczny owner;
	
	@ManyToOne
	@JoinColumn(name = "rodzaj_id", referencedColumnName = "id")
	@NotNull
	private RodzajPakietu packageType;
	
	
	@OneToMany(mappedBy = "trainingPackage", fetch = FetchType.EAGER)
	@BatchSize(size = 12)
	//@JsonManagedReference
	private Set<Trening> trainings;

	
	public PakietTreningow() {
		}
	
	public PakietTreningow(Podopieczny owner, RodzajPakietu type) {
		this.owner = owner;
		this.packageType = type;
	}
	

	public Long getId() {
		return id;
	}

	public Podopieczny getOwner() {
		return owner;
	}

	public void setOwner(Podopieczny owner) {
		this.owner = owner;
		owner.getTrainingPackages().add(this);
	}

	public RodzajPakietu getPackageType() {
		return packageType;
	}


	public void setPackageType(RodzajPakietu packageType) {
		this.packageType = packageType;
	}


	public Set<Trening> getTrainings() {
		return trainings;
	}

	public void setTrainings(Set<Trening> trainings) {
		this.trainings = trainings;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}	

	
}
