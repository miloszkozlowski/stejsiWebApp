package pl.mihome.stejsiWebApp.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Secured("ROLE_STEJSI")
@RequestMapping("/ustawienia")
public class UstawieniaController {

	
	public UstawieniaController() {
	
	}

	@GetMapping
	String settingsMenu() {
		
		return "settings";
	}
	
	
	
}
