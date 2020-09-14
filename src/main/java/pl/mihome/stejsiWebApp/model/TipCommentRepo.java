package pl.mihome.stejsiWebApp.model;

import java.util.Optional;

public interface TipCommentRepo {

	TipComment save(TipComment tipComment);
	
	Optional<TipComment> findById(Long id);
	

}
