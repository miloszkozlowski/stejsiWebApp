package pl.mihome.stejsiWebApp.controller;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(basePackages = {"pl.mihome.stejsiWebApp.controller"})
@Order(2)
public class ErrorHandler {

	
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Wrong data provided")
		void onIllegalArgumentResponse(IllegalArgumentException ex) {}
	
	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "State cannot be changed")
		void onIllegalStateResponse(IllegalStateException ex) {}
}
