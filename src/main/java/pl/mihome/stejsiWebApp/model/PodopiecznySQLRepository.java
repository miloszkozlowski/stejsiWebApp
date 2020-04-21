package pl.mihome.stejsiWebApp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
interface PodopiecznySQLRepository extends PodopiecznyRepo, JpaRepository<Podopieczny, Long> {

}
