package pl.mihome.stejsiWebApp.adapter;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.mihome.stejsiWebApp.model.TrainerData;
import pl.mihome.stejsiWebApp.model.TrainerDataRepo;

@Repository
interface TrainerDataSQLRepo extends JpaRepository<TrainerData, Long>, TrainerDataRepo {
	
}
