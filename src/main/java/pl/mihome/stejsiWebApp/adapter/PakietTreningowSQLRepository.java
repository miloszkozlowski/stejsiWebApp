package pl.mihome.stejsiWebApp.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.mihome.stejsiWebApp.model.PakietTreningow;
import pl.mihome.stejsiWebApp.model.PakietTreningowRepo;

@Repository
interface PakietTreningowSQLRepository extends JpaRepository<PakietTreningow, Long>, PakietTreningowRepo {
}
