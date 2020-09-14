package pl.mihome.stejsiWebApp.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import pl.mihome.stejsiWebApp.model.Tip;

public interface TipRepo {
	
	List<Tip> findByRemovedIsFalse();
	
	List<Tip> findByRemovedIsFalseAndUsersNotifiedIsNull();

	Page<Tip> findByRemovedIsFalse(Pageable page);
	
	Tip save(Tip source);
	
	Optional<Tip> findById(Long id);

	Optional<Tip> findByIdAndRemovedIsFalse(Long id);

	boolean existsByIdAndRemovedIsFalse(Long tipId);

	boolean existsByUsersNotifiedIsNull();

}
