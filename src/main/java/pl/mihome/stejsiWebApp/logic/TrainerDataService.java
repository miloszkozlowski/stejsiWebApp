package pl.mihome.stejsiWebApp.logic;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.mihome.stejsiWebApp.model.TrainerData;
import pl.mihome.stejsiWebApp.model.TrainerDataRepo;

@Service
public class TrainerDataService {
	
	private TrainerDataRepo repo;
	
	private static final Logger log = LoggerFactory.getLogger(TrainerDataService.class);
	

	public TrainerDataService(TrainerDataRepo repo) throws IllegalStateException {
		this.repo = repo;
	}

	
	public TrainerData getCurrentData() {
		log.info("Wczytywanie danych trenera");
		var currentData = repo.findByRemovedIsFalse();
		if(currentData.isEmpty()) {
			log.warn("Nie odnaleziono ustawień trenera - zwracanie pustych");
			return new TrainerData();
		}
		else if(currentData.size() > 1) {
			log.error("Nie można zwrócić danych, bo aktualnych rekordów danych jest więcej niż 1");
			throw new IllegalStateException();
		}
		return currentData.get(0);
	}


	@Transactional
	public TrainerData newSettings(TrainerData trainerData) {
		log.info("Zapisywanie nowych danych trenera");
		
		var oldSettings = repo.findByRemovedIsFalse();
		
		oldSettings.stream()
		.filter(s -> !s.isRemoved())
		.forEach(s -> s.setRemoved(true));
		
		return repo.save(trainerData);
	}

}
