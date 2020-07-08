package pl.mihome.stejsiWebApp.config;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


@Configuration
@ConfigurationProperties("androidapp")
@Validated
public class AndroidAppConfiguration {
	
	private Registration registration;
	
	@NotBlank
	private String webServerUrl;
	
	@Min(1)
	private int innactiveTokenValidityInDays;
	


	public Registration getRegistration() {
		return registration;
	}

	public void setRegistration(Registration registration) {
		this.registration = registration;
	}


	public String getWebServerUrl() {
		return webServerUrl;
	}

	public void setWebServerUrl(String webServerUrl) {
		this.webServerUrl = webServerUrl;
	}


	public int getInnactiveTokenValidityInDays() {
		return innactiveTokenValidityInDays;
	}

	public void setInnactiveTokenValidityInDays(int innactiveTokenValidityInDays) {
		this.innactiveTokenValidityInDays = innactiveTokenValidityInDays;
	}




	public static class Registration {
		
		@NotNull
		@Min(1)
		private int allowedAttempsAmount;
		
		@Min(1)
		@NotNull
		private int suspentionTimeInHours;
		
		public int getAllowedAttempsAmount() {
			return allowedAttempsAmount;
		}
		public void setAllowedAttempsAmount(int allowedAttempsAmount) {
			this.allowedAttempsAmount = allowedAttempsAmount;
		}
		public int getSuspentionTimeInHours() {
			return suspentionTimeInHours;
		}
		public void setSuspentionTimeInHours(int suspentionTimeInHours) {
			this.suspentionTimeInHours = suspentionTimeInHours;
		}
		
	}
	
	
}
