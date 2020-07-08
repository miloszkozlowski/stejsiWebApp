package pl.mihome.stejsiWebApp.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RodzajPakietuRepo {
	
	List<RodzajPakietu> findAll();
	
	Page<RodzajPakietu> findAll(Pageable page);
	
	Optional<RodzajPakietu> findById(Long id);
	
	RodzajPakietu save(RodzajPakietu type);
	
	boolean existsById(Long id);

}
