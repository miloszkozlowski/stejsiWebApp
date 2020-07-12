package pl.mihome.stejsiWebApp.logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.SendFailedException;
import javax.transaction.Transactional;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import pl.mihome.stejsiWebApp.DTO.TreningReadModel;
import pl.mihome.stejsiWebApp.config.AndroidAppConfiguration;
import pl.mihome.stejsiWebApp.exeptions.AndroidSessionNotAuthorizedException;
import pl.mihome.stejsiWebApp.exeptions.CallendarTimeConflictException;
import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.Lokalizacja;
import pl.mihome.stejsiWebApp.model.LokalizacjaRepo;
import pl.mihome.stejsiWebApp.model.PakietTreningow;
import pl.mihome.stejsiWebApp.model.PakietTreningowRepo;
import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.RodzajPakietu;
import pl.mihome.stejsiWebApp.model.Token;
import pl.mihome.stejsiWebApp.model.TokenRepo;
import pl.mihome.stejsiWebApp.model.TrainerData;
import pl.mihome.stejsiWebApp.model.Trening;
import pl.mihome.stejsiWebApp.model.TreningRepo;

@Service
public class TreningService {

		private TreningRepo repo;
		private PakietTreningowRepo packageRepo;
		private LokalizacjaRepo locationRepo;
		private TokenRepo tokenRepo;
		private TrainerDataService trainerDataService;
		private AndroidAppConfiguration androidAppConfiguration;
		private EmailSenderService emailSenderService;
		
		private static final Logger log = LoggerFactory.getLogger(TreningService.class);


		public TreningService(TreningRepo repo, PakietTreningowRepo packageRepo, LokalizacjaRepo locationRepo,
				TokenRepo tokenRepo, TrainerDataService trainerDataService,
				AndroidAppConfiguration androidAppConfiguration, EmailSenderService emailSenderService) {
			this.repo = repo;
			this.packageRepo = packageRepo;
			this.locationRepo = locationRepo;
			this.tokenRepo = tokenRepo;
			this.trainerDataService = trainerDataService;
			this.androidAppConfiguration = androidAppConfiguration;
			this.emailSenderService = emailSenderService;
		}
		

		public List<List<TreningReadModel>> getTrainingsWeekly(LocalDateTime weekStart) {
			log.info("Pobieranie listy treningów w cyklu tygodniowym dla tygodnia rozpoczynającego się od: " + weekStart);
			var allTrainings = repo.findByScheduledForGreaterThanEqualAndScheduledForLessThan(weekStart, weekStart.plusDays(7));
			List<List<TreningReadModel>> toReturn = new ArrayList<>();
			
			for(int i = 0; i < 7; i++)
			{
				Long a = Long.valueOf(i);
				List<TreningReadModel> trainingsDaily = allTrainings.stream()
				.filter(t -> LocalDate.from(t.getScheduledFor()).isEqual(LocalDate.from(weekStart.plusDays(a))))
				.map(TreningReadModel::new)
				.sorted(Comparator.comparing(TreningReadModel::getScheduledFor))
				.collect(Collectors.toList());
				toReturn.add(trainingsDaily);
			}
			return toReturn;
		}
		
		@Transactional
		public void removeSchedule(Long pid, Long tid) throws NotFoundCustomException {
			log.info("Usuwanie treningu z grafiku id: " + tid);
			var trening = repo.findById(tid).orElseThrow(() -> new NotFoundCustomException());
			var trainingDTO = new TreningReadModel(trening);
			if(!trainingDTO.getTrainingPackage().getId().equals(pid)) {
				log.error("Nie można usunąć treningu z grafiku, bo trening id " + tid + " nie znajduje się w pakiecie id " + pid);
				throw new NotFoundCustomException();
			}
			trening.setScheduledFor(null);
			trening.setMarkedAsDone(null);
			trening.setPresenceConfirmedByUser(null);
			trening.setScheduleConfirmed(null);
			trening.setWhenCanceled(null);
		}
		
		
		public Trening getOneToChange(Long id) throws IllegalStateException, NotFoundCustomException {
			log.info("Pobieranie danych treningu do edycji id: " + id);
			var trening = repo.findById(id).orElseThrow(() -> new NotFoundCustomException());
			var trainingDTO = new TreningReadModel(trening);
			if(trainingDTO.isDone()) {
				log.warn("Nie można pobrać treningu do edycji, bo jest już oznaczony jako wykonany");
				throw new IllegalStateException("Training can't be changed as it is already done");
			}
			return trening;
		}
		
