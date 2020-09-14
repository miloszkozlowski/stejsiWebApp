package pl.mihome.stejsiWebApp.DTO;

import pl.mihome.stejsiWebApp.model.Trening;

import java.time.LocalDateTime;

public class CalendarItem {
    private final LocalDateTime eventStartsAt;
    private final LocalDateTime eventEndsAt;
    private final String eventName;
    private final String eventSubName;
    private final String userName;
    private final String userSurname;
    private final long userId;
    private final long trainingId;
    private final long packageId;
    private final boolean canceled;
    private final boolean confirmed;

    public CalendarItem(Trening training) {
        this.eventStartsAt = training.getScheduledFor();
        this.eventEndsAt = training.getScheduledFor().plusMinutes(training.getTrainingPackage().getPackageType().getLengthMinutes());
        this.eventName = training.getTrainingPackage().getOwner().getImie() + " " + training.getTrainingPackage().getOwner().getNazwisko();
        this.eventSubName = training.getTrainingPackage().getPackageType().getTitle();
        this.userName = training.getTrainingPackage().getOwner().getImie();
        this.userSurname = training.getTrainingPackage().getOwner().getNazwisko();
        this.userId = training.getTrainingPackage().getOwner().getId();
        this.trainingId = training.getId();
        this.packageId = training.getTrainingPackage().getId();
        this.canceled = training.getWhenCanceled() != null;
        this.confirmed = training.getScheduleConfirmed() != null;
    }

    public LocalDateTime getEventStartsAt() {
        return eventStartsAt;
    }

    public LocalDateTime getEventEndsAt() {
        return eventEndsAt;
    }

    public String getEventName() {
        return eventName;
    }

    public long getTrainingId() {
        return trainingId;
    }

    public long getPackageId() {
        return packageId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getEventSubName() {
        return eventSubName;
    }
}
