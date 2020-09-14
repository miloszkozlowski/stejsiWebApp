package pl.mihome.stejsiWebApp.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mihome.stejsiWebApp.DTO.PodopiecznyReadModel;
import pl.mihome.stejsiWebApp.DTO.PodopiecznyWriteModel;
import pl.mihome.stejsiWebApp.DTO.appComms.EmailRegistrationDTO;
import pl.mihome.stejsiWebApp.DTO.appComms.RegistrationStatus;
import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PodopiecznyService {

    private static final Logger log = LoggerFactory.getLogger(PodopiecznyService.class);
    private final PodopiecznyRepo repo;
    private final PodopiecznyCustomRepoImpl customRepo;
    private final TokenRepo tokenRepo;
    private final AppClientService appClientService;

    public PodopiecznyService(PodopiecznyRepo repo, PodopiecznyCustomRepoImpl customRepo, TokenRepo tokenRepo, AppClientService appClientService) {
        this.repo = repo;
        this.customRepo = customRepo;
        this.tokenRepo = tokenRepo;
        this.appClientService = appClientService;
    }

    public PodopiecznyReadModel getUserById(Long id) {
        log.info("Pobieranie danych użytkownika id: " + id);
        return repo.findById(id).map(PodopiecznyReadModel::new).orElseThrow(() -> {
            throw new NotFoundCustomException("PCZNY_NOT_FOUND", "Could not find user with delivered id: " + id);
        });
    }

    public PodopiecznyReadModel saveNewUser(PodopiecznyWriteModel source) {
        log.info("Tworzenie nowego uzytkownika: " + source.getEmail());
        var p = repo.save(source.toPodopieczny());
        return new PodopiecznyReadModel(p);
    }

    public List<PodopiecznyReadModel> getAll() {
        log.warn("Pobieranie wszystkich użytkowników do odczytu");
        return repo.findAll().stream()
                .filter(u -> !u.isRemoved())
                .map(PodopiecznyReadModel::new)
                .collect(Collectors.toList());
    }

    public Slice<PodopiecznyReadModel> getAllSlice(int pageNo) {
        log.info("Zwracanie slice podopiecznych - strona " + pageNo);
        int RESULTS_PER_PAGE = 9;
        var req = PageRequest.of(pageNo, RESULTS_PER_PAGE);
        return repo.findByRemovedIsFalse(req).map(PodopiecznyReadModel::new);
    }


    public Page<PodopiecznyReadModel> getAllPage(int pageNo, String sort, Boolean reverese, Boolean onlyactive) {
        log.info("Pobieranie strony użytkowników do odczytu");
        Pageable pageable;
        Sort.Order order;
        if (reverese) {
            order = new Sort.Order(Sort.Direction.DESC, sort).ignoreCase();
        } else {
            order = new Sort.Order(Sort.Direction.ASC, sort).ignoreCase();
        }
        pageable = PageRequest.of(pageNo, 10, Sort.by(order));

        Page<Podopieczny> listaPage;

        if (onlyactive)
            listaPage = repo.findByAktywnyIsTrue(pageable);
        else
            listaPage = repo.findAll(pageable);


        List<PodopiecznyReadModel> listaContent = listaPage.stream()
                .map(PodopiecznyReadModel::new)
                .collect(Collectors.toList());
        return new PageImpl<>(listaContent, pageable, listaPage.getTotalElements());

    }


    @Transactional
    public RegistrationStatus registerDeviceStatus(EmailRegistrationDTO registerSet) {
        log.info("Odpowiadanie na żądanie rejestracji urządzenia przenośnego");
        var user = repo.findByEmail(registerSet.getEmailAddress().trim());

        if (user.isEmpty())
            return RegistrationStatus.EMAIL_NOT_FOUND;

        if (user.get().isRemoved())
            return RegistrationStatus.EMAIL_NOT_FOUND;


        var userTokens = tokenRepo.findByOwnerAndActiveIsTrue(user.get());

        if (userTokens.size() == 0) {
            appClientService.createNewDeviceSession(user.get(), registerSet);
            return RegistrationStatus.ACTIVATION_SENT;
        } else {
            Optional<Token> matchingToken = userTokens.stream()
                    .sorted(Comparator.comparing(Token::getWhenCreated))
                    .filter(t -> t.getTokenString().equals(registerSet.getToken()))
                    .findFirst();

            if (matchingToken.isPresent()) {
                if (matchingToken.get().getAssignedDeviceId().equals(registerSet.getAndroidDeviceId()) && !matchingToken.get().isRemoved())
                    return RegistrationStatus.ALREADY_OK;
                else
                    return RegistrationStatus.DEVICE_MISMATCH;
            }

            if (userTokens.stream().anyMatch(t -> !t.getAssignedDeviceId().equals(registerSet.getAndroidDeviceId()) && !t.isRemoved())) {
                appClientService.createNewDeviceSession(user.get(), registerSet);
                return RegistrationStatus.NEW_DEVICE;
            } else {
                userTokens.stream()
                        .filter(t -> t.getAssignedDeviceId().equals(registerSet.getAndroidDeviceId()) && !t.isRemoved())
                        .forEach(t -> t.setRemoved(true));
                appClientService.createNewDeviceSession(user.get(), registerSet);
                return RegistrationStatus.ACTIVATION_SENT;
            }


        }

    }


    @Transactional
    public void toggleSetting(String setting, boolean isChecked, String token) throws IllegalArgumentException, NotFoundCustomException {
        log.info("Zmiana ustawień użytkownika na żądanie z urządzenia przenośnego. Ustawienie: " + setting + ", zmiana na: " + isChecked);
        if (setting.equals("tipnotification")) {
            var tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
            tokenFound.ifPresentOrElse(t -> t.getOwner().setSettingTipNotifications(isChecked), () -> {
                throw new NotFoundCustomException();
            });

        } else {
            log.error("Nie można zmienić ustawienia, bo nie ma takiego ustawienia");
            throw new IllegalArgumentException("No \"" + setting + "\" seetting avalibale");
        }

    }

    public boolean isEmailAvailableForUse(String email) {
        log.info("Sprawdzanie dostępności adresu e-mail do rejestracji dla: " + email);
        return !repo.existsByEmail(email.toLowerCase());
    }

    public boolean isPhoneNumberAvailableForUser(String phoneNumber) {
        log.info("Sprawdzanie dostępności numeru telefonu do rejestracji dla: " + phoneNumber);
        try {
            return !repo.existsByPhoneNumber(Integer.parseInt(phoneNumber));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("PARAM_ERROR");
        }
    }

	public List<PodopiecznyReadModel> returnElasticsearchResult(String searchKey) {
        if(isInteger(searchKey)) {
            log.info("Inicjacja zapytania wyszukania podopiecznego (numer telefonu) z frazą: " + searchKey);
            return customRepo.elasticSearchFindByPhoneNumber(searchKey).stream()
                    .map(PodopiecznyReadModel::new)
                    .collect(Collectors.toList());
        }
        else {
            log.info("Inicjacja zapytania wyszukania podopiecznego z frazą: " + searchKey);
            return customRepo.elasticSearchFindByKeyWord(searchKey).stream()
                    .map(PodopiecznyReadModel::new)
                    .collect(Collectors.toList());
        }
	}

	private boolean isInteger(String searchKey) {
        if (searchKey == null) {
            return false;
        }
        int length = searchKey.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (searchKey.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = searchKey.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

}
