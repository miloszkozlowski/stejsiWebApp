package pl.mihome.stejsiWebApp.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RodzajPakietuRepo {
	
	List<RodzajPakietu> findAll();

	Slice<RodzajPakietu> findByRemovedIsFalse(Pageable page);
	
	Page<RodzajPakietu> findAll(Pageable page);
	
	Optional<RodzajPakietu> findByIdAndRemovedIsFalse(Long id);
	
	RodzajPakietu save(RodzajPakietu type);
	
	boolean existsById(Long id);

}
