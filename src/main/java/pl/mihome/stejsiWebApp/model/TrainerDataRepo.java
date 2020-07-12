package pl.mihome.stejsiWebApp.model;

import java.util.List;

public interface TrainerDataRepo {
	
	List<TrainerData> findByRemovedIsFalse();
	
	TrainerData save(TrainerData source);

}
