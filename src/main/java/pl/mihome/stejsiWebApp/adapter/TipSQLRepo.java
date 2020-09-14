package pl.mihome.stejsiWebApp.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pl.mihome.stejsiWebApp.model.Tip;
import pl.mihome.stejsiWebApp.model.TipRepo;

@Repository
interface TipSQLRepo extends TipRepo, JpaRepository<Tip, Long> {

	@Query("select distinct tip from Tip tip left join fetch tip.usersRead u left join fetch tip.comments c where tip.removed = false and tip.id = :id")
	Optional<Tip> findByIdAndRemovedIsFalse(Long id);

	@Query("select distinct tip from Tip tip left join fetch tip.usersRead u left join fetch tip.comments c where tip.removed = false")
	List<Tip> findByRemovedIsFalse();

//	@Query(
//			value = "select distinct tip from Tip tip left join fetch tip.usersRead u left join fetch tip.comments c where tip.removed = false",
//			countQuery = "select count(tip) from Tip tip where tip.removed = false"
//	)
//	Page<Tip> findByRemovedIsFalse(Pageable page);
}
