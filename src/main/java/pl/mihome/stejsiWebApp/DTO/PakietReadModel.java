package pl.mihome.stejsiWebApp.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import pl.mihome.stejsiWebApp.model.PakietTreningow;
import pl.mihome.stejsiWebApp.model.RodzajPakietu;
import pl.mihome.stejsiWebApp.model.Trening;

public class PakietReadModel {

	private Long id;
	private PodopiecznyReadModel owner;
	private RodzajPakietu packageType;
	private List<TreningReadModel> trainings;
	private Boolean paid;
	private LocalDateTime created;
	
	
	public PakietReadModel(PakietTreningow pakiet, PodopiecznyReadModel user) {
		this.id = pakiet.getId();
		this.owner = user;
		this.packageType = pakiet.getPackageType();
		this.trainings = pakiet.getTrainings().stream()
				.sorted(Comparator.comparing(Trening::getScheduledFor, Comparator.nullsLast(Comparator.reverseOrder())))
				.map(TreningReadModel::new)
				.collect(Collectors.toList());
		this.paid = pakiet.isPaid();
		this.created = pakiet.getWhenCreated();
	}
	
	public PakietReadModel(PakietTreningow pakiet) {
		this.id = pakiet.getId();
		this.owner = new PodopiecznyReadModel(pakiet.getOwner());
		this.packageType = pakiet.getPackageType();
		this.trainings = pakiet.getTrainings().stream()
				.sorted(Comparator.comparing(Trening::getScheduledFor, Comparator.nullsLast(Comparator.naturalOrder())))
				.map(TreningReadModel::new)
				.collect(Collectors.toList());
		this.paid = pakiet.isPaid();
		this.created = pakiet.getWhenCreated();
	}
	
	
	

	public List<TreningReadModel> getTrainings() {
		return trainings;
	}


	public Long getId() {
		return id;
	}

	public PodopiecznyReadModel getOwner() {
		return owner;
	}

	public RodzajPakietu getPackageType() {
		return packageType;
	}
	
	
	public boolean isPaid() {
		return paid;
	}


	public LocalDateTime getCreated() {
		return created;
	}
	
	public String getReadableCreated() {
		var format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return created.format(format);
	}


	public Long getAmountTrainingsPlanned() {
		var amount = trainings.stream()
		.filter(t -> t.getScheduledFor() != null)
		.count();
		return amount;

	}
	
	public Long getAmountTrainingsDone() {
		var amount = trainings.stream()
		.filter(t -> t.isDone())
		.count();
		return amount;
	}
	
	public Long getAmountOfPresenceConfirmations() {
		return trainings.stream()
				.filter(t -> t.isPresenceConfirmed())
				.count();
	}
	
	public int getValidityDays() {
		LocalDate today = LocalDate.now();
		LocalDate created = getCreated().toLocalDate();
		var validTo = created.plusDays(getPackageType().getDaysValid());
		Period period = Period.between(today, validTo);
		return period.getDays();
	}
	
	public Boolean isValid() {
		if(packageType.getDaysValid() == 0)
			return true;
		else
			return getValidityDays() >= 0;
					
	}
	
	public Boolean isValidIndefinetely() {
		return packageType.getDaysValid() == 0;
	}
	
	public Boolean isDone() {
		return trainings.stream()
		.filter(t -> !t.isDone())
		.count() == 0;
	}
	
	public String whenDoneReadable() {
		if(this.isDone()) {
			final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return trainings.stream()
			.sorted(Comparator.comparing(TreningReadModel::getMarkedAsDone).reversed())
			.findFirst()
			.map(t -> t.getMarkedAsDone().format(dtf))
			.orElse("");
		}
		return "";
	}
	
	
}
