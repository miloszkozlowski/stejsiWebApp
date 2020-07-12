package pl.mihome.stejsiWebApp.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import pl.mihome.stejsiWebApp.model.PakietTreningow;
import pl.mihome.stejsiWebApp.model.RodzajPakietu;
import pl.mihome.stejsiWebApp.model.Trening;

@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id",
		scope = PakietReadModel.class)
public class PakietReadModel {

	private Long id;
	private PodopiecznyReadModel owner;
	private RodzajPakietu packageType;
	private List<TreningReadModel> trainings;
	private Boolean paid;
	private Boolean closed;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime whenCreated;
	
	
	public PakietReadModel(PakietTreningow pakiet, PodopiecznyReadModel user) {
		this.id = pakiet.getId();
		this.owner = user;
		this.packageType = pakiet.getPackageType();
		this.trainings = pakiet.getTrainings().stream()
				.sorted(Comparator.comparing(Trening::getScheduledFor, Comparator.nullsLast(Comparator.reverseOrder())))
				.map(t -> new TreningReadModel(t, this))
				.collect(Collectors.toList());
		this.paid = pakiet.isPaid();
		this.closed = pakiet.isClosed();
		this.whenCreated = pakiet.getWhenCreated();
	}
	
	public PakietReadModel(PakietTreningow pakiet) {
		this.id = pakiet.getId();
		this.owner = new PodopiecznyReadModel(pakiet.getOwner());
		this.packageType = pakiet.getPackageType();
		this.trainings = pakiet.getTrainings().stream()
				.sorted(Comparator.comparing(Trening::getScheduledFor, Comparator.nullsLast(Comparator.naturalOrder())))
				.map(t -> new TreningReadModel(t, this))
				.collect(Collectors.toList());
		this.paid = pakiet.isPaid();
		this.closed = pakiet.isClosed();
		this.whenCreated = pakiet.getWhenCreated();
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

	public Boolean isClosed() {
		return closed;
	}

	public LocalDateTime getWhenCreated() {
		return whenCreated;
	}
	
	//metody supportujące

	@JsonIgnore
	public String getReadableCreated() {
		var format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return whenCreated.format(format);
	}

	@JsonIgnore
	public Long getAmountTrainingsPlanned() {
		var amount = trainings.stream()
		.filter(t -> t.getScheduledFor() != null)
		.count();
		return amount;

	}
	
	@JsonIgnore
	public Long getAmountTrainigsPast() {
		var amount = trainings.stream()
				.filter(t -> t.getWhenCanceled() == null && t.getScheduledFor() != null)
				.filter(t -> t.getScheduledFor().isBefore(LocalDateTime.now()))
				.count();
		return amount;
	}
	
	@JsonIgnore
	public Long getAmountTrainingsDone() {
		var amount = trainings.stream()
		.filter(t -> t.isDone())
		.count();
		return amount;
	}
	
	@JsonIgnore
	public Long getAmountOfPresenceConfirmations() {
		return trainings.stream()
				.filter(t -> t.isPresenceConfirmed())
				.count();
	}
	
	@JsonIgnore
	public int getValidityDays() {
		LocalDate today = LocalDate.now();
		LocalDate created = getWhenCreated().toLocalDate();
		var validTo = created.plusDays(getPackageType().getDaysValid());
		Period period = Period.between(today, validTo);
		return period.getDays();
	}
	
	@JsonIgnore
	public Boolean isValid() {
		if(packageType.getDaysValid() == 0)
			return true;
		else
			return getValidityDays() >= 0;
					
	}
	
	@JsonIgnore
	public Boolean isValidIndefinetely() {
		return packageType.getDaysValid() == 0;
	}
	
	@JsonIgnore
	public Boolean isDone() {
		return trainings.stream()
		.filter(t -> !t.isDone() && t.getPresenceConfirmedByUser() != null)
		.count() == 0;
	}
	
	@JsonIgnore
	public String whenDoneReadable() {
		if(this.isClosed()) {
			final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			Optional<TreningReadModel> dateReadable = trainings.stream()
					.filter(t -> t.getMarkedAsDone() != null)
					.sorted(Comparator.comparing(TreningReadModel::getMarkedAsDone).reversed())
					.findFirst();
			if(dateReadable.isPresent()) {
				if(dateReadable.get().getMarkedAsDone() != null) {
					return dateReadable.get().getMarkedAsDone().format(dtf);
				}
			}

		}
		return "";
	}
	
	
}
