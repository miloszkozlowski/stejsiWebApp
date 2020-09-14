package pl.mihome.stejsiWebApp.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pl.mihome.stejsiWebApp.DTO.RodzajPakietuWriteModel;
import pl.mihome.stejsiWebApp.logic.RodzajPakietuService;
import pl.mihome.stejsiWebApp.model.RodzajPakietu;

@Controller
@RequestMapping("/rodzaje")
public class RodzajPakietuController {

	private RodzajPakietuService service;
	
	public RodzajPakietuController(RodzajPakietuService service) {
		this.service = service;
	}
	
	@GetMapping
	public String addTypeBlank() {
		
		return "typeAdd";
	}
	
	@PostMapping(params = "addnew")
	public String addNew(Model model) {
		model.addAttribute("newType", new RodzajPakietuWriteModel());
		return "typeAdd";
	}
	
	@PostMapping("/dodaj")
	public String addType(
			@ModelAttribute("newType") @Valid RodzajPakietuWriteModel source,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redir) {
		if(bindingResult.hasErrors())
			return "typeAdd";
		else {
			service.saveNewType(source);
			redir.addFlashAttribute("msg", "Nowy pakiet treningów został dodany!");
			return "redirect:/rodzaje";
		}
		
	}
	
	@GetMapping("/usun/{pid}")
	public String removeType(@PathVariable Long pid, Model model) {
		
		String removedName = service.removeById(pid);
		if(!removedName.equals("")) {
			model.addAttribute("msg", "Pakiet \"" + removedName + "\" został usunięty");
			model.addAttribute("types", service.getAllActive());
		}
		
		return "typeAdd";
		
	}
	
	@ModelAttribute("types")
	List<RodzajPakietu> wszystkiePakiety() {
		return service.getAllActive();
	}
	
}
