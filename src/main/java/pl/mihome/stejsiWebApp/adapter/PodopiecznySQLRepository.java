package pl.mihome.stejsiWebApp.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.PodopiecznyRepo;

@Repository
interface PodopiecznySQLRepository extends PodopiecznyRepo, JpaRepository<Podopieczny, Long> {

	@Override
	@Query("select distinct u from Podopieczny u left join fetch u.trainingPackages")
	List<Podopieczny> findAll();

	@Override
	@Query("from Podopieczny u left join fetch u.trainingPackages p left join fetch p.packageType left join fetch p.trainings where u.id = ?1")
	Optional<Podopieczny> findById(Long id);


}
