package pl.mihome.stejsiWebApp.logic;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.TipPicture;
import pl.mihome.stejsiWebApp.model.TipPictureRepo;

@Service
public class TipPictureService {

	TipPictureRepo pictureRepo;
	
	private static final Logger log = LoggerFactory.getLogger(TipPictureService.class);

	public TipPictureService(TipPictureRepo pictureRepo) {
		this.pictureRepo = pictureRepo;
	}
	
	public TipPicture getTipPictureFromTipId(Long tid) throws NotFoundCustomException {
		log.info("Ładowanie obiekty obrazka dla tip id: " + tid);
		var img = pictureRepo.findByTipId(tid);
		
		if(img.isPresent()) {
			return img.get();
		}
		log.warn("Nie załadowano obiektu obrazka, bo nie ma takiego obrazka dla tip id: " + tid);
		throw new NotFoundCustomException();
	}
}