		@Transactional
		public void confirmPresence(Long pid, Long tid)  throws NotFoundCustomException {
			log.info("Oznaczanie treningu jako wykonany id: " + tid);
			var trening = repo.findById(tid).orElseThrow(() -> new NotFoundCustomException());
			var trainingDTO = new TreningReadModel(trening);
			if(!trainingDTO.getTrainingPackage().getId().equals(pid)) {
				log.error("Nie można oznaczyć treningu jako wykonany, bo trening id " + tid + " nie znajduje się w pakiecie id " + pid);
				throw new NotFoundCustomException();
			}
			if(!trainingDTO.isDone()) {
					trening.setMarkedAsDone(LocalDateTime.now());
					var undoneTrainingsInPackage = trening.getTrainingPackage().getTrainings().stream()
					.filter(t -> t.getMarkedAsDone() == null)
					.count();
					if(undoneTrainingsInPackage == 0) {
						trening.getTrainingPackage().setClosed(true);
					}
			}
		}
		
		@Transactional
		public void confirmPresenceByUser(Long tid, String token) throws AndroidSessionNotAuthorizedException, NotFoundCustomException {
			log.info("Podpisywanie listy obecności z żądania tokenu na treningu id: " + tid);
			Token tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token)
					.orElseThrow(() -> new AndroidSessionNotAuthorizedException());
			
			var trening = repo.findById(tid)
					.orElseThrow(() -> new NotFoundCustomException());
			
			var trainingDTO = new TreningReadModel(trening);
			
