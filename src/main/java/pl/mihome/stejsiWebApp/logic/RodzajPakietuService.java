package pl.mihome.stejsiWebApp.logic;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import pl.mihome.stejsiWebApp.DTO.RodzajPakietuWriteModel;
import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.RodzajPakietu;
import pl.mihome.stejsiWebApp.model.RodzajPakietuRepo;

@Service
public class RodzajPakietuService {

	private final RodzajPakietuRepo repo;
	
	private static final Logger log = LoggerFactory.getLogger(RodzajPakietuService.class);

	public RodzajPakietuService(RodzajPakietuRepo repo) {
		this.repo = repo;
	}
	
	public RodzajPakietu getTypeById(Long id) {
		log.info("Pobieranie rodzaju pakietu po id: " + id);
		return repo.findByIdAndRemovedIsFalse(id).orElseThrow(() -> new NotFoundCustomException("PACKAGE_TYPE_NOT_FOUND", "Requested package type could not be found by id: " + id));
	}
	
	@Transactional
	public String removeById(Long id) {
		log.info("Usuwanie rodzaju pakietu po id: " + id);
		var type = getTypeById(id);
			type.setRemoved(true);
			return type.getTitle();
	}
	
	public RodzajPakietu saveNewType(RodzajPakietuWriteModel source) {
		log.info("Zapisywanie nowego rodzaju pakietu: " + source.getTitle());
		var result = source.toRodzajPakietu();
		repo.save(result);
		return result;
	}
	
	public List<RodzajPakietu> getAllActive() {
		log.info("Pobieranie wszystkich rodzajów pakietów");
		return repo.findAll().stream()
				.filter(p -> !p.isRemoved())
				.collect(Collectors.toList());
	}

	public Slice<RodzajPakietu> getSliceOfActive(int pageNo) {
		int DEFAULT_PAGE_SIZE = 10;
		var pageReq = PageRequest.of(pageNo, DEFAULT_PAGE_SIZE, Sort.by("whenCreated").descending());
		return repo.findByRemovedIsFalse(pageReq);
	}

}


