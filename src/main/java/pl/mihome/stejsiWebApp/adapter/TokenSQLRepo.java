package pl.mihome.stejsiWebApp.adapter;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pl.mihome.stejsiWebApp.model.Token;
import pl.mihome.stejsiWebApp.model.TokenRepo;

@Repository
interface TokenSQLRepo extends JpaRepository<Token, Long>, TokenRepo {

	@Modifying
	@Query("delete from Token t where t.active = false and t.whenCreated < ?1")
	void removeOutOfDate(LocalDateTime olderThan);
	
}
