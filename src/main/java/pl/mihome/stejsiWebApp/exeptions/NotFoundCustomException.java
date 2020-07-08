package pl.mihome.stejsiWebApp.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundCustomException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5759832432660724289L;

}