			if(trening.getTrainingPackage().getOwner().equals(tokenFound.getOwner())) {
				if(!trainingDTO.isPresenceConfirmed()) {
					trening.setPresenceConfirmedByUser(LocalDateTime.now());
					if(!trainingDTO.isConfirmed()) {
						confirmSchedule(trening);
					}
				}
			}
			else
				throw new AndroidSessionNotAuthorizedException();

		}
		
		private void confirmSchedule(Trening training) {
			training.setScheduleConfirmed(LocalDateTime.now());
		}
		
		@Transactional
		public void confirmScheduleByUser(Long tid, String token) throws AndroidSessionNotAuthorizedException, NotFoundCustomException {
			log.info("Potwierdzanie grafiku na żądanie tokenu dla treningu id: " + tid);
			Token tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token)
					.orElseThrow(() -> new AndroidSessionNotAuthorizedException());
			
			var trening = repo.findById(tid)
					.orElseThrow(() -> new NotFoundCustomException());
			
			var trainingDTO = new TreningReadModel(trening);
			
			if(trening.getTrainingPackage().getOwner().equals(tokenFound.getOwner())) {
				if(!trainingDTO.isConfirmed()) 
					confirmSchedule(trening);
			}
			else
				throw new AndroidSessionNotAuthorizedException();

		}
		
		
		Set<Trening> fullFillPackage(Long packageId) {
			log.info("Tworzenie pustych treninngów po utworzeniu pakietu id: " + packageId);
			Optional<PakietTreningow> pakiet = packageRepo.findById(packageId);
			if(pakiet.isPresent())
			{
				int amountOfTrainings = pakiet
						.map(PakietTreningow::getPackageType)
						.map(RodzajPakietu::getAmountOfTrainings)
						.get();
				
				Set<Trening> trainingsList = new HashSet<>();
				
				for(int i=0; i < amountOfTrainings; i++) {
					trainingsList.add(repo.save(new Trening(pakiet.get())));
				}
				
				return trainingsList;
			}
			log.error("Nie można było utworzyć listy pustych treningów, bo nie istnieje pakiet id: " + packageId);	
			throw new IllegalArgumentException("Wrong training package data provided");
		}
		
		@Transactional
		public Trening schedule(Long id, String trainingDate, String trainingTime, Optional<Long> locationId) throws CallendarTimeConflictException {
			log.info("Zapisywanie terminu treningu id: " + id);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("H:mm");
			LocalTime time = LocalTime.parse(trainingTime, dtf);
			LocalDate date = LocalDate.parse(trainingDate);
			var trainingDateTime = LocalDateTime.of(date, time);
			Optional<Trening> trening = repo.findById(id);
			
			Lokalizacja location = locationId.map(l -> locationRepo.findById(l).get()).orElse(null);
			
			
			if(trening.isPresent()){
				if(trening.get().getScheduledFor() == null) {
					if(checkTimeAvability(trening.get().getId(), trainingDateTime, trening.get().getTrainingPackage().getPackageType().getLengthMinutes())) {
						trening.get().setScheduledFor(trainingDateTime);
						trening.get().setScheduleConfirmed(null);
						trening.get().setPresenceConfirmedByUser(null);
						trening.get().setWhenCanceled(null);
						trening.get().setUserNotified(null);
						trening.get().setLocation(location);
					
					}
					else {
						throw new CallendarTimeConflictException(trainingDate + " godz. " + trainingTime, trainingDateTime);
					}
				}
				else if(trening.get().getLocation() == null ) {
					if(!trening.get().getScheduledFor().equals(trainingDateTime) || location != null) {
						if(checkTimeAvability(trening.get().getId(), trainingDateTime, trening.get().getTrainingPackage().getPackageType().getLengthMinutes())) {
							trening.get().setScheduledFor(trainingDateTime);
							trening.get().setScheduleConfirmed(null);
							trening.get().setPresenceConfirmedByUser(null);
							trening.get().setWhenCanceled(null);
							trening.get().setUserNotified(null);
							trening.get().setLocation(location);
						}
						else {
							throw new CallendarTimeConflictException(trainingDate + " godz. " + trainingTime, trainingDateTime);
						}
					}
				}
				else {
					if(!trening.get().getScheduledFor().equals(trainingDateTime) || !trening.get().getLocation().equals(location)) {
						if(checkTimeAvability(trening.get().getId(), trainingDateTime, trening.get().getTrainingPackage().getPackageType().getLengthMinutes())) {
							trening.get().setScheduledFor(trainingDateTime);
							trening.get().setScheduleConfirmed(null);
							trening.get().setPresenceConfirmedByUser(null);
							trening.get().setWhenCanceled(null);
							trening.get().setUserNotified(null);
							trening.get().setLocation(location);
						}
						else {
							throw new CallendarTimeConflictException(trainingDate + " godz. " + trainingTime, trainingDateTime);
						}
					}
				}
				
			}
			else {
				log.error("Nie można zapisać terminu treningu, bo nie istnieje trening id: " + id);
				throw new IllegalArgumentException("Wrong training data provided");
			}
			
			return trening.get();
		}
		
		private Boolean checkTimeAvability(Long trainingId, LocalDateTime dateTimeToCheck, Integer trainingLength) {
			final LocalDateTime toValidateStart = dateTimeToCheck;
			final LocalDateTime toValidatedEnd = dateTimeToCheck.plusMinutes(trainingLength).minusSeconds(1);
			
			List<Trening> rawListOfTrainings = repo.findByScheduledForGreaterThanEqualAndScheduledForLessThan(toValidateStart.minusDays(1), toValidatedEnd.plusDays(1));
			
			List<TreningReadModel> toCheckWith = rawListOfTrainings.stream()
					.map(TreningReadModel::new)
					.collect(Collectors.toList());
			
			return toCheckWith.stream()
					.noneMatch(t -> toValidateStart.isBefore(t.getTrainingEndInclusive()) && t.getScheduledFor().isBefore(toValidatedEnd) && t.getId() != trainingId);
			
		}

		@Transactional
		@Async
		public void cancelByUser(Long tid, String token) throws IllegalArgumentException {
			log.info("Zapisywanie informacji o odwołaniu przez użytkownika treningu id: " + tid);
			var training = repo.findById(tid);
			var foundToken = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
		
			if(training.isPresent() && foundToken.isPresent()) {
				if(foundToken.get().getOwner().equals(training.get().getTrainingPackage().getOwner())) {
					training.get().setWhenCanceled(LocalDateTime.now());
					var trainerData = trainerDataService.getCurrentData();
					if(trainerData.getEmail() != null && !trainerData.getEmail().isBlank()) {
						sendCancelationEmailToTrainer(foundToken.get().getOwner(), training.get(), trainerData);
					}
				}
				else {
					throw new AndroidSessionNotAuthorizedException();
				}
				
			}
			else throw new IllegalArgumentException("Training/user missmatch");
			
		}
		
		
		private void sendCancelationEmailToTrainer(Podopieczny owner, Trening training, TrainerData trainerData) {
			var tr = new TreningReadModel(training);
			String subject = owner.getImie() + " " + owner.getNazwisko() + " - trening odwołany!";
			String text = "Trenerze! \n"
					+ owner.getImie() + " odwołuje trening zaplanowany na:\n\n"
					+ tr.getReadableDateScheduled() + ", godzina " + tr.getReadableTimeScheduled() + "/n/n"
					+ "Możesz zaplanować ten trening na inny termin kub przywrócić go do grafiku w pierwotnym terminie: " + androidAppConfiguration.getWebServerUrl() + "/plan/" + tr.getTrainingPackage().getId()
					+ "\n\nPozdrawiam\nAutomat";
			try {
			emailSenderService.sendSimpleMessage(trainerData.getEmail(), subject, text);
			log.info("E-mail z informacją o odwołaniu treningu wysłany do trenera");
			}
			catch(SendFailedException ex) {
				log.error("Nie można było wysłać e-mail z infomracją o odwołaniu treningu do trenera, bo: " + ex.getMessage());
			}
			
		}

		@Transactional
		public void unCancel(Long tid) throws IllegalArgumentException {
			log.info("Przywracanie terminu treningu przez trenera w odpowiedzi na odwołanine przez użytkownika, dla treningu id: " + tid);
			var training = repo.findById(tid);
			training.orElseThrow(() -> new IllegalArgumentException("Wrong training id")).setWhenCanceled(null);
			training.get().setScheduleConfirmed(null);
		}
}
