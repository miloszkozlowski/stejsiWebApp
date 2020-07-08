package pl.mihome.stejsiWebApp.DTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import pl.mihome.stejsiWebApp.model.Lokalizacja;
import pl.mihome.stejsiWebApp.model.PakietTreningow;
import pl.mihome.stejsiWebApp.model.TrainingStatus;
import pl.mihome.stejsiWebApp.model.Trening;

public class TreningReadModel {
	
	private long id;
	
	private LocalDateTime scheduledFor;

	private LocalDateTime markedAsDone;
	
	private LocalDateTime scheduleConfirmed;
	
	private LocalDateTime whenCanceled;
	
	private LocalDateTime presenceConfirmedByUser;
	
	private PakietTreningow trainingPackage;
	
	private Lokalizacja location;
	
	public TreningReadModel(Trening training) {
		this.id = training.getId();
		this.scheduledFor = training.getScheduledFor();
		this.markedAsDone = training.getMarkedAsDone();
		this.scheduleConfirmed = training.getScheduleConfirmed();
		this.presenceConfirmedByUser = training.getPresenceConfirmedByUser();
		this.trainingPackage = training.getTrainingPackage();
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
	
	
	
	/*
	 * 	Metody supportujące
	 */
	
	public LocalDateTime getWhenCanceled() {
		return whenCanceled;
	}




	public void setWhenCanceled(LocalDateTime whenCanceled) {
		this.whenCanceled = whenCanceled;
	}




	public boolean isDone() {
		return markedAsDone != null;
	}	
	
	public boolean isCanceled() {
		return whenCanceled != null;
	}

	public boolean isConfirmed() {
		return scheduleConfirmed != null;
	}
	
	public boolean isPresenceConfirmed() {
		return presenceConfirmedByUser != null;
	}

	public boolean isInPast() {
		return scheduledFor.isBefore(LocalDateTime.now());
	}

	public String getReadableDateScheduled() {
		return this.scheduledFor.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
	
	public String getReadableTimeScheduled() {
		return this.scheduledFor.format(DateTimeFormatter.ofPattern("HH:mm"));
	}
	
	public PakietReadModel getTrainingPackageToRead() {
		return new PakietReadModel(trainingPackage);
	}
	
	public LocalDateTime getTrainingEndInclusive() {
		return scheduledFor.plusMinutes(trainingPackage.getPackageType().getLengthMinutes()).minusSeconds(1);
	}
	
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
