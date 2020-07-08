package pl.mihome.stejsiWebApp.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TreningRepo {

	Trening save(Trening source); 
	
	Optional<Trening> findById(Long id);
	
	List<Trening> findByScheduledForGreaterThanEqualAndScheduledForLessThan(LocalDateTime from, LocalDateTime to);
}
