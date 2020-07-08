package pl.mihome.stejsiWebApp.logic;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import pl.mihome.stejsiWebApp.DTO.RodzajPakietuWriteModel;
import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.RodzajPakietu;
import pl.mihome.stejsiWebApp.model.RodzajPakietuRepo;

@Service
public class RodzajPakietuService {

	private RodzajPakietuRepo repo;
	
	public RodzajPakietuService(RodzajPakietuRepo repo) {
		this.repo = repo;
	}
	
	public RodzajPakietu getTypeById(Long id) throws IllegalArgumentException {
		return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Wrong package type data provided"));
	}
	
	@Transactional
	public String removeById(Long id) throws NotFoundCustomException {
		var type = getTypeById(id);
		if(!type.isRemoved()) {
			type.setRemoved(true);
			return type.getTitle();
		}
		return "";
	}
	
	public RodzajPakietu saveNewType(RodzajPakietuWriteModel source) {
		var result = source.toRodzajPakietu();
		repo.save(result);
		return result;
	}
	
	public List<RodzajPakietu> getAllActive() {
		return repo.findAll().stream()
				.filter(p -> !p.isRemoved())
				.collect(Collectors.toList());
		
	}
}


