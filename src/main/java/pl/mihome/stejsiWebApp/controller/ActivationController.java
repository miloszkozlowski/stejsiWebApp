package pl.mihome.stejsiWebApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import pl.mihome.stejsiWebApp.logic.AppClientService;
import pl.mihome.stejsiWebApp.logic.PodopiecznyService;

@Controller
public class ActivationController {
	
	PodopiecznyService userService;
	AppClientService appClientService;
	
	public ActivationController(PodopiecznyService userService, AppClientService appClientService) {
		this.userService = userService;
		this.appClientService = appClientService;
	}

	@GetMapping("/activate/{id}/{key}")
	public String activateUser(@PathVariable Long id, @PathVariable String key, Model model) {
		model.addAttribute("success", appClientService.activateToken(id, key));
		return "activated";
	}


}
