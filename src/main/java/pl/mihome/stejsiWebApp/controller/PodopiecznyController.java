package pl.mihome.stejsiWebApp.controller;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import pl.mihome.stejsiWebApp.DTO.PakietWriteModel;
import pl.mihome.stejsiWebApp.DTO.PodopiecznyWriteModel;
import pl.mihome.stejsiWebApp.constraints.StejsiNameValidator;
import pl.mihome.stejsiWebApp.logic.PakietTreningowService;
import pl.mihome.stejsiWebApp.logic.PodopiecznyService;
import pl.mihome.stejsiWebApp.logic.RodzajPakietuService;
import pl.mihome.stejsiWebApp.model.Podopieczny;

@Controller
@Secured("ROLE_STEJSI")
@RequestMapping("/podopieczni")
public class PodopiecznyController {
	
	private static final Logger logowanie = LoggerFactory.getLogger(Podopieczny.class);
	private final PodopiecznyService service;
	private final RodzajPakietuService rodzajService;
	private final PakietTreningowService pakietService;
		
	
	public PodopiecznyController(PodopiecznyService service, RodzajPakietuService rodzajService, PakietTreningowService pakietService) {
		this.service = service;
		this.rodzajService = rodzajService;
		this.pakietService = pakietService;
	}

	@GetMapping
	String showUsers(@ModelAttribute String addedUser,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "reverse", required = false) Boolean reverse,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "onlyactive", required = false) Boolean onlyactive,
			Model model) {
		
		model.addAttribute("msg", addedUser);
		logowanie.info("[showUsers] Showing all users sorted by " + sort + " z parametrem reverse: " + reverse);
		
		Set<String> sortowniki = Set.of("imie", "nazwisko", "aktywny");
		
		if(sort == null || !sortowniki.contains(sort))
			sort = "imie";
		if(reverse == null)
			reverse = false;
		if(page == null)
			page = 0;
		if(onlyactive == null)
			onlyactive = true;
		
		var usersPage = service.getAllPage(page, sort, reverse, onlyactive); 
		model.addAttribute("rev", reverse);
		model.addAttribute("sortParam", sort);	
		model.addAttribute("onlyActive", onlyactive);
		model.addAttribute("currentPage", usersPage.getNumber());
		model.addAttribute("totalPages", usersPage.getTotalPages());
		
//		var usersGenerated = usersPage.stream()
//				.collect(Collectors.toList());
		
		model.addAttribute("users", usersPage.getContent());
		
        if (usersPage.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, usersPage.getTotalPages())
                .boxed()
                .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
		
		return "users";
	}
	
	@GetMapping("/dodaj")
	String addUserBlank(Model model) {
		model.addAttribute("user", new PodopiecznyWriteModel());
		return "userAdd";
	}
	
	@InitBinder("user")
	protected void initBinder(WebDataBinder binder) {
		binder.addValidators(new StejsiNameValidator());
	}
	
	@PostMapping("/dodaj")
	String addUser(@ModelAttribute("user") @Valid PodopiecznyWriteModel source, 
			BindingResult bindingResult, 
			Model model, 
			RedirectAttributes redir) {
		
		if(bindingResult.hasErrors()) {
			logowanie.warn("[addUser] New user form validation failed");
			model.addAttribute("msg", "Formularz należy poprawić,");
			return "userAdd";
		}
		else {
			logowanie.info("[addUser] Adding new user");
			var addedUser = service.saveNewUser(source);
			logowanie.info("[addUser] New user added: " + source.getEmail());
			redir.addFlashAttribute("addedEmail", addedUser.getEmail());
			return "redirect:/podopieczni";
		}
	}
	
	@GetMapping("/{id}")
	String readOne(@PathVariable Long id, Model model) {
		var user = service.getUserById(id);
		model.addAttribute("loadedUser", user);		
		model.addAttribute("activePackages", user.getActivePackages());
		model.addAttribute("archivePackages", user.getInactivePackages());
		
		return "userDetail";
	}
	
	
	@PostMapping(value = "/{id}", params = "pid")
	String togglePayment(@RequestParam Long pid, @PathVariable Long id, Model model) {
		pakietService.togglePayment(pid);
		var user = service.getUserById(id);
		model.addAttribute("activePackages", user.getActivePackages());
		model.addAttribute("archivePackages", user.getInactivePackages());
		model.addAttribute("loadedUser", user);
		
		return "userDetail";
	}
	
	@PostMapping(value = "/{id}", params = "pid_toClose")
	String closePackage(@RequestParam(name = "pid_toClose") Long pid, @PathVariable Long id, Model model) {
		pakietService.closePackage(pid);
		var user = service.getUserById(id);
		model.addAttribute("activePackages", user.getActivePackages());
		model.addAttribute("archivePackages", user.getInactivePackages());
		model.addAttribute("loadedUser", user);
		
		return "userDetail";
	}
	
	@PostMapping(value = "/{id}", params = "pid_toMarkDone")
	String markAllDonePackage(@RequestParam(name = "pid_toMarkDone") Long pid, @PathVariable Long id, Model model) {
		pakietService.markAllPastDone(pid);
		var user = service.getUserById(id);
		model.addAttribute("activePackages", user.getActivePackages());
		model.addAttribute("archivePackages", user.getInactivePackages());
		model.addAttribute("loadedUser", user);
		
		return "userDetail";
	}
	
	@GetMapping("/{id}/newpackage")
	String newUsersPackage(@PathVariable Long id, Model model) {
		var user = service.getUserById(id);
		model.addAttribute("loadedUser", user);
		model.addAttribute("activePackages", user.getActivePackages());
		model.addAttribute("archivePackages", user.getInactivePackages());
		model.addAttribute("newpackage", new PakietWriteModel(id));
		model.addAttribute("rodzaje", rodzajService.getAllActive());
		return "userDetail";
	}
	
	@PostMapping("/{id}/newpackage")
	String createUsersPackage(@ModelAttribute PakietWriteModel newpackage, @PathVariable Long id, Model model) {
		
		if(newpackage.getRodzajPakietuId() <= 0)
		{
			var user = service.getUserById(id);
			model.addAttribute("loadedUser", user);
			model.addAttribute("activePackages", user.getActivePackages());
			model.addAttribute("archivePackages", user.getInactivePackages());
			model.addAttribute("newpackage", newpackage);
			model.addAttribute("rodzaje", rodzajService.getAllActive());
		}
		else
		{
			newpackage.setPodopiecznyId(id);
			var user = service.getUserById(id);
			user.getTrainingPackages().add(0, pakietService.createNew(newpackage));
			model.addAttribute("activePackages", user.getActivePackages());
			model.addAttribute("archivePackages", user.getInactivePackages());
			model.addAttribute("loadedUser", user);
			model.addAttribute("msg", "Nowy pakiet został otwarty");
		}
		return "userDetail";
	}

	
	

}
