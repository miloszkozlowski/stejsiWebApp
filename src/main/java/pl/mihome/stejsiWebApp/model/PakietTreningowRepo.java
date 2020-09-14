package pl.mihome.stejsiWebApp.model;

import java.util.List;
import java.util.Optional;

public interface PakietTreningowRepo {

	List<PakietTreningow> findAll();
	
	Optional<PakietTreningow> findById(Long id);
	
	List<PakietTreningow> findByOwner(Long id);
	
	PakietTreningow save(PakietTreningow source);

	List<PakietTreningow> findByClosedIsFalse();
}
