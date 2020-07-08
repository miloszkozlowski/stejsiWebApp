package pl.mihome.stejsiWebApp.logic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.SendFailedException;

import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.LoggerFactory;

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

	private List<RegistrationAttemp> registerAttempsList;

	
	private AndroidAppConfiguration androidAppConfiguration;
	private TokenRepo tokenRepo;
	private EmailSenderService emailSenderService;
	private TipRepo tipRepo;
	
	private static final Logger log = LoggerFactory.getLogger(AppClientService.class);

	public AppClientService(List<RegistrationAttemp> registerAttempsList,
			AndroidAppConfiguration androidAppConfiguration,
			TokenRepo tokenRepo,
			EmailSenderService emailSenderService,
			TipRepo tipRepo) {
		
		this.registerAttempsList = registerAttempsList;
		this.androidAppConfiguration = androidAppConfiguration;
		this.tokenRepo = tokenRepo;
		this.emailSenderService = emailSenderService;
		this.tipRepo = tipRepo;
	}
	
	public Boolean registerAttempAllowed(RegistrationAttemp attemp) {		
		
		/*
		 * Faktyczne sprawdzenie, czy w ciągu ostatnich {@code HOURS_FOR_REGISTRATION_LIMIT} ilość prób nie przekracza limitu {@code ALLOWED_AMOUNT_OF_REGISTRATION_ATTEMPS}
		 */
		if(registerAttempsList.isEmpty()) {
			registerAttempsList.add(attemp);
			return true;
		}
			
		else {
			/*
			 * Usuwanie rekordów starszych niż 1 godzina + {@code HOURS_FOR_REGISTRATION_LIMIT}
			 */
			registerAttempsList = registerAttempsList.stream()
			.filter(a -> a.getRequestDateTime().isAfter(LocalDateTime.now().minusHours(1 + androidAppConfiguration.getRegistration().getSuspentionTimeInHours())))
			.collect(Collectors.toList());
			
			/*
			 * Właściwe sprawdzenie ilości prób rejestracji w ciągu określonego w konfiguracji czasu
			 */
			var tries = registerAttempsList.stream()
			.filter(a -> LocalDateTime.now().minusHours(androidAppConfiguration.getRegistration().getSuspentionTimeInHours()).isBefore(a.getRequestDateTime()))
			.filter(a -> (a.getRequestDevId().equals(attemp.getRequestDevId()) || a.getRequestIpAdd().equals(attemp.getRequestIpAdd())))
			.count();
			
			
			
			log.info("Ilość prób rejestracji o tych samych identyfikatorach: " + tries);
			
			if(tries >= androidAppConfiguration.getRegistration().getAllowedAttempsAmount())
				return false;
			
			registerAttempsList.add(attemp);
			return true;
		}
		
	}
	
	@Async
	@Transactional
	public void createNewDeviceSession(Podopieczny user, EmailRegistrationDTO registerSet) throws SendFailedException {		
		tokenRepo.deleteAllByAssignedDeviceId(registerSet.getAndroidDeviceId());
		
		clearInactiveTokens();
		
		String activationCode = generateActivationCode();
        
		Token token = new Token(user, registerSet.getToken(), registerSet.getAndroidDeviceId(), activationCode);
		tokenRepo.save(token);
		
		sendActivationEmail(user, activationCode);
	}

	private String generateActivationCode() {
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
		tokenRepo.removeOutOfDate(LocalDateTime.now().minusDays(androidAppConfiguration.getInnactiveTokenValidityInDays()));
	}
	
	public boolean isAuthorized(String token, String deviceId) throws AndroidSessionNotAuthorizedException {
		var tokenFound = tokenRepo.findByTokenStringAndAssignedDeviceIdAndRemovedIsFalse(token, deviceId);
		if(tokenFound.isEmpty()) {
			throw new AndroidSessionNotAuthorizedException();
		}
		else {
			return tokenFound.get().isActive();
		}
	}
	
	@Transactional
	public boolean activateToken(Long uid, String activationCode) {
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
				if(!token.get().getOwner().isAktywny()) {
					token.get().getOwner().setAktywny(true);
					
				}
				return true;
			}
		}
		
		return false;
	}
	
	public Podopieczny getUser(String token) throws AndroidSessionNotAuthorizedException {
		var foundToken = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
		if(foundToken.isPresent())
			return foundToken.get().getOwner();
		
		throw new AndroidSessionNotAuthorizedException();
	}
	
	public List<TipReadModel> getTips(String token) throws AndroidSessionNotAuthorizedException {
		var foundToken = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
		
		if(foundToken.isPresent()) {
			var user = foundToken.get().getOwner();
			return tipRepo.findByRemovedIsFalse().stream()
					.map(tip -> new TipReadModel(tip, user))
					.collect(Collectors.toList());
		}
		
		throw new AndroidSessionNotAuthorizedException();

	}

	@Transactional
	public void logout(String token) throws AndroidSessionNotAuthorizedException {
		var foundToken = getAuthorizedToken(token);
		foundToken.setRemoved(true);

	}

	@Transactional
	public void registerFCMToken(String token, String newFCMToken) {
		var authToken = getAuthorizedToken(token);
		authToken.setTokenFCM(newFCMToken);
	}
	
	private Token getAuthorizedToken(String token) {
		var foundToken = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
		if(foundToken.isPresent()) {
			return foundToken.get();
		} else {
			throw new AndroidSessionNotAuthorizedException();
		}
	}

	
}
