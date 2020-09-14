package pl.mihome.stejsiWebApp.model;

public enum TrainingStatus {
    DONE("wykonany"),
    CANCELED("odwołany"),
    SCHEDULE_TO_CONFIRM("niepotwierdzony"),
    PRESENCE_TO_CONFIRM("czeka na podopiecznego"),
    UNPLANNED("niezaplanowany"),
    READY_TO_HAPPEN("potwierdzony"),
    UNKNOWN("czeka na Ciebie...");

    private final String description;

    private TrainingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
