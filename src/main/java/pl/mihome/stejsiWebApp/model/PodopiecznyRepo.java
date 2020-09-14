package pl.mihome.stejsiWebApp.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PodopiecznyRepo {
	
	List<Podopieczny> findAll();
	
	Page<Podopieczny> findAll(Pageable page);
	
	Page<Podopieczny> findByAktywnyIsTrue(Pageable page);

	Slice<Podopieczny> findByRemovedIsFalse(Pageable page);

	Page<Podopieczny> findAllByImieContainingOrNazwiskoContaining(String imie, String nazwisko, Pageable page);
	
	Optional<Podopieczny> findById(Long id);
	
	Optional<Podopieczny> findByEmail(String email);
	
	Podopieczny save(Podopieczny user);
	
	boolean existsById(Long id);
	
	boolean existsByEmail(String email);
	
	boolean existsByPhoneNumber(int phoneNumber);
}
