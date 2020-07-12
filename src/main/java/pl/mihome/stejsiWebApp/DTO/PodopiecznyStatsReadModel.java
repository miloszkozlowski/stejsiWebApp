package pl.mihome.stejsiWebApp.DTO;

import pl.mihome.stejsiWebApp.model.appClient.UserRank;

public class PodopiecznyStatsReadModel {

	private Integer totalTrainingsDone;
	private float lastFourWeeksAvgTrainingsDone;
	private Integer unconfirmedTrainings;
	private float progressPoints;
	private UserRank rank;
	
	public PodopiecznyStatsReadModel(PodopiecznyReadModel source) {
		this.totalTrainingsDone = source.getTotalTrainingsDone();
		this.lastFourWeeksAvgTrainingsDone = source.getLastFourWeeksAvgTrainingsDone();
		this.unconfirmedTrainings = source.getUnconfirmedTrainings();
		this.progressPoints = source.getProgressPoints();
		this.rank = source.getRank();
	}

	public Integer getTotalTrainingsDone() {
		return totalTrainingsDone;
	}

	public float getLastFourWeeksAvgTrainingsDone() {
		return lastFourWeeksAvgTrainingsDone;
	}

	public Integer getUnconfirmedTrainings() {
		return unconfirmedTrainings;
	}

	public float getProgressPoints() {
		return progressPoints;
	}

	public UserRank getRank() {
		return rank;
	}
	
	
}
