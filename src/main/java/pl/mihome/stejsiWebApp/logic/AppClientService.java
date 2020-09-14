package pl.mihome.stejsiWebApp.logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.SendFailedException;

import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;

import org.slf4j.LoggerFactory;

import pl.mihome.stejsiWebApp.DTO.PodopiecznyReadModel;
import pl.mihome.stejsiWebApp.DTO.appComms.EmailRegistrationDTO;
import pl.mihome.stejsiWebApp.DTO.appComms.TipReadModel;
import pl.mihome.stejsiWebApp.config.AndroidAppConfiguration;
import pl.mihome.stejsiWebApp.exeptions.AndroidSessionNotAuthorizedException;
import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.TipRepo;
import pl.mihome.stejsiWebApp.model.Token;
import pl.mihome.stejsiWebApp.model.TokenRepo;
import pl.mihome.stejsiWebApp.model.appClient.RegistrationAttemp;

@Service
public class AppClientService {

	private List<RegistrationAttemp> registerAttemptsList = new ArrayList<>();

	
	private AndroidAppConfiguration androidAppConfiguration;
	private TokenRepo tokenRepo;
	private EmailSenderService emailSenderService;
	private TipRepo tipRepo;
	
	private static final Logger log = LoggerFactory.getLogger(AppClientService.class);

	public AppClientService(
			AndroidAppConfiguration androidAppConfiguration,
			TokenRepo tokenRepo,
			EmailSenderService emailSenderService,
			TipRepo tipRepo) {

		this.androidAppConfiguration = androidAppConfiguration;
		this.tokenRepo = tokenRepo;
		this.emailSenderService = emailSenderService;
		this.tipRepo = tipRepo;
	}
	
	public Boolean registerAttempAllowed(RegistrationAttemp attemp) {		
		log.info("Zapisywanie próby rejestracji z urządzenia przenośnego");
		/*
		 * Faktyczne sprawdzenie, czy w ciągu ostatnich {@code HOURS_FOR_REGISTRATION_LIMIT} ilość prób nie przekracza limitu {@code ALLOWED_AMOUNT_OF_REGISTRATION_ATTEMPS}
		 */
		if(registerAttemptsList.isEmpty()) {
			registerAttemptsList.add(attemp);
			log.info("Nie było wcześniej prób z tego urządzenia");
			return true;
		}
			
		else {
			/*
			 * Usuwanie rekordów starszych niż 1 godzina + {@code HOURS_FOR_REGISTRATION_LIMIT}
			 */
			registerAttemptsList = registerAttemptsList.stream()
			.filter(a -> a.getRequestDateTime().isAfter(LocalDateTime.now().minusHours(1 + androidAppConfiguration.getRegistration().getSuspentionTimeInHours())))
			.collect(Collectors.toList());
			
			/*
			 * Właściwe sprawdzenie ilości prób rejestracji w ciągu określonego w konfiguracji czasu
			 */
			var tries = registerAttemptsList.stream()
			.filter(a -> LocalDateTime.now().minusHours(androidAppConfiguration.getRegistration().getSuspentionTimeInHours()).isBefore(a.getRequestDateTime()))
			.filter(a -> (a.getRequestDevId().equals(attemp.getRequestDevId()) || a.getRequestIpAdd().equals(attemp.getRequestIpAdd())))
			.count();
			
			
			
			log.info("Ilość prób rejestracji o tych samych identyfikatorach: " + tries);
			
			if(tries >= androidAppConfiguration.getRegistration().getAllowedAttempsAmount())
				return false;
			
			registerAttemptsList.add(attemp);
			return true;
		}
		
	}
	
	@Async
	@Transactional
	public void createNewDeviceSession(Podopieczny user, EmailRegistrationDTO registerSet) {	
		log.info("Tworzenie nowej sesji użytkownika w ramach rejestracji na urządzeniu");
		tokenRepo.deleteAllByAssignedDeviceId(registerSet.getAndroidDeviceId());
		
		clearInactiveTokens();
		
		
		String activationCode = generateActivationCode();
        
		Token token = new Token(user, registerSet.getToken(), registerSet.getAndroidDeviceId(), registerSet.getTokenFCM(), activationCode);
		tokenRepo.save(token);
		try {
			sendActivationEmail(user, activationCode);
		}
		catch(SendFailedException ex) {
			log.error("Nieudana wysyłka wiadomości e-mail do aktywacji konta w ramach rejestracji: " + ex.getMessage());
		}
	}

	private String generateActivationCode() {
		log.info("Generowanie kodu do aktywacji sesji");
		// Generowanie kodu aktywacyjnego 16-znakowego
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";       
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        var activationCode = sb.toString();
		return activationCode;
	}
	
	
	private void sendActivationEmail(Podopieczny user, String activationCode) throws SendFailedException {
		String subject = "Stejsi - Twój Trener Personalny: aktywuj aplikację!";
		String text = "Witaj " + user.getImie() +", \n"
				+ "aby korzystać z aplikacji kliknij w link: \n\n"
				+ androidAppConfiguration.getWebServerUrl() + "/activate/" + user.getId() + "/" + activationCode + "\n\n"
				+ "Pozdrawiam\nStejsi";
		
		emailSenderService.sendSimpleMessage(user.getEmail(), subject, text);
		log.info("E-mail aktywacyjny wysłany do " + user.getEmail());
	}
	
