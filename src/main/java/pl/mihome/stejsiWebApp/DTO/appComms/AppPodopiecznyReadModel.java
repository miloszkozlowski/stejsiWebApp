package pl.mihome.stejsiWebApp.DTO.appComms;

import java.util.List;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import pl.mihome.stejsiWebApp.DTO.PakietReadModel;
import pl.mihome.stejsiWebApp.DTO.PodopiecznyReadModel;
import pl.mihome.stejsiWebApp.model.appClient.UserRank;

@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id",
		scope = AppPodopiecznyReadModel.class)
public class AppPodopiecznyReadModel {
	
	@Id
	private Long id;
	private String name;
	private String surname;
	private String email;
	private Integer phoneNumber;
	private boolean active;
	private Integer totalTrainingsDone;
	private float lastFourWeeksAvgTrainingsDone;
	private Integer unconfirmedTrainigs;
	private float progressPoints;
	private UserRank rank;
	
	private boolean settingTipNotifications;
	
	private List<PakietReadModel> trainingPackages;
	private List<TipCommentReadModel> tipComments;
	
	public AppPodopiecznyReadModel(PodopiecznyReadModel source) {
		this.id = source.getId();
		this.name = source.getName();
		this.surname = source.getSurname();
		this.email = source.getEmail();
		this.phoneNumber = source.getPhoneNumber();
		this.active = source.isActive();
		this.totalTrainingsDone = source.getTotalTrainingsDone();
		this.lastFourWeeksAvgTrainingsDone = source.getLastFourWeeksAvgTrainingsDone();
		this.unconfirmedTrainigs = source.getUnconfirmedTrainings();
		this.progressPoints = source.getProgressPoints();
		this.rank = source.getRank();
		
		this.settingTipNotifications = source.isSettingTipNotifications();
		
		this.trainingPackages = source.getTrainingPackages();
		this.tipComments = source.getTipComments();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getEmail() {
		return email;
	}

	public Integer getPhoneNumber() {
		return phoneNumber;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isSettingTipNotifications() {
		return settingTipNotifications;
	}

	public Integer getTotalTrainingsDone() {
		return totalTrainingsDone;
	}

	public float getLastFourWeeksAvgTrainingsDone() {
		return lastFourWeeksAvgTrainingsDone;
	}

	public Integer getUnconfirmedTrainigs() {
		return unconfirmedTrainigs;
	}

	public float getProgressPoints() {
		return progressPoints;
	}

	public UserRank getRank() {
		return rank;
	}

	public List<PakietReadModel> getTrainingPackages() {
		return trainingPackages;
	}

	public List<TipCommentReadModel> getTipComments() {
		return tipComments;
	}
	
	

}
