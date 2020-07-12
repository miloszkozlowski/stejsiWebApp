package pl.mihome.stejsiWebApp.logic;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import pl.mihome.stejsiWebApp.DTO.TreningReadModel;
import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.TokenRepo;
import pl.mihome.stejsiWebApp.model.TreningRepo;

@Service
public class ScheduledNotificationService {
	
	private final int CYCLE_NEW_TRAININGS_PLANNED_MINS = 15;
	private final int CYCLE_PRESENCE_CONFIRMATION_MINS = 30;
	private final int CYCLE_PRESENCE_CONFIRMATION_REMINDER_MINS = 360;
	
	private final AndroidNotificationsService notificationsService;
	private final TreningRepo trainingRepo;
	private final TokenRepo tokenRepo;
	
	private static final Logger log = LoggerFactory.getLogger(ScheduledNotificationService.class);
	
	public ScheduledNotificationService(AndroidNotificationsService notificationsService, TreningRepo trainingRepo,
			TokenRepo tokenRepo) {
		this.notificationsService = notificationsService;
		this.trainingRepo = trainingRepo;
		this.tokenRepo = tokenRepo;
	}




	@Scheduled(fixedDelay = CYCLE_NEW_TRAININGS_PLANNED_MINS*60000, initialDelay = 60000) //co 15 minut
//	@Scheduled(fixedDelay = 60000, initialDelay = 15000) //test
	@Transactional
	public void sendNoificationOnNewPlannedTrainings() {
		log.info("Rozpoczynanie wysyłki powiadomień o nowych treningach z częstotliwością raz na " + CYCLE_NEW_TRAININGS_PLANNED_MINS + " minut(y)");
		var trainings = trainingRepo.findByUserNotifiedIsNullAndScheduledForIsNotNullAndScheduleConfirmedIsNullAndScheduledForIsGreaterThan(LocalDateTime.now().plusHours(1)); //plus
//		var trainings = trainingRepo.findByUserNotifiedIsNullAndScheduledForIsNotNullAndScheduleConfirmedIsNullAndScheduledForIsGreaterThan(LocalDateTime.now()); //test
		if(!trainings.isEmpty()) {
			Map<Podopieczny, String> newTrainigsFound =  new HashMap<Podopieczny, String>();
			
			trainings.stream()
			.map(TreningReadModel::new)
			.forEach(t -> {
				if(newTrainigsFound.containsKey(t.getTrainingPackage().getOwner().getRootSource())) {
					newTrainigsFound.replace(t.getTrainingPackage().getOwner().getRootSource(), "");
				}
				else {
					newTrainigsFound.put(t.getTrainingPackage().getOwner().getRootSource(), t.getReadableDateScheduled() + " " + t.getReadableTimeScheduled());
				}
			});
			
			newTrainigsFound.keySet().stream()
			.filter(k -> !tokenRepo.findByOwnerAndActiveIsTrue(k).isEmpty())
			.forEach(k -> notificationsService.
						notifyUserOnNewTrainingPlanned(newTrainigsFound.get(k), 
								tokenRepo.findByOwnerAndActiveIsTrue(k).get(0).getTokenFCM()));
			
			trainings.stream()
			.forEach(t -> t.setUserNotified(LocalDateTime.now()));

		}
	}
	
	@Scheduled(fixedDelay = CYCLE_PRESENCE_CONFIRMATION_MINS*60000, initialDelay = 120000) //co 30 minut
//	@Scheduled(fixedDelay = 60000, initialDelay = 60000) //test
	@Transactional
	public void sendNoificationOnPresenceConfirmation() {
		log.info("Rozpoczynanie wysyłki powiadomień z prośbą o potweirdzenie obecności na treningu z częstotliwością raz na " + CYCLE_PRESENCE_CONFIRMATION_MINS + " minut(y)");
		var trainings = trainingRepo.findByScheduledForIsNotNullAndPresenceConfirmedByUserIsNullAndScheduledForIsLessThan(LocalDateTime.now().minusHours(1)); //minus
//		var trainings = trainingRepo.findByScheduledForIsNotNullAndPresenceConfirmedByUserIsNullAndScheduledForIsLessThan(LocalDateTime.now()); //test
		if(!trainings.isEmpty()) {
			Set<Podopieczny> presencesToConfirm = new HashSet<>();
			
			var trainingsToStream = trainings.stream()
			.filter(t -> t.getUserNotified() == null)
			.collect(Collectors.toList());
			
			trainingsToStream.addAll(trainings.stream()
			.filter(t -> t.getScheduledFor().isAfter(t.getUserNotified()))
			.collect(Collectors.toList()));
			
			trainingsToStream.stream()
			.map(TreningReadModel::new)
			.forEach(t -> {
				if(!presencesToConfirm.contains(t.getTrainingPackage().getOwner().getRootSource())) {
					presencesToConfirm.add(t.getTrainingPackage().getOwner().getRootSource());
				}
				
			});
			
			presencesToConfirm.stream()
			.filter(u -> !tokenRepo.findByOwnerAndActiveIsTrue(u).isEmpty())
			.forEach(k -> notificationsService.
						notifyUserOnPresenceToBeConfirmed(tokenRepo.findByOwnerAndActiveIsTrue(k).get(0).getTokenFCM()));
			
			trainingsToStream.stream()
			.forEach(t -> t.setUserNotified(LocalDateTime.now()));

		}
	}
	
	@Scheduled(fixedDelay = CYCLE_PRESENCE_CONFIRMATION_REMINDER_MINS*60000, initialDelay = 180000) //co 6 godzin
//	@Scheduled(fixedDelay = 60000, initialDelay = 18000) //test
	@Transactional
	public void sendNoificationOnLatePresenceConfirmation() {
		log.info("Rozpoczynanie wysyłki powiadomień ponaglających o konieczności potwierdzenia obecności z częstotliwością raz na " + CYCLE_PRESENCE_CONFIRMATION_REMINDER_MINS + " minut(y)");
		var trainings = trainingRepo.findByScheduledForIsNotNullAndPresenceConfirmedByUserIsNullAndUserNotifiedIsNotNullAndUserNotifiedIsLessThan(LocalDateTime.now().minusDays(3));
		if(!trainings.isEmpty()) {	
			Set<Podopieczny> presencesToConfirm = new HashSet<>();
			
			trainings.stream()
			.map(TreningReadModel::new)
			.forEach(t -> {
				if(!presencesToConfirm.contains(t.getTrainingPackage().getOwner().getRootSource())) {
					presencesToConfirm.add(t.getTrainingPackage().getOwner().getRootSource());
				}
				
			});
			
			presencesToConfirm.stream()
			.filter(u -> !tokenRepo.findByOwnerAndActiveIsTrue(u).isEmpty())
			.forEach(k -> notificationsService.
						notifyUserOnLatePresenceToBeConfirmed(tokenRepo.findByOwnerAndActiveIsTrue(k).get(0).getTokenFCM()));
			
			trainings.stream()
			.forEach(t -> t.setUserNotified(LocalDateTime.now()));

		}
	}
	
	

}
