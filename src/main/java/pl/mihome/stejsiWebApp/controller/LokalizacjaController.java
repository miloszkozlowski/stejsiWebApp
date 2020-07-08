package pl.mihome.stejsiWebApp.controller;

import javax.validation.Valid;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pl.mihome.stejsiWebApp.logic.LokalizacjaService;
import pl.mihome.stejsiWebApp.model.Lokalizacja;

@Controller
@Secured("ROLE_STEJSI")
@RequestMapping("/ustawienia/lokalizacje")
public class LokalizacjaController {

	private LokalizacjaService locationService;

	
	public LokalizacjaController(LokalizacjaService locationService) {
		this.locationService = locationService;
	}
	
	@GetMapping
	String locations(Model model) {
		model.addAttribute("nowaLokalizacja", new Lokalizacja());
		model.addAttribute("lokalizacje", locationService.getAll());
		return "settings";
	}
	
	@PostMapping
	String addNewLocation(@Valid @ModelAttribute("nowaLokalizacja") Lokalizacja nowaLokalizacja,
			BindingResult bindingResult,
			Model model
			) {
		if(!bindingResult.hasErrors()) {
			locationService.addNew(nowaLokalizacja);
			model.addAttribute("msg", "Nowa lokalizacja został dodana");
			model.addAttribute("nowaLokalizacja", new Lokalizacja());
		}
		model.addAttribute("lokalizacje", locationService.getAll());
		return "settings";
	}
	
	@PostMapping("/domyslna")
	String setDefaultLocation(@RequestParam(name = "lid") Long lid,
			Model model) {
		locationService.setNewDefault(lid);
		model.addAttribute("nowaLokalizacja", new Lokalizacja());
		model.addAttribute("lokalizacje", locationService.getAll());
		return "settings";
	}
	
	@PostMapping("/usun")
	String removeLocation(@RequestParam(name = "lid_remove") Long lid,
			Model model) {
		locationService.remove(lid);
		model.addAttribute("nowaLokalizacja", new Lokalizacja());
		model.addAttribute("lokalizacje", locationService.getAll());
		return "settings";	
	}
	
	@ModelAttribute
	void addAttirbutes(Model model) {
		model.addAttribute("opcja", "lokalizacje");
		
	}
	
	
}
