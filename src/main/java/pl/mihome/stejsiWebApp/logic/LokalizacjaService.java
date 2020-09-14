package pl.mihome.stejsiWebApp.logic;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.Lokalizacja;
import pl.mihome.stejsiWebApp.model.LokalizacjaRepo;

@Service
public class LokalizacjaService {

	private final LokalizacjaRepo repo;
	
	private static final Logger log = LoggerFactory.getLogger(LokalizacjaService.class);

	public LokalizacjaService(LokalizacjaRepo repo) {
		this.repo = repo;
	}
	
	@Transactional
	public Lokalizacja addNew(Lokalizacja source) {
		log.info("Tworzenie nowej lokalziacji w bazie");
		var saved = repo.save(source);
		if(source.getDefaultLocation())
			setNewDefault(saved.getId());
		return source;
	}
	
	@Transactional
	public void setNewDefault(Long id) {
		log.info("Ustawianie lokalziacji jako domyślna id: " + id);
		repo.setOneDefault(id);

	}
	
	public Long getDefaultId() {
		log.info("Sprawdzanie id domyślnej lokalizacji");
		var location = repo.findByDefaultLocationIsTrue();
		return location.map(Lokalizacja::getId).orElse(null);

	}
	
	public List<Lokalizacja> getAll() {
		log.info("Pobieranie wszystkich lokalizacji lokalizacji");
		return repo.findAllByRemovedIsFalse().stream()
		.sorted(Comparator.comparing(Lokalizacja::getWhenCreated).reversed())
		.collect(Collectors.toList());
	}

	public Slice<Lokalizacja> getSliceOfActive(int pageNo) {
		int DEFAULT_PAGE_SIZE = 10;
		var pageReq = PageRequest.of(pageNo, DEFAULT_PAGE_SIZE, Sort.by("whenCreated").descending());
		return repo.findByRemovedIsFalse(pageReq);
	}
	
	@Transactional
	public void remove(Long id) {
		log.info("Usuwanie wybranej lokalizacji id: " + id);
		repo.findByIdAndRemovedIsFalse(id).ifPresentOrElse(l -> l.setRemoved(true), () -> {throw new NotFoundCustomException("LOCATION_NOT_FOUND", "Removal request refused - no location found with id: " + id);});
	}

}
