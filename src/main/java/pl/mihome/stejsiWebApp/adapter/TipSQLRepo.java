package pl.mihome.stejsiWebApp.adapter;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pl.mihome.stejsiWebApp.model.Tip;
import pl.mihome.stejsiWebApp.model.TipRepo;

@Repository
interface TipSQLRepo extends TipRepo, JpaRepository<Tip, Long> {

	@Query("select distinct tip from Tip tip left join fetch tip.usersRead u left join fetch tip.comments c where tip.removed = false")
	List<Tip> findByRemovedIsFalse();
}
