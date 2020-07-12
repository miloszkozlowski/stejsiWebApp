package pl.mihome.stejsiWebApp.model;

import java.util.Optional;


public interface TipPictureRepo {
	
	Optional<TipPicture> findByTipId(Long tipid);

	void deleteByTipId(Long tipid);

}
