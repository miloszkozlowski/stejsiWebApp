package pl.mihome.stejsiWebApp.logic;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import pl.mihome.stejsiWebApp.model.PushNotificationRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FirebaseCloudMessagingService {
	
	private static final Logger log = LoggerFactory.getLogger(FirebaseCloudMessagingService.class);
	
	public void sendMessage(Map<String, String> data, PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
		
        Message message = getPreconfiguredMessageWithData(data, request);
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //String jsonOutput = gson.toJson(message);
        sendAndGetResponse(message);
        log.info("Wysłano powiadomienie z danymi do konkretnego tokenu: " + request.getTitle() + ": " + request.getBody());
        
    }
	
	public void sendMessageWithoutData(PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageWithoutData(request);
        String response = sendAndGetResponse(message);
        log.info("Wysłano powiadomienie ogóle dla tematu: " + request.getTopic() + ", " + response);
    }
	
	public void sendMessageWithoutDataToToken(PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageWithoutDataToToken(request);
        String response = sendAndGetResponse(message);
        log.info("Wysłano powiadomienie na konkretny token: " + response);
    }
	
	private Message getPreconfiguredMessageWithData(Map<String, String> data, PushNotificationRequest request) {
		return getPreconfiguredMessageBuilder(request).putAllData(data).setToken(request.getUserToken())
	                .build();
	    }
	
	private Message getPreconfiguredMessageWithoutData(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).setTopic(request.getTopic())
                .build();
    }
	
	private Message getPreconfiguredMessageWithoutDataToToken(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).setToken(request.getUserToken())
                .build();
    }
	
	private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequest request) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTitle());
        ApnsConfig apnsConfig = getApnsConfig(request.getTitle());
        Notification.Builder builder = Notification.builder();
        builder.setBody(request.getBody());
        builder.setTitle(request.getTitle());
        return Message.builder()
                .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(builder.build());
    }
	
	private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder().setSound("default")
                        .setColor("#373438").setTag(topic).build()).build();
    }
	
    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }
    
    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }
}
