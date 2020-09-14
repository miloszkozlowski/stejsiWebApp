package pl.mihome.stejsiWebApp.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pl.mihome.stejsiWebApp.model.Lokalizacja;
import pl.mihome.stejsiWebApp.model.LokalizacjaRepo;

@Repository
interface LokalizacjaSQLRepo extends JpaRepository<Lokalizacja, Long>, LokalizacjaRepo {

	@Modifying
	@Query("update Lokalizacja l set l.defaultLocation = case l.id when ?1 then true else false end")
	void setOneDefault(Long id);
}
