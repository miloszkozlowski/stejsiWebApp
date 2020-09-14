package pl.mihome.stejsiWebApp.DTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import pl.mihome.stejsiWebApp.model.Lokalizacja;
import pl.mihome.stejsiWebApp.model.TrainingStatus;
import pl.mihome.stejsiWebApp.model.Trening;

public class TreningReadModel {
	
	private long id;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime scheduledFor;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime markedAsDone;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime scheduleConfirmed;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime whenCanceled;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime presenceConfirmedByUser;
	
	private PakietReadModel trainingPackage;
	
	private Lokalizacja location;
	
	public TreningReadModel(Trening training, PakietReadModel pakiet) {
		this.id = training.getId();
		this.scheduledFor = training.getScheduledFor();
		this.markedAsDone = training.getMarkedAsDone();
		this.scheduleConfirmed = training.getScheduleConfirmed();
		this.presenceConfirmedByUser = training.getPresenceConfirmedByUser();
		this.trainingPackage = pakiet;
		this.whenCanceled = training.getWhenCanceled();
		this.location = training.getLocation();
	}
	
	public TreningReadModel(Trening training) {
		this.id = training.getId();
		this.scheduledFor = training.getScheduledFor();
		this.markedAsDone = training.getMarkedAsDone();
		this.scheduleConfirmed = training.getScheduleConfirmed();
		this.presenceConfirmedByUser = training.getPresenceConfirmedByUser();
		this.trainingPackage = new PakietReadModel(training.getTrainingPackage());
		this.whenCanceled = training.getWhenCanceled();
		this.location = training.getLocation();
	}
	
	
	

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TreningReadModel) {
			var training = (TreningReadModel)obj;
			return (id == training.getId() &&
					scheduledFor.equals(training.getScheduledFor()) &&
					markedAsDone.equals(training.getMarkedAsDone()) &&
					scheduleConfirmed.equals(training.getScheduleConfirmed()) &&
					whenCanceled.equals(training.whenCanceled) &&
					presenceConfirmedByUser.equals(training.getPresenceConfirmedByUser()) &&
					location.equals(training.getLocation()));
		}
		return false;
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


	public PakietReadModel getTrainingPackage() {
		return trainingPackage;
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
	
	
	
	/*
	 * 	Metody supportujące
	 */
	
	@JsonIgnore
	public LocalDateTime getWhenCanceled() {
		return whenCanceled;
	}

	@JsonIgnore
	public void setWhenCanceled(LocalDateTime whenCanceled) {
		this.whenCanceled = whenCanceled;
	}

	@JsonIgnore
	public boolean isDone() {
		return markedAsDone != null;
	}	
	
	@JsonIgnore
	public boolean isCanceled() {
		return whenCanceled != null;
	}

	@JsonIgnore
	public boolean isConfirmed() {
		return scheduleConfirmed != null;
	}
	
	@JsonIgnore
	public boolean isPresenceConfirmed() {
		return presenceConfirmedByUser != null;
	}

	@JsonIgnore
	public boolean isInPast() {
		return scheduledFor.isBefore(LocalDateTime.now());
	}
	
	@JsonIgnore
	public LocalDateTime getEndDateTime() {
		return this.scheduledFor.plusMinutes(getTrainingPackage().getPackageType().getLengthMinutes());
	}

	@JsonIgnore
	public String getReadableDateScheduled() {
		return this.scheduledFor.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
	
	@JsonIgnore
	public String getReadableTimeScheduled() {
		return this.scheduledFor.format(DateTimeFormatter.ofPattern("HH:mm"));
	}
	
	@JsonIgnore
	public String getReadableEndDateScheduled() {
		
		return getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
	
	@JsonIgnore
	public String getReadableEndTimeScheduled() {
		return getEndDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
	}
	
	@JsonIgnore
	public LocalDateTime getTrainingEndInclusive() {
		return getEndDateTime().minusSeconds(1);
	}
	
	@JsonIgnore
	public String getLocationInGoogleCalendarFormat() {
		if(getLocation() != null) {
			//var string = getLocation().getName() + ", " + getLocation().getPostalAddress();
			//return string.replace(" ", "+"); //nieużyteczne, bo thymeleaf przekształca + na %2B
			return getLocation().getName() + ", " + getLocation().getPostalAddress();
		}
		return "";
	}
	
	@JsonIgnore
	public TrainingStatus getStatus() {
       
        LocalDateTime now = LocalDateTime.now();
        if(scheduledFor == null) {
            return TrainingStatus.UNPLANNED;
        }

        if(whenCanceled != null) {
            return TrainingStatus.CANCELED;
        }

        if(scheduleConfirmed == null && scheduledFor.isAfter(now)) {
            return TrainingStatus.SCHEDULE_TO_CONFIRM;
        }

        if(scheduledFor.isAfter(now)) {
            return TrainingStatus.READY_TO_HAPPEN;
        }

        if(scheduledFor.isBefore(now) && presenceConfirmedByUser == null) {
            return TrainingStatus.PRESENCE_TO_CONFIRM;
        }

        if(scheduledFor.isBefore(now) && markedAsDone == null) {
            return TrainingStatus.UNKNOWN;
        }

        return TrainingStatus.DONE;
    }

}
