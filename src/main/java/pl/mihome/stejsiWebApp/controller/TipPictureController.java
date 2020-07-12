package pl.mihome.stejsiWebApp.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.mihome.stejsiWebApp.logic.TipPictureService;


@RestController
@Secured("ROLE_STEJSI")
@RequestMapping("/tips/img")
public class TipPictureController {

	private TipPictureService pictureService;
	
	
	public TipPictureController(TipPictureService pictureService) {
		this.pictureService = pictureService;
	}


	@GetMapping("/{tid}")
	public ResponseEntity<byte[]> getImage(@PathVariable Long tid) {
		var media = pictureService.getTipPictureFromTipId(tid);
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(media.getImageType()));
		return new ResponseEntity<byte[]>(media.getThumb(), headers, HttpStatus.CREATED);
	}
}
