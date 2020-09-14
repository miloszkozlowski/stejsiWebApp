package pl.mihome.stejsiWebApp.model.appClient;

import java.time.LocalDateTime;

public class RegistrationAttemp {
	
	private LocalDateTime requestDateTime;
	private String requestIpAdd;
	private String requestDevId;
	private String requestEmail;
	public RegistrationAttemp(LocalDateTime requestDateTime, String requestIpAdd, String requestDevId,
			String requestEmail) {
		super();
		this.requestDateTime = requestDateTime;
		this.requestIpAdd = requestIpAdd;
		this.requestDevId = requestDevId;
		this.requestEmail = requestEmail;
	}
	
	public LocalDateTime getRequestDateTime() {
		return requestDateTime;
	}
	public String getRequestIpAdd() {
		return requestIpAdd;
	}
	public String getRequestDevId() {
		return requestDevId;
	}
	public String getRequestEmail() {
		return requestEmail;
	}
	
	
	
}
