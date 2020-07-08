package pl.mihome.stejsiWebApp.DTO.appComms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonAlias;

public class EmailRegistrationDTO {
	
	@NotBlank
	@Pattern(regexp = "^\\w+[\\w-\\.]*\\@\\w+((-\\w+)|(\\w*))\\.[a-z]{2,3}$")
	@JsonAlias("email")
	private String emailAddress;
	
	@NotBlank
	@Pattern(regexp = "^\\w+$")
	private String token;
	
	@NotBlank
	@JsonAlias("device")
	private String androidDeviceId;

	public EmailRegistrationDTO() {
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAndroidDeviceId() {
		return androidDeviceId;
	}

	public void setAndroidDeviceId(String androidDeviceId) {
		this.androidDeviceId = androidDeviceId;
	}
	
	
	
	

}
