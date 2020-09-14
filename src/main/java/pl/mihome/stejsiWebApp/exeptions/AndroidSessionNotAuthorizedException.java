package pl.mihome.stejsiWebApp.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AndroidSessionNotAuthorizedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2350147812712041376L;

	

}
