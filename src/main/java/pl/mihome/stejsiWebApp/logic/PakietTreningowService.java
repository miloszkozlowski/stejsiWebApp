package pl.mihome.stejsiWebApp.logic;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import pl.mihome.stejsiWebApp.DTO.PakietReadModel;
import pl.mihome.stejsiWebApp.DTO.PakietWriteModel;
import pl.mihome.stejsiWebApp.DTO.PodopiecznyReadModel;
import pl.mihome.stejsiWebApp.model.PakietTreningowRepo;
import pl.mihome.stejsiWebApp.model.PodopiecznyRepo;

@Service
public class PakietTreningowService {

	PakietTreningowRepo repo;
	PodopiecznyRepo userRepo;
	TreningService trainingService;
	RodzajPakietuService typeService;
	
	public PakietTreningowService(PakietTreningowRepo repo, PodopiecznyRepo userRepo, RodzajPakietuService typeService, TreningService trainingService) {
		this.repo = repo;
		this.userRepo = userRepo;
		this.typeService = typeService; 
		this.trainingService = trainingService;
	}
	
	public PakietReadModel getByIdEagerly(Long id) {
		var packageRaw = repo.findById(id);
		return packageRaw
				.map(p -> new PakietReadModel(p))
				.orElseThrow(() -> {throw new IllegalArgumentException("Wrong package date provided");});			
	}
	
	@Transactional
	public PakietReadModel createNew(PakietWriteModel source) {
		if(source.getPodopiecznyId() == null || source.getRodzajPakietuId() == null)
			throw new IllegalArgumentException("Incorrect data provided");	
		var owner = userRepo.findById(source.getPodopiecznyId()).get();
		var pakiet = repo.save(source.toPakietTreningow(owner, typeService.getTypeById(source.getRodzajPakietuId())));
		var treninigi = trainingService.fullFillPackage(pakiet.getId());
		pakiet.setTrainings(treninigi);
		return new PakietReadModel(pakiet, new PodopiecznyReadModel(owner));
	}
	
	@Transactional
	public void togglePayment(Long id) {
		repo.findById(id).ifPresentOrElse(p -> p.setPaid(!p.isPaid()), () -> { throw new IllegalArgumentException("Wrong package data provided"); });
	}
}
