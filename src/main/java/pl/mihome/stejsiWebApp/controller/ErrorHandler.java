package pl.mihome.stejsiWebApp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandler {

	
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Wrong data provided")
		void onIllegalArgumentResponse(IllegalArgumentException ex) {}
	
	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "State cannot be changed")
		void onIllegalStatetResponse(IllegalStateException ex) {}
}
