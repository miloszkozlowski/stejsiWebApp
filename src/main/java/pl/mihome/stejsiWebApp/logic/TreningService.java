package pl.mihome.stejsiWebApp.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.mihome.stejsiWebApp.DTO.CalendarItem;
import pl.mihome.stejsiWebApp.DTO.TreningReadModel;
import pl.mihome.stejsiWebApp.config.AndroidAppConfiguration;
import pl.mihome.stejsiWebApp.exeptions.AndroidSessionNotAuthorizedException;
import pl.mihome.stejsiWebApp.exeptions.CalendarTimeConflictException;
import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TreningService {

    private static final Logger log = LoggerFactory.getLogger(TreningService.class);
    private final TreningRepo repo;
    private final PakietTreningowRepo packageRepo;
    private final LokalizacjaRepo locationRepo;
    private final TokenRepo tokenRepo;
    private final TrainerDataService trainerDataService;
    private final AndroidAppConfiguration androidAppConfiguration;
    private final EmailSenderService emailSenderService;


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


    /*
     * use getMapTrainingsWeekly
     */
    @Deprecated
    public List<List<TreningReadModel>> getTrainingsWeekly(LocalDateTime weekStart) {
        log.info("Pobieranie listy treningów w cyklu tygodniowym dla tygodnia rozpoczynającego się od: " + weekStart);
        var allTrainings = repo.findByScheduledForGreaterThanEqualAndScheduledForLessThan(weekStart, weekStart.plusDays(7));
        List<List<TreningReadModel>> toReturn = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            long a = i;
            List<TreningReadModel> trainingsDaily = allTrainings.stream()
                    .filter(t -> LocalDate.from(t.getScheduledFor()).isEqual(LocalDate.from(weekStart.plusDays(a))))
                    .map(TreningReadModel::new)
                    .sorted(Comparator.comparing(TreningReadModel::getScheduledFor))
                    .collect(Collectors.toList());
            toReturn.add(trainingsDaily);
        }
        return toReturn;
    }

    public Map<LocalDateTime, List<CalendarItem>> getMapTrainingsWeekly(int weekNo) {
        log.info("Pobieranie mapy treningów w cyklu tygodniowym dla indeksu tygodnia: " + weekNo);
        LocalDateTime weekStart = weekStart(weekNo).atStartOfDay();
        var allTrainings = repo.findByScheduledForGreaterThanEqualAndScheduledForLessThan(weekStart, weekStart.plusDays(7));

        Map<LocalDateTime, List<CalendarItem>> toReturn = new HashMap<>();

        for (int i = 0; i < 7; i++) {
            long a = i;
            List<CalendarItem> calendarItemListDaily = allTrainings.stream()
                    .filter(t -> LocalDate.from(t.getScheduledFor()).isEqual(LocalDate.from(weekStart.plusDays(a))))
                    .map(CalendarItem::new)
                    .sorted(Comparator.comparing(CalendarItem::getEventStartsAt))
                    .collect(Collectors.toList());
            toReturn.put(LocalDate.from(weekStart.plusDays(a)).atStartOfDay(), calendarItemListDaily);
        }
        return toReturn;
    }


    @Transactional
    public void removeSchedule(Long tid) {
        log.info("Usuwanie treningu z grafiku id: " + tid);
        var trening = repo.findById(tid).orElseThrow(NotFoundCustomException::new);
        trening.setScheduledFor(null);
        trening.setMarkedAsDone(null);
        trening.setPresenceConfirmedByUser(null);
        trening.setScheduleConfirmed(null);
        trening.setWhenCanceled(null);
    }


    public Trening getOneToChange(Long id) {
        log.info("Pobieranie danych treningu do edycji id: " + id);
        var trening = repo.findById(id).orElseThrow(NotFoundCustomException::new);
        var trainingDTO = new TreningReadModel(trening);
        if (trainingDTO.isDone()) {
            log.warn("Nie można pobrać treningu do edycji, bo jest już oznaczony jako wykonany");
            throw new IllegalStateException("Training can't be changed as it is already done");
        }
        return trening;
    }

    @Transactional
    public TreningReadModel confirmPresence(Long tid) {
        log.info("Oznaczanie treningu jako wykonany id: " + tid);
        var trening = repo.findById(tid).orElseThrow(NotFoundCustomException::new);
        var trainingDTO = new TreningReadModel(trening);
        if (!trainingDTO.isDone()) {
            trening.setMarkedAsDone(LocalDateTime.now());
        }
        return new TreningReadModel(trening);
    }

    @Transactional
    public void confirmPresenceByUser(Long tid, String token) throws AndroidSessionNotAuthorizedException, NotFoundCustomException {
        log.info("Podpisywanie listy obecności z żądania tokenu na treningu id: " + tid);
        Token tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token)
                .orElseThrow(AndroidSessionNotAuthorizedException::new);

        var trening = repo.findById(tid)
                .orElseThrow(NotFoundCustomException::new);

        var trainingDTO = new TreningReadModel(trening);

        if (trening.getTrainingPackage().getOwner().equals(tokenFound.getOwner())) {
            if (!trainingDTO.isPresenceConfirmed()) {
                trening.setPresenceConfirmedByUser(LocalDateTime.now());
                if (!trainingDTO.isConfirmed()) {
                    confirmSchedule(trening);
                }
            }
        } else
            throw new AndroidSessionNotAuthorizedException();

    }

    private void confirmSchedule(Trening training) {
        training.setScheduleConfirmed(LocalDateTime.now());
    }

    @Transactional
    public void confirmScheduleByUser(Long tid, String token) throws AndroidSessionNotAuthorizedException, NotFoundCustomException {
        log.info("Potwierdzanie grafiku na żądanie tokenu dla treningu id: " + tid);
        Token tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token)
                .orElseThrow(AndroidSessionNotAuthorizedException::new);

        var trening = repo.findById(tid)
                .orElseThrow(NotFoundCustomException::new);

        var trainingDTO = new TreningReadModel(trening);

        if (trening.getTrainingPackage().getOwner().equals(tokenFound.getOwner())) {
            if (!trainingDTO.isConfirmed())
                confirmSchedule(trening);
        } else
            throw new AndroidSessionNotAuthorizedException();

    }


    Set<Trening> fullFillPackage(Long packageId) {
        log.info("Tworzenie pustych treninngów po utworzeniu pakietu id: " + packageId);
        Optional<PakietTreningow> pakiet = packageRepo.findById(packageId);
        if (pakiet.isPresent()) {
            int amountOfTrainings = pakiet
                    .map(PakietTreningow::getPackageType)
                    .map(RodzajPakietu::getAmountOfTrainings)
//                    .get();
                    .orElseGet(() -> {
                        throw new NotFoundCustomException();
                    });

            Set<Trening> trainingsList = new HashSet<>();

            for (int i = 0; i < amountOfTrainings; i++) {
                trainingsList.add(repo.save(new Trening(pakiet.get())));
            }

            return trainingsList;
        }
        log.error("Nie można było utworzyć listy pustych treningów, bo nie istnieje pakiet id: " + packageId);
        throw new IllegalArgumentException("Wrong training package data provided");
    }


    public Trening schedule(Long id, String trainingDate, String trainingTime, Optional<Long> locationId) throws CalendarTimeConflictException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("H:mm");
        LocalTime time = LocalTime.parse(trainingTime, dtf);
        LocalDate date = LocalDate.parse(trainingDate);
        var trainingDateTime = LocalDateTime.of(date, time);
        return scheduleWithLocalDateTime(id, trainingDateTime, locationId);
    }

    @Transactional
    public Trening scheduleWithLocalDateTime(Long id, LocalDateTime trainingDateTime, Optional<Long> locationId) {

        log.info("Zapisywanie terminu treningu id: " + id);
        Optional<Trening> trening = repo.findById(id);

        Lokalizacja location = locationId
                .map(l -> locationRepo.findById(l)
                        .orElseGet(() -> {
                            throw new NotFoundCustomException();
                        }))
                .orElse(null);


        if (trening.isPresent()) {
            if (trening.get().getScheduledFor() == null) {
                setScheduleForTraining(trainingDateTime, trening, location);
            } else if (trening.get().getLocation() == null) {
                if (!trening.get().getScheduledFor().equals(trainingDateTime) || location != null) {
                    setScheduleForTraining(trainingDateTime, trening, location);
                }
            } else {
                if (!trening.get().getScheduledFor().equals(trainingDateTime) || !trening.get().getLocation().equals(location)) {
                    setScheduleForTraining(trainingDateTime, trening, location);
                }
            }

        } else {
            log.error("Nie można zapisać terminu treningu, bo nie istnieje trening id: " + id);
            throw new NotFoundCustomException("Incorrect training ID provided");
        }

        return trening.get();
    }

