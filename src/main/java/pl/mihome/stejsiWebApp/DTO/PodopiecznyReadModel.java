package pl.mihome.stejsiWebApp.DTO;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import pl.mihome.stejsiWebApp.DTO.appComms.TipCommentReadModel;
import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.TrainingStatus;
import pl.mihome.stejsiWebApp.model.appClient.UserRank;

@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id",
		scope = PodopiecznyReadModel.class)
public class PodopiecznyReadModel {
	
	private Long id;
	private String name;
	private String surname;
	private String email;
	private int phoneNumber;
	private boolean active;
	private boolean settingTipNotifications;
	private List<PakietReadModel> trainingPackages;
	private List<TipCommentReadModel> tipComments;
	
	@JsonIgnore
	private Podopieczny rootSource;

	public PodopiecznyReadModel(Podopieczny source) {
		this.rootSource = source;
		this.id = source.getId();
		this.name = source.getImie();
		this.surname = source.getNazwisko();
		this.email = source.getEmail();
		this.phoneNumber = source.getPhoneNumber();
		settingTipNotifications = source.isSettingTipNotifications();
		this.active = source.isAktywny();
		if(source.getTrainingPackages() == null)
			this.trainingPackages = List.of();
		else {
			this.trainingPackages = source.getTrainingPackages().stream()
					.map(p -> new PakietReadModel(p, this))
					.sorted(Comparator.comparing(PakietReadModel::getValidityDays))
					.collect(Collectors.toList());
		}
		
		if(source.getComments() == null) {
			this.tipComments = List.of();
		}
		else {
		this.tipComments = source.getComments().stream()
				.map(TipCommentReadModel::new)
				.sorted(Comparator.comparing(TipCommentReadModel::getWhenCreated))
				.collect(Collectors.toList());
		}
				
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
	

	public int getPhoneNumber() {
		return phoneNumber;
	}
	
	
	public boolean isSettingTipNotifications() {
		return settingTipNotifications;
	}

	public List<PakietReadModel> getTrainingPackages() {
		return trainingPackages;
	}

	public boolean isActive() {
		return active;
	}
	
	
	public Podopieczny getRootSource() {
		return rootSource;
	}

	public List<PakietReadModel> getActivePackages() {
		return trainingPackages.stream()
		.filter(p -> !p.isClosed() && (p.getValidityDays() >= 0 || p.isValidIndefinetely()))
		.sorted(Comparator.comparing(PakietReadModel::isValidIndefinetely).thenComparing(Comparator.comparing(PakietReadModel::getValidityDays)))
		.collect(Collectors.toList());
	}
	
	public List<PakietReadModel> getInactivePackages() {
		return trainingPackages.stream()
		.filter(p -> p.isClosed() || (p.getValidityDays() < 0 && !p.isValidIndefinetely()))
		.sorted(Comparator.comparing(PakietReadModel::whenDoneReadable))
		.collect(Collectors.toList());
	}
	

	public Integer getTotalTrainingsDone() {
		if(getTrainingPackages() != null) {
			return getTrainingPackages().stream()
					.mapToInt(p -> p.getAmountTrainingsDone().intValue())
					.sum();
		}
		return 0;
	}

	public float getLastFourWeeksAvgTrainingsDone() {
		if(getTrainingPackages() != null) {
			var dateFourWeeksAgo = LocalDateTime.now().minusDays(28);
			var trainings = getTrainingPackages().stream()
				.flatMap(p -> p.getTrainings().stream())
				.filter(t -> t.isDone() && t.getScheduledFor().isAfter(dateFourWeeksAgo))
				.count();
			return trainings / 4;
		}
		return 0;
	}

	
	public List<TipCommentReadModel> getTipComments() {
		return tipComments;
	}

	public Integer getUnconfirmedTrainings() {
		if(getTrainingPackages() != null) {
			Long amount = getTrainingPackages().stream()
					.flatMap(p -> p.getTrainings().stream())
					.filter(t -> !t.isConfirmed() || t.getStatus() == TrainingStatus.PRESENCE_TO_CONFIRM)
					.count();
			return amount.intValue();
		}
		return 0;
	}
	
	@JsonIgnore
	public UserRank getRank() {
		/*
		 * Punkty do zdobycia: 2 za wykonane ogółem, 5 za średnią, 3 za komentarze, 1 za potwierdzenia
		 * Suma punktów do zdobycia: 11
		 * 
		 * Rankingi:
		 * Kulturysta od 10,5
		 * Osiłek od 9
		 * Fit Malina od 6,5
		 * Beginner od 3
		 * Leser od 0
		 */
		float points = getProgressPoints();
		
		if(points > 10.5)
			return UserRank.KULTURYSTA;
		if(points > 9)
			return UserRank.OSIŁEK;
		if(points > 6.5)
			return UserRank.FIT_MALINA;
		if(points > 3)
			return UserRank.BEGINNER;
		
		return UserRank.LESER;
		
	}
	
	public float getProgressPoints() {
		float points = 0;
		
		
		//wyliczanie punktów za wykonane treningi
		var trainingsDone = getTotalTrainingsDone();
		if(trainingsDone > 100) {
			points = (float)((trainingsDone - 100)*0.005);
			trainingsDone = trainingsDone - (trainingsDone - 100);
		}
		if(trainingsDone > 80) {
			points = (float)((trainingsDone - 80)*0.01);
			trainingsDone = trainingsDone - (trainingsDone - 80);
		}
		if(trainingsDone > 60) {
			points = (float)((trainingsDone - 60)*0.0175);
			trainingsDone = trainingsDone - (trainingsDone - 60);
		}
		if(trainingsDone > 40) {
			points = (float)((trainingsDone - 40)*0.02);
			trainingsDone = trainingsDone - (trainingsDone - 40);
		}
		if(trainingsDone > 20) {
			points = (float)((trainingsDone - 20)*0.0225);
			trainingsDone = trainingsDone - (trainingsDone - 20);
		}
		if(trainingsDone > 0) {
			points = (float)(trainingsDone*0.03);
		}
		
		//wyliczanie punktów za średnią tygodniową
		var avg = getLastFourWeeksAvgTrainingsDone();
		if(avg >= 5)
			points = points + 5;
		else if(avg >= 4)
			points = points + 4;
		else if(avg >= 3)
			points = points + 3;
		else if(avg >= 2)
			points = points + 2;
		else if(avg >= 1)
			points = points + 1;
		
		//wyliczanie punktów za komentarze
		if(getTipComments() != null) {
			var fourWeeksAgo = LocalDateTime.now().minusDays(28);
			var lastComments = getTipComments().stream()
					.filter(c -> c.getWhenCreated().isAfter(fourWeeksAgo))
					.count();
			if(lastComments > 25)
				points = points + 3;
			else if(lastComments > 18)
				points = (float)(points + 2.5);
			else if(lastComments > 12)
				points = points + 2;
			else if(lastComments > 7)
				points = (float)(points + 1.5);
			else if(lastComments > 3)
				points = points + 1;
			else if(lastComments > 1)
				points = (float)(points + 0.5);
		}
		
		//wyliczanie punktów za niepotwierdzone treningi
		var uncofnrimed = getUnconfirmedTrainings();
		switch(uncofnrimed) {
		case 1:
			points = points + 1;
			break;
		case 2:
			points = (float)(points + 0.5);
			break;
		case 3:
			points = (float)(points + 0.25);
			break;
		}
		
		return points;
		
	}
	
	
	
	
	
}
