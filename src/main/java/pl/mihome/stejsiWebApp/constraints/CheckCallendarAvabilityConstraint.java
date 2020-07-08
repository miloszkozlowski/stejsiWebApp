package pl.mihome.stejsiWebApp.constraints;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import pl.mihome.stejsiWebApp.DTO.TreningReadModel;
import pl.mihome.stejsiWebApp.model.Trening;
import pl.mihome.stejsiWebApp.model.TreningRepo;

/*
 * This constraint is not used in entity Trening due to input format (gets date and time separately).
 */
public class CheckCallendarAvabilityConstraint implements ConstraintValidator<CheckCallendarAvability, Trening> {

	@Autowired
	private TreningRepo repo;

	@Override
	public boolean isValid(Trening value, ConstraintValidatorContext context) {
		var trainingDTO = new TreningReadModel(value);
		
		final LocalDateTime validatedStart = trainingDTO.getScheduledFor();
		final LocalDateTime validatedEnd = trainingDTO.getTrainingEndInclusive();
		
		
		List<Trening> rawListOfTrainings = repo.findByScheduledForGreaterThanEqualAndScheduledForLessThan(validatedStart.minusDays(1), validatedEnd.plusDays(1));
		
		List<TreningReadModel> toCheckWith = rawListOfTrainings.stream()
				.map(TreningReadModel::new)
				.collect(Collectors.toList());
		
		return toCheckWith.stream()
				.noneMatch(t -> overlaps(validatedStart, validatedEnd, t.getScheduledFor(), t.getTrainingEndInclusive()));
		
	}
	
	boolean overlaps(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
		return start1.isBefore(end2) && start2.isBefore(end1);
	}



}
