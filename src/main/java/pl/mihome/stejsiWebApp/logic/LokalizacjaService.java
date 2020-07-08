package pl.mihome.stejsiWebApp.logic;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.Lokalizacja;
import pl.mihome.stejsiWebApp.model.LokalizacjaRepo;

@Service
public class LokalizacjaService {

	private LokalizacjaRepo repo;

	public LokalizacjaService(LokalizacjaRepo repo) {
		this.repo = repo;
	}
	
	@Transactional
	public Lokalizacja addNew(Lokalizacja source) {
		var saved = repo.save(source);
		if(source.getDefaultLocation())
			setNewDefault(saved.getId());
		return source;
	}
	
	@Transactional
	public void setNewDefault(Long id) {
		repo.setOneDefault(id);
	}
	
	public Long getDefaultId() {
		var location = repo.findByDefaultLocationIsTrue().orElseThrow(() -> new NotFoundCustomException());
		return location.getId();
	}
	
	public List<Lokalizacja> getAll() {
		return repo.findAllByRemovedIsFalse().stream()
		.sorted(Comparator.comparing(Lokalizacja::getWhenCreated).reversed())
		.collect(Collectors.toList());
	}
	
	@Transactional
	public void remove(Long id) {
		repo.findById(id).ifPresentOrElse(l -> l.setRemoved(true), () -> {throw new IllegalArgumentException("Wron location data provided");});
	}
		
		
}
