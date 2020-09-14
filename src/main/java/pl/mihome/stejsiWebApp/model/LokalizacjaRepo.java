package pl.mihome.stejsiWebApp.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface LokalizacjaRepo {

	List<Lokalizacja> findAll();
	
	List<Lokalizacja> findAllByRemovedIsFalse();

	Slice<Lokalizacja> findByRemovedIsFalse(Pageable page);
	
	Optional<Lokalizacja> findById(Long id);

	Optional<Lokalizacja> findByIdAndRemovedIsFalse(Long id);
	
	Lokalizacja save(Lokalizacja source);
	
	Optional<Lokalizacja> findByDefaultLocationIsTrue();
	
	void setOneDefault(Long id);
	
}
