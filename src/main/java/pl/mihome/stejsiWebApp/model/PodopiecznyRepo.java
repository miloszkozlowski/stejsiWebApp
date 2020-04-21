package pl.mihome.stejsiWebApp.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PodopiecznyRepo {
	
	List<Podopieczny> findAll();
	
	Page<Podopieczny> findAll(Pageable page);
	
	Optional<Podopieczny> findById(Long id);
	
	Podopieczny save(Podopieczny user);
	
	boolean existsById(Long id);
}
