package pl.mihome.stejsiWebApp.controller.controllerRest;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import pl.mihome.stejsiWebApp.DTO.TipWriteModel;
import pl.mihome.stejsiWebApp.DTO.appComms.TipCommentReadModel;
import pl.mihome.stejsiWebApp.DTO.appComms.TipCommentWriteModel;
import pl.mihome.stejsiWebApp.DTO.appComms.TipReadModel;
import pl.mihome.stejsiWebApp.config.AngularConfiguration;
import pl.mihome.stejsiWebApp.logic.TipPictureService;
import pl.mihome.stejsiWebApp.logic.TipService;
import pl.mihome.stejsiWebApp.model.Tip;

@RestController
@CrossOrigin({"http://192.168.0.96:4200", "http://localhost:4200", "http://192.168.1.51:4200"})
@RequestMapping("/ngaccess/tips")
@Secured("ROLE_STEJSI")
public class TipControllerRest {
	
	private final TipService service;
	private final TipPictureService pictureService;
	private final AngularConfiguration angularConf;

	public TipControllerRest(TipService service, TipPictureService pictureService, AngularConfiguration angularConf) {
		this.service = service;
		this.pictureService = pictureService;
		this.angularConf = angularConf;
	}

	@GetMapping
	ResponseEntity<Page<TipReadModel>> loadTipsPage(Pageable page) {
		var tips = service.getTipsPage(page);
		return ResponseEntity.ok(tips);
	}
	
	@GetMapping("/{id}")
	ResponseEntity<TipReadModel> getTipById(@PathVariable Long id) {
		var tip = new TipReadModel(service.getTipById(id));
		return ResponseEntity.ok(tip);
	}

	@DeleteMapping("/{id}")
	ResponseEntity<?> removeTipId(@PathVariable Long id) {
		service.removeById(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/add")
	ResponseEntity<Tip> addNewTip(@RequestBody TipWriteModel tipSource) {
		Tip tipToSave = tipSource.mapToTip();
		try {
			if(!tipSource.getImageUrl().isBlank()) {
				var pic = pictureService.processImageUrl(tipSource.getImageUrl());
				tipToSave.setPicture(pic);
				tipToSave.setLocalImagePresent(false);
			}
			else if(tipSource.getUploadedImage() != null) {
				var parts = tipSource.getUploadedImage().split(",");
				byte[] rawImage = Base64.getMimeDecoder().decode(parts[1]);
				var pic = pictureService.processImageBytes(rawImage);
				tipToSave.setPicture(pic);
				tipToSave.setLocalImagePresent(true);
			}
		}
		catch(IOException ex) {
			throw new IllegalArgumentException("Image processing failed at controller level");
		}
		var tip = service.saveNewTip(tipToSave);
		return ResponseEntity.created(URI.create(angularConf.getWebServerUrl() + "/tips/" + tip.getId())).body(tip);
	}

	@GetMapping("/thumb/{tid}")
	public ResponseEntity<byte[]> getImageThumbnail(@PathVariable Long tid) {
		var media = pictureService.getTipPictureFromTipId(tid);
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(media.getImageType()));
		return new ResponseEntity<>(media.getThumb(), headers, HttpStatus.OK);
	}

	@GetMapping("/pic/{tid}")
	public ResponseEntity<byte[]> getImage(@PathVariable Long tid) {
		var media = pictureService.getTipPictureFromTipId(tid);
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(media.getImageType()));
		return new ResponseEntity<>(media.getSource(), headers, HttpStatus.OK);
	}

	@PostMapping("/comments")
	public ResponseEntity<TipCommentReadModel> addComment(@RequestBody TipCommentWriteModel newComment) {
		var addedComment = service.trainerToAddComment(newComment);
		return ResponseEntity.created(URI.create(angularConf.getWebServerUrl() + "/tips" + newComment.getTipId())).body(addedComment);
	}

	@GetMapping("/notifypossible")
	public ResponseEntity<Boolean> isNotificationAvailable() {
		return ResponseEntity.ok(service.areThereTipsWithUesrsNotNotified());
	}

	@GetMapping("/notifyusers")
	public ResponseEntity<?> sendNotificationOnNewPosts() {
		service.notifyUsersOnNewTips();
		return ResponseEntity.ok().build();
	}

}
