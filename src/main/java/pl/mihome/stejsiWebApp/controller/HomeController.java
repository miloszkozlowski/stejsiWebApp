package pl.mihome.stejsiWebApp.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.mihome.stejsiWebApp.logic.AndroidNotificationsService;

@Controller
@RequestMapping("/")
public class HomeController {
	
	AndroidNotificationsService notificationService;
	
	

	public HomeController(AndroidNotificationsService notificationService) {
		this.notificationService = notificationService;
	}

	String homePage() {
		return "index";
	}
	
	@Secured("ROLE_STEJSI")
	@GetMapping("/login")
	String logowanie() {
		return "index";
	}
	
}
