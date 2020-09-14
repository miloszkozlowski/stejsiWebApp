package pl.mihome.stejsiWebApp.controller;

import javax.validation.Valid;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pl.mihome.stejsiWebApp.logic.TrainerDataService;
import pl.mihome.stejsiWebApp.model.TrainerData;

@Controller
@Secured("ROLE_STEJSI")
@RequestMapping("/ustawienia")
public class UstawieniaController {

	private TrainerDataService trainerDataService;
	

	public UstawieniaController(TrainerDataService trainerDataService) {
		this.trainerDataService = trainerDataService;
	}

	@GetMapping
	String settingsMenu() {
		
		return "settings";
	}
	
	@GetMapping("/danetrenera/{edit}")
	String trainerData(@PathVariable String edit, Model model) {
		var currentData = trainerDataService.getCurrentData();
		model.addAttribute("currentSettings", currentData);
		model.addAttribute("opcja", "danetrenera");
		model.addAttribute("edit", edit);
		
		
		return "settings";
	}
	
	@GetMapping("/danetrenera")
	String trainerData(Model model) {
		var currentData = trainerDataService.getCurrentData();
		model.addAttribute("currentSettings", currentData);
		model.addAttribute("opcja", "danetrenera");
		
		return "settings";
	}
	
	@PostMapping("/danetrenera/{edit}")
	String newTrainerData(@Valid @ModelAttribute TrainerData trainerData, 
			BindingResult bindingResult, 
			@PathVariable String edit,
			Model model,
			RedirectAttributes redir) {
		
		if(!bindingResult.hasErrors()) {
			trainerDataService.newSettings(trainerData);
			redir.addFlashAttribute("msg", "Adres e-mail został zapisany");
			return "redirect:/ustawienia/danetrenera";
		}
		
		model.addAttribute("currentSettings", trainerData);
		model.addAttribute("opcja", "danetrenera");
		model.addAttribute("edit", edit);
		return "settings";
	}

	
}
