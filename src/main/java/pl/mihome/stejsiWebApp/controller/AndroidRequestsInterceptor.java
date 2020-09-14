package pl.mihome.stejsiWebApp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import pl.mihome.stejsiWebApp.config.AndroidAuthorization;
import pl.mihome.stejsiWebApp.exeptions.AndroidSessionNotAuthorizedException;
import pl.mihome.stejsiWebApp.logic.AppClientService;

public class AndroidRequestsInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private AppClientService appClientService;
	
	
	public AndroidRequestsInterceptor() {
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		
		HandlerMethod handlerMethod;

		if(handler instanceof HandlerMethod) {
			handlerMethod = (HandlerMethod) handler;
		}
		else {
			return true;
		}

		AndroidAuthorization androidAuthorization = handlerMethod.getMethod().getAnnotation(AndroidAuthorization.class);
		if(androidAuthorization == null)
			return true;

		String token = request.getHeader("token");
		String deviceId = request.getHeader("deviceId");

		if(token == null || deviceId == null)
			throw new AndroidSessionNotAuthorizedException();

		if(!appClientService.isAuthorized(token, deviceId))
			throw new AndroidSessionNotAuthorizedException();
		
		return true;
	}

	
}
