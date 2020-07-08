package pl.mihome.stejsiWebApp.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
//@Secured("ROLE_STEJSI")
public class HomeController {

	String homePage() {
		return "index";
	}
	
	@Secured("ROLE_STEJSI")
	@GetMapping("/login")
	String logowanie() {
		return "index";
	}
}
