package pl.mihome.stejsiWebApp.model;

import java.util.List;
import java.util.Optional;

public interface TipRepo {
	
	List<Tip> findByRemovedIsFalse();
	
	List<Tip> findByRemovedIsFalseAndUsersNotifiedIsNull();
	
	void save(Tip source);
	
	Optional<Tip> findById(Long id);
	
	Optional<Tip> findByIdAndRemovedIsFalse(Long id);

	boolean existsByIdAndRemovedIsFalse(Long tipId);

}
