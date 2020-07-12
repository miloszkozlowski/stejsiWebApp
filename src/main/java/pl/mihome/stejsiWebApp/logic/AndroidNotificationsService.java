package pl.mihome.stejsiWebApp.logic;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pl.mihome.stejsiWebApp.model.PushNotificationRequest;
import pl.mihome.stejsiWebApp.model.Tip;

@Service
public class AndroidNotificationsService {

	private FirebaseCloudMessagingService fcmService;
	
	private static final Logger log = LoggerFactory.getLogger(AndroidNotificationsService.class);

	
	public AndroidNotificationsService(FirebaseCloudMessagingService fcmService) {
		this.fcmService = fcmService;
	}
	
	public void notifyUserAboutNewTip(Tip tip, String tokenFCM, String userName) {
		var push = new PushNotificationRequest(userName + ", sprawdź nową poradę od Stejsi", "\"" + tip.getHeading() + "\"");
		push.setUserToken(tokenFCM);
		
		Map<String, String> data = new HashMap<>();
		data.put("tipId", tip.getId().toString());
		try {
			fcmService.sendMessage(data, push);
		}
		catch(Exception ex) {
			log.error("Nieudana wysyłka powiadomienia o nowym tip: " + ex.getMessage());
		}
	}
	

	public void notifyUserOnNewTrainingPlanned(String trainingDateTime, String tokenFCM) {
		PushNotificationRequest push;
		if(trainingDateTime == "") {
			push = new PushNotificationRequest("Masz nowe terminy treningów", "Stejsi zaplanowała treningi personalne dla Ciebie. Potwierdź proszę terminy w apliakcji.");
		}
		else {
			push = new PushNotificationRequest("Nowy trening personalny został zaplanowany", "Stejsi zaplanowała trening na " + trainingDateTime + ". Potwierdź proszę termin w aplikacji.");
		}
		
		push.setUserToken(tokenFCM);
		try {
		fcmService.sendMessageWithoutDataToToken(push);
		}
		catch(Exception ex) {
			log.error("Nieudana wysyłka powiadomienia o nowym zaplanowanym treningu: " + ex.getMessage());
		}
	}
	
	public void notifyUserOnPresenceToBeConfirmed(String tokenFCM) {
		PushNotificationRequest push = new PushNotificationRequest("Stejsi dała niezły wycisk?", "Potwierdź obecność na treningu w aplikacji.");
		push.setUserToken(tokenFCM);
		try {
		fcmService.sendMessageWithoutDataToToken(push);
		}
		catch(Exception ex) {
			log.error("Nieudana wysyłka powiadomienia o potwierdzeniu obecności: " + ex.getMessage());
		}
	}
	
	public void notifyUserOnLatePresenceToBeConfirmed(String tokenFCM) {
		PushNotificationRequest push = new PushNotificationRequest("Lista obecności wymaga uwagi", "Otwórz aplikację i uzupełnij listę obecności.");
		push.setUserToken(tokenFCM);
		try {
		fcmService.sendMessageWithoutDataToToken(push);
		}
		catch(Exception ex) {
			log.error("Nieudana wysyłka ponaglającego powiadomienia o potwierdzeniu obecności: " + ex.getMessage());
		}
	}
	
    public void sendPushNotification(PushNotificationRequest request) {
        try {
            fcmService.sendMessageWithoutData(request);
        } catch (Exception e) {
            log.error("Bład wysyłania powiadomienia bez danych: " + e.getMessage());
        }
    }
//    
//    private Map<String, String> getSamplePayloadData() {
//        Map<String, String> pushData = new HashMap<>();
//        pushData.put("messageId", "msgid");
//        pushData.put("text", "txt");
//        pushData.put("user", "pankaj singh");
//        return pushData;
//    }
	
	
}
