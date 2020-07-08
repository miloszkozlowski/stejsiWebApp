package pl.mihome.stejsiWebApp.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;



@Entity
@Table(name = "treningi")
//@CheckCallendarAvability - this validator cannot be used due to input from user with date and time . Validation is done on service level.  
public class Trening extends AuditBase {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "termin")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime scheduledFor;
	
	@Column(name = "kiedy_odbyty")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime markedAsDone;
	
	@Column(name = "kiedy_potwierdzony")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime scheduleConfirmed;
	
	@Column(name = "kiedy_potwierdzona_obecnosc")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime presenceConfirmedByUser;
	
	@Column(name = "kiedy_odwolany")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime whenCanceled;
	
	@JsonIgnore
	private LocalDateTime userNotified;
	
	@ManyToOne
	@JoinColumn(name = "pakiet_id")
	//@JsonBackReference
	private PakietTreningow trainingPackage;
	
	@ManyToOne
	@JoinColumn(name = "gdzie")
	private Lokalizacja location;
	
	public Trening() {
	}
	
	public Trening(PakietTreningow pakiet)
	{
		this();
		this.trainingPackage = pakiet;
	}

	public LocalDateTime getScheduledFor() {
		return scheduledFor;
	}

	public void setScheduledFor(LocalDateTime scheduledFor) {
		this.scheduledFor = scheduledFor;
	}

	public LocalDateTime getMarkedAsDone() {
		return markedAsDone;
	}

	public void setMarkedAsDone(LocalDateTime markedAsDone) {
		this.markedAsDone = markedAsDone;
	}

	public LocalDateTime getPresenceConfirmedByUser() {
		return presenceConfirmedByUser;
	}

	public void setPresenceConfirmedByUser(LocalDateTime presenceConfirmedByUser) {
		this.presenceConfirmedByUser = presenceConfirmedByUser;
	}

	public LocalDateTime getWhenCanceled() {
		return whenCanceled;
	}

	public void setWhenCanceled(LocalDateTime whenCanceled) {
		this.whenCanceled = whenCanceled;
	}

	public PakietTreningow getTrainingPackage() {
		return trainingPackage;
	}

	public void setTrainingPackage(PakietTreningow trainingPackage) {
		this.trainingPackage = trainingPackage;
	}

	public long getId() {
		return id;
	}
	
	
	public Lokalizacja getLocation() {
		return location;
	}

	public void setLocation(Lokalizacja location) {
		this.location = location;
	}

	
	public LocalDateTime getScheduleConfirmed() {
		return scheduleConfirmed;
	}

	public void setScheduleConfirmed(LocalDateTime scheduleConfirmed) {
		this.scheduleConfirmed = scheduleConfirmed;
	}

	public LocalDateTime getUserNotified() {
		return userNotified;
	}

	public void setUserNotified(LocalDateTime usesNotified) {
		this.userNotified = usesNotified;
	}
	

}
