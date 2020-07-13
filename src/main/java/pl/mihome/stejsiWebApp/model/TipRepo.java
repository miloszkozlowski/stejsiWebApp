package pl.mihome.stejsiWebApp.model;

import java.util.List;
import java.util.Optional;

import pl.mihome.stejsiWebApp.model.Tip;

public interface TipRepo {
	
	List<Tip> findByRemovedIsFalse();
	
	List<Tip> findByRemovedIsFalseAndUsersNotifiedIsNull();
	
	Tip save(Tip source);
	
	Optional<Tip> findById(Long id);
	
	Optional<Tip> findByIdAndRemovedIsFalse(Long id);

	boolean existsByIdAndRemovedIsFalse(Long tipId);

}
