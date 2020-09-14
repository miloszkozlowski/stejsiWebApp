package pl.mihome.stejsiWebApp.logic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.mihome.stejsiWebApp.DTO.PakietReadModel;
import pl.mihome.stejsiWebApp.DTO.PakietWriteModel;
import pl.mihome.stejsiWebApp.DTO.PodopiecznyReadModel;
import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.PakietTreningowRepo;
import pl.mihome.stejsiWebApp.model.PodopiecznyRepo;
import pl.mihome.stejsiWebApp.model.PushNotificationRequest;
import pl.mihome.stejsiWebApp.model.TokenRepo;

@Service
public class PakietTreningowService {

	private final PakietTreningowRepo repo;
	private final PodopiecznyRepo userRepo;
	private final TokenRepo tokenRepo;
	private final TreningService trainingService;
	private final RodzajPakietuService typeService;
	private final FirebaseCloudMessagingService fcmService;
	
	private static final Logger log = LoggerFactory.getLogger(PakietTreningowService.class);

	public PakietTreningowService(PakietTreningowRepo repo, PodopiecznyRepo userRepo, TokenRepo tokenRepo,
			TreningService trainingService, RodzajPakietuService typeService,
			FirebaseCloudMessagingService fcmService) {
		this.repo = repo;
		this.userRepo = userRepo;
		this.tokenRepo = tokenRepo;
		this.trainingService = trainingService;
		this.typeService = typeService;
		this.fcmService = fcmService;
	}

	public PakietReadModel getByIdEagerly(Long id) throws IllegalArgumentException {
		var packageRaw = repo.findById(id);
		log.info("Pobieranie pakietu do odczytu wraz z treningami id: " + id);
		return packageRaw
				.map(PakietReadModel::new)
				.orElseThrow(() -> {
					log.error("Nie można było pobrać pakietu w modele do odczytu wraz z treningami");
					throw new IllegalArgumentException("Wrong package date provided");
					});	
	}
	
	@Transactional
	public PakietReadModel createNew(PakietWriteModel source) {
		log.info("Tworzenie nowego pakietu treningów");
		if(source.getPodopiecznyId() == null || source.getRodzajPakietuId() == null) {
			log.warn("Nie dotarły dane rodzaju pakietu lub podopiecznego");
			throw new NotFoundCustomException("Incorrect data provided");
		}
		var owner = userRepo.findById(source.getPodopiecznyId()).orElseGet(() -> {throw new NotFoundCustomException();});
		var pakiet = repo.save(source.toPakietTreningow(owner, typeService.getTypeById(source.getRodzajPakietuId())));
		var treninigi = trainingService.fullFillPackage(pakiet.getId());
		pakiet.setTrainings(treninigi);
		var token = tokenRepo.findByOwnerAndActiveIsTrue(owner);
		token.stream().findFirst().ifPresent(t -> notifyUserOnNewPackage(pakiet.getPackageType().getTitle(), t.getTokenFCM()));
		
		return new PakietReadModel(pakiet, new PodopiecznyReadModel(owner));
	}
	
	@Transactional
	public void togglePayment(Long id) {
		log.info("Oznaczanie pakietu jako (nie)opłaconego id: " + id);
		repo.findById(id).ifPresentOrElse(p -> p.setPaid(!p.isPaid()), () -> { 
			log.warn("Nie można było odnaleźć danych pakietu");
			throw new NotFoundCustomException("Wrong package data provided");
			});
	}

	@Transactional
	public void closePackage(Long pid) {
		log.info("Zamykanie pakietu id: " + pid);
		repo.findById(pid).ifPresentOrElse(p -> {
	//		var dto = new PakietReadModel(p);
	//		if(!dto.isValid()) {
				p.setClosed(true);
	//		}
		}, () -> { 
			log.error("Nie można było odnaleźć danych pakietu");
			throw new IllegalArgumentException("Wrong package data provided");
			});
		
	}
	
	@Transactional
	public void markAllPastDone(Long pid) throws IllegalArgumentException {
		log.info("Oznaczanie wszystkich przeszłych treningów jako wykonane w pakiecie id: " + pid);
		repo.findById(pid).ifPresentOrElse(p -> p.getTrainings().stream()
		.filter(t -> t.getScheduledFor().isBefore(LocalDateTime.now()) && t.getMarkedAsDone() == null && t.getWhenCanceled() == null)
		.forEach(t -> t.setMarkedAsDone(LocalDateTime.now())), () -> {
			log.error("Nie można było odnaleźć danych pakietu");
			throw new IllegalArgumentException("Wrong package data provided");
			});
		
	}
	
	private void notifyUserOnNewPackage(String packageName, String tokenFCM) {
		PushNotificationRequest push;
		push = new PushNotificationRequest("Masz nowy pakiet treningów", "Stejsi otwarła dla Ciebie świeżutki pakiet: " + packageName + ". Do boju!");
		push.setUserToken(tokenFCM);
		try {
			fcmService.sendMessageWithoutDataToToken(push);
		}
		catch(Exception ex) {
			log.warn("Nieudana wysyłka powiadomienia o nowym zaplanowanym treningu: " + ex.getMessage());
		}
	}

	public List<PakietReadModel> getUnclosedPackages() {
		return repo.findByClosedIsFalse().stream()
				.map(PakietReadModel::new)
				.collect(Collectors.toList());
	}
}
