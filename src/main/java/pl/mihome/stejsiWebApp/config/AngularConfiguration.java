package pl.mihome.stejsiWebApp.config;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


@Configuration
@ConfigurationProperties("angular")
@Validated
public class AngularConfiguration {
	
	@NotBlank
	private String webServerUrl;
	
	@Min(1)
	@NotNull
	private Long maxStoredPictureSize;


	public String getWebServerUrl() {
		return webServerUrl;
	}

	public void setWebServerUrl(String webServerUrl) {
		this.webServerUrl = webServerUrl;
	}

	public Long getMaxStoredPictureSize() {
		return maxStoredPictureSize;
	}

	public void setMaxStoredPictureSize(Long maxStoredPictureSize) {
		this.maxStoredPictureSize = maxStoredPictureSize;
	}
	

	
}
