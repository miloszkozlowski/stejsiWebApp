package pl.mihome.stejsiWebApp.constraints;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageContentValidator implements ConstraintValidator<ImageContent, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		
		//nie sprawdza, kiedy adres jest pusty
		if(value == null) {
			return true; 
		}
		if(value.isBlank()) {
			return true;
		}
		
		try {
			URL url = new URL(value);
			HttpURLConnection connection = (HttpURLConnection)  url.openConnection();
			connection.setRequestMethod("HEAD");
			connection.connect();
			String contentType = connection.getContentType();
			for(ALLOWED_TYPES t: ALLOWED_TYPES.values()) {
				if(t.getDescription().contentEquals(contentType)) {
					return true;
				}
			}
		}
		catch(IOException ex) {
			return false;
		}
		return false;
	}
	
	private enum ALLOWED_TYPES {
		
		JPEG("image/jpeg"),
		JPG("image/jpg"),
		GIF("image/gif"),
		PNG("image/png");
		
		private String description; 

		ALLOWED_TYPES(String description) {
			this.description = description;
		}
		
		protected String getDescription() {
			return description;
		}
	}

}
