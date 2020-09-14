package pl.mihome.stejsiWebApp.model;

import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TreningRepo {

	Trening save(Trening source); 
	
	Optional<Trening> findById(Long id);
	
	List<Trening> findByScheduledForGreaterThanEqualAndScheduledForLessThan(LocalDateTime from, LocalDateTime to);
	
	List<Trening> findByUserNotifiedIsNullAndScheduledForIsNotNullAndScheduleConfirmedIsNullAndScheduledForIsGreaterThan(LocalDateTime now);
	
	List<Trening> findByScheduledForIsNotNullAndPresenceConfirmedByUserIsNullAndScheduledForIsLessThan(LocalDateTime trainingEnd);
	
	List<Trening> findByScheduledForIsNotNullAndPresenceConfirmedByUserIsNullAndUserNotifiedIsNotNullAndUserNotifiedIsLessThan(LocalDateTime twoDaysBeforeNow);

	List<Trening> findByWhenCanceledIsNotNull();
}
