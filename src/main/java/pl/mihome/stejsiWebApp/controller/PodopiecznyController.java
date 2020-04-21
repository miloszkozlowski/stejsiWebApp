package pl.mihome.stejsiWebApp.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.PodopiecznyRepo;

@RestController("/podopieczni")
public class PodopiecznyController {
	
	
	private final PodopiecznyRepo repo;
	
	
	
	public PodopiecznyController(PodopiecznyRepo repo) {
		this.repo = repo;
	}

	@PostMapping("/podopieczni")
	ResponseEntity<Podopieczny> saveOne(@RequestBody @Valid Podopieczny user)
	{
		Podopieczny dodany = repo.save(user);
		return ResponseEntity.created(URI.create("/"+dodany.getId())).body(dodany);
	}


	@GetMapping("/podopieczni")
	ResponseEntity<List<Podopieczny>> readAll()
	{
		return ResponseEntity.ok(repo.findAll());
	}

}
