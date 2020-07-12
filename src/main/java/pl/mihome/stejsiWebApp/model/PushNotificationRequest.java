package pl.mihome.stejsiWebApp.model;

public class PushNotificationRequest {
	
	private String title;
	private String body;
	private String userToken;
	private String topic;
	
	
	public PushNotificationRequest() {
	}
	
	public PushNotificationRequest(String title, String body) {
		this.title = title;
		this.body = body;
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

}