//    private void proceedWithScheduling(LocalDateTime trainingDateTime, Optional<Trening> trening, Lokalizacja location) {
//        setScheduleForTraining(trainingDateTime, trening, location);
//    }

    private void setScheduleForTraining(LocalDateTime trainingDateTime, Optional<Trening> trening, Lokalizacja location) {
        if (checkTimeAvailability(trening.orElseGet(() -> {throw new NotFoundCustomException();}).getId(), trainingDateTime, trening.get().getTrainingPackage().getPackageType().getLengthMinutes())) {
            trening.get().setScheduledFor(trainingDateTime);
            trening.get().setScheduleConfirmed(null);
            trening.get().setPresenceConfirmedByUser(null);
            trening.get().setWhenCanceled(null);
            trening.get().setUserNotified(null);
            trening.get().setLocation(location);
        } else {
            throw new CalendarTimeConflictException(trainingDateTime.toLocalDate().toString() + " godz. " + trainingDateTime.toLocalTime().toString(), trainingDateTime);
        }
    }

    private Boolean checkTimeAvailability(Long trainingId, LocalDateTime dateTimeToCheck, Integer trainingLength) {
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

        if (training.isPresent() && foundToken.isPresent()) {
            if (foundToken.get().getOwner().equals(training.get().getTrainingPackage().getOwner())) {
                training.get().setWhenCanceled(LocalDateTime.now());
                var trainerData = trainerDataService.getCurrentData();
                if (trainerData.getEmail() != null && !trainerData.getEmail().isBlank()) {
                    sendCancellationEmailToTrainer(foundToken.get().getOwner(), training.get(), trainerData);
                }
            } else {
                throw new AndroidSessionNotAuthorizedException();
            }

        } else throw new IllegalArgumentException("Training/user missmatch");

    }


    private void sendCancellationEmailToTrainer(Podopieczny owner, Trening training, TrainerData trainerData) {
        var tr = new TreningReadModel(training);
        String subject = owner.getImie() + " " + owner.getNazwisko() + " - trening odwołany!";
        String text = "Trenerze! \n"
                + owner.getImie() + " odwołuje trening zaplanowany na:\n\n"
                + tr.getReadableDateScheduled() + ", godzina " + tr.getReadableTimeScheduled() + "/n/n"
                + "Możesz zaplanować ten trening na inny termin kub przywrócić go do grafiku w pierwotnym terminie: " + androidAppConfiguration.getWebServerUrl() + "/plan/" + tr.getTrainingPackage().getId()
                + "\n\nPozdrawiam\nAutomat";
        emailSenderService.sendSimpleMessage(trainerData.getEmail(), subject, text);
        log.info("E-mail z informacją o odwołaniu treningu wysłany do trenera");


    }

    @Transactional
    public TreningReadModel unCancel(Long tid) {
        log.info("Przywracanie terminu treningu przez trenera w odpowiedzi na odwołanine przez użytkownika, dla treningu id: " + tid);
        var training = repo.findById(tid);
        training.orElseThrow(() -> new NotFoundCustomException("Wrong training id")).setWhenCanceled(null);
        training.get().setScheduleConfirmed(null);
        return new TreningReadModel(training.get());
    }

    private LocalDate weekStart(Integer week) {
        LocalDate today = LocalDate.now();
        return today.with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 1L).plusWeeks(week);
    }

    public List<CalendarItem> getAllCanceled() {
        return repo.findByWhenCanceledIsNotNull().stream()
        .map(CalendarItem::new)
        .collect(Collectors.toList());
    }
}