	private void clearInactiveTokens() {
		log.info("Czyszczenie nieaktywnych tokenów");
		tokenRepo.removeOutOfDate(LocalDateTime.now().minusDays(androidAppConfiguration.getInnactiveTokenValidityInDays()));
	}
	
	public boolean isAuthorized(String token, String deviceId) throws AndroidSessionNotAuthorizedException {
		log.info("Odpowiadanie na zapytanie autoryzacyjne");
		var tokenFound = tokenRepo.findByTokenStringAndAssignedDeviceIdAndRemovedIsFalse(token, deviceId);
		if(tokenFound.isEmpty()) {
			log.warn("Nie można było udzielić autoryzacji, bo token nie istnieje w bazie. Zginął?");
			throw new AndroidSessionNotAuthorizedException();
		}
		else {
			return tokenFound.get().isActive();
		}
	}
	
	@Transactional
	public boolean activateToken(Long uid, String activationCode) {
		log.info("Przetwarzanie żądania aktywacji tokenu");
		var token = tokenRepo.findByActivationCodeAndActiveIsFalse(activationCode);
		if(token.isPresent())
		{
			if(token.get().getOwner().getId().equals(uid)) {
				//usuwanie poprzednich sesji
				var oldTokens = tokenRepo.findByOwnerAndActiveIsTrue(token.get().getOwner());
				oldTokens.stream()
				.forEach(t -> t.setRemoved(true));
				
				//dodawanie nowej sesji
				token.get().setActive(true);
				
				//pierwotna akytacja użytkownika 
				if(!token.get().getOwner().isAktywny()) {
					token.get().getOwner().setAktywny(true);
				}
				
				//dopisanie do topicu FCM
				try {
					TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(
					    Arrays.asList(token.get().getTokenFCM()), FirebaseCloudMessagingInit.GENERAL_TOPIC);
					log.info("Aktywacja nowego tokenu - dopisanie do głównego topic'u w FCM udane: " + response.getSuccessCount() + "/1");
				}
				catch(FirebaseMessagingException ex) {
					log.warn("Aktywacja nowego tokenu - nie udało się dopisać do głównego topic'u w FCM, bo: " + ex.getMessage());
				}
				return true;
			}
		}
		
		return false;
	}
	
	public PodopiecznyReadModel getUser(String token) throws AndroidSessionNotAuthorizedException {
		log.info("Pobieranie danych użytkownika na podstawie tokenu");
		var foundToken = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
		if(foundToken.isPresent()) {
			var user = new PodopiecznyReadModel(foundToken.get().getOwner());
			return user;
		}
			
		log.warn("Nie można było pobrać danych użytkownika, bo nie ma odpowiadającego tokenu");
		throw new AndroidSessionNotAuthorizedException();
	}
	
	public List<TipReadModel> getTips(String token) throws AndroidSessionNotAuthorizedException {
		log.info("Pobieranie listy tipów na podstawie tokenu");
		var foundToken = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
		
		if(foundToken.isPresent()) {
			var user = foundToken.get().getOwner();
			return tipRepo.findByRemovedIsFalse().stream()
					.map(tip -> new TipReadModel(tip, user))
					.collect(Collectors.toList());
		}
		log.warn("Nie można było pobrać tipów, bo nie ma odpowiadającego tokenu");
		throw new AndroidSessionNotAuthorizedException();

	}

	@Transactional
	public void logout(String token) throws AndroidSessionNotAuthorizedException {
		log.info("Usuwanie tokenu po żądaniu wylogowania");
		var foundToken = getAuthorizedToken(token);
		foundToken.setRemoved(true);
		try {
			TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopic(Arrays.asList(foundToken.getTokenFCM()), FirebaseCloudMessagingInit.GENERAL_TOPIC);
			log.info("Wylogowanie z urządzenia - wypisanie z głównego topic'u FCM: " + response.getSuccessCount() + "/1");
		} catch (FirebaseMessagingException e) {
			log.warn("Podczas wylogowywania nastapił problem z wypisaniem z FCM topic: " + e.getMessage());
		}

	}

	@Transactional
	public void registerFCMToken(String token, String newFCMToken) {
		log.info("Dopisywanie nowego tokenu FCM do danych sesji");
		var authToken = getAuthorizedToken(token);
		authToken.setTokenFCM(newFCMToken);
	}
	
	private Token getAuthorizedToken(String token) throws AndroidSessionNotAuthorizedException {
		log.info("Pobieranie danych tokenu na podstawie tokenu");
		var foundToken = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
		if(foundToken.isPresent()) {
			return foundToken.get();
		} else {
			throw new AndroidSessionNotAuthorizedException();
		}
	}

	
}
