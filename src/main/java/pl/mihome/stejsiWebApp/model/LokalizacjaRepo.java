package pl.mihome.stejsiWebApp.model;

import java.util.List;
import java.util.Optional;

public interface LokalizacjaRepo {

	List<Lokalizacja> findAll();
	
	List<Lokalizacja> findAllByRemovedIsFalse();
	
	Optional<Lokalizacja> findById(Long id);
	
	Lokalizacja save(Lokalizacja source);
	
	Optional<Lokalizacja> findByDefaultLocationIsTrue();
	
	void setOneDefault(Long id);
	
}
