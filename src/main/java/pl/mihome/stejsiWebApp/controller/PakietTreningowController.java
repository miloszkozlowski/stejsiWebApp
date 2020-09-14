package pl.mihome.stejsiWebApp.controller;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.*;

import pl.mihome.stejsiWebApp.model.PakietTreningow;
import pl.mihome.stejsiWebApp.model.PakietTreningowRepo;

@Secured("ROLE_STEJSI")
@RestController("/pakiety")
public class PakietTreningowController {

	private static final Logger loguj = LoggerFactory.getLogger(PakietTreningow.class);
	private final PakietTreningowRepo repo;
	
	public PakietTreningowController(PakietTreningowRepo repo) {
		this.repo = repo;
	}
	
	@PostMapping("/pakiety")
	ResponseEntity<PakietTreningow> dodajPakiet(@RequestBody @Valid PakietTreningow pakiet)
	{
		loguj.info("[dodajPakiet] Tworzenie nowego pakietu");
		PakietTreningow nowyPakiet = repo.save(pakiet);
		return ResponseEntity.created(URI.create("/" + nowyPakiet.getId())).body(nowyPakiet);
	}
	
	@GetMapping("/pakiety")
	ResponseEntity<List<PakietTreningow>> pokazWszystkiePakiety()
	{
		loguj.warn("[pokazWszystkiePakiety] Wyświetlanie wszystkich pakietów!");
		return ResponseEntity.ok(repo.findAll());
	}
	
}
