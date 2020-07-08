package pl.mihome.stejsiWebApp.model;

import java.util.Optional;

public interface TipCommentRepo {

	void save(TipComment tipComment);
	
	Optional<TipComment> findById(Long id);
	

}
