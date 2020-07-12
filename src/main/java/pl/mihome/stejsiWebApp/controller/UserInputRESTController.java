package pl.mihome.stejsiWebApp.controller;


import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.SendFailedException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.mihome.stejsiWebApp.DTO.PodopiecznyStatsReadModel;
import pl.mihome.stejsiWebApp.DTO.appComms.EmailRegistrationDTO;
import pl.mihome.stejsiWebApp.DTO.appComms.RegistrationStatus;
import pl.mihome.stejsiWebApp.DTO.appComms.TipCommentReadModel;
import pl.mihome.stejsiWebApp.DTO.appComms.TipCommentWriteModel;
import pl.mihome.stejsiWebApp.DTO.appComms.TipReadModel;
import pl.mihome.stejsiWebApp.config.AndroidAuthorization;
import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.logic.AppClientService;
import pl.mihome.stejsiWebApp.logic.PodopiecznyService;
import pl.mihome.stejsiWebApp.logic.TipPictureService;
import pl.mihome.stejsiWebApp.logic.TipService;
import pl.mihome.stejsiWebApp.logic.TreningService;
import pl.mihome.stejsiWebApp.model.appClient.RegistrationAttemp;

@RestController
@RequestMapping("/userinput")
public class UserInputRESTController {
	
	private TreningService treningService;
	private PodopiecznyService podopiecznyService;
	private AppClientService appClientService;
	private TipService tipService;
	private TipPictureService pictureService;
	
	private static final Logger log = LoggerFactory.getLogger(UserInputRESTController.class);

	
	public UserInputRESTController(TreningService treningService, PodopiecznyService podopiecznyService, AppClientService appClientService, TipService tipService, TipPictureService pictureService) {
		this.treningService = treningService;
		this.podopiecznyService = podopiecznyService;
		this.appClientService = appClientService;
		this.tipService = tipService;
		this.pictureService = pictureService;
	}
	
	@PatchMapping("/setting/{setting}/{status}")
	@AndroidAuthorization
	public ResponseEntity<?> changeUserSetting(@PathVariable String setting, @PathVariable boolean status, @RequestHeader String token) {
		log.info("Zgłoszenie zmiany ustawienia: " + setting);
		podopiecznyService.toggleSetting(setting, status, token);
		return ResponseEntity.noContent().build();
	}

	
	@PatchMapping("/present/{tid}")
	@AndroidAuthorization
	public ResponseEntity<?> confirmPresenceByUser(@PathVariable Long tid, @RequestHeader String token) {
		log.info("Zgłoszono obecność na treningu numer " + tid);
		treningService.confirmPresenceByUser(tid, token);
		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/cancel/{tid}")
	@AndroidAuthorization
	public ResponseEntity<?> cancelTrainingByUser(@PathVariable Long tid, @RequestHeader String token) {
		log.info("Uzytkownik odwołał trening numer " + tid);
		treningService.cancelByUser(tid, token);
		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/scheduleconfirmation/{tid}")
	@AndroidAuthorization
	public ResponseEntity<?> confirmSchedule(@PathVariable Long tid, @RequestHeader String token) {
		log.info("Potwierdzono termin treningu numer " + tid);
		treningService.confirmScheduleByUser(tid, token);
		return ResponseEntity.noContent().build();
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<JSONObject> registerEmail(@RequestBody @Valid EmailRegistrationDTO registrationData, HttpServletRequest req) throws SendFailedException {
		log.info("Request received: email: " + registrationData.getEmailAddress() + ", token: " + registrationData.getToken() + ", device ID: " + registrationData.getAndroidDeviceId());
		
		var attemp = new RegistrationAttemp(LocalDateTime.now(), req.getRemoteAddr(), registrationData.getAndroidDeviceId(), registrationData.getEmailAddress());
		RegistrationStatus status;
		
		if(appClientService.registerAttempAllowed(attemp)) {
			status = podopiecznyService.registerDeviceStatus(registrationData);
		}
		else {
			status = RegistrationStatus.ALLOWED_REGISTRATION_ATTEMPS_EXCEEDED;
		}
		
		log.info("Status rejestracji: " + status + " dla " + req.getRemoteAddr());
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", status);
		return ResponseEntity.ok(jsonObject);
		
	}
	
	@GetMapping("/authorized")
	public ResponseEntity<?> authorizationStatus(@RequestHeader String token, @RequestHeader String deviceId) {
		
		boolean isAuthorized = appClientService.isAuthorized(token, deviceId);
		
		if(isAuthorized) {
			var user = appClientService.getUser(token);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("email", user.getEmail());
			log.info("Autoryzacja udzielona dla " + user.getEmail());
			return ResponseEntity.ok(jsonObject);
		}
		return ResponseEntity.badRequest().build();
	}
	
	@GetMapping("/userdata")
	@AndroidAuthorization
	public ResponseEntity<JSONObject> getUserData(@RequestHeader String token) {
		
		log.info("Zapytano o dane poprzez token");
		var user = appClientService.getUser(token);
		var tips = appClientService.getTips(token);
		var stats = new PodopiecznyStatsReadModel(user);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("user", user);
		jsonObject.put("tips", tips);
		jsonObject.put("stats", stats);
		log.info("Poprawnie wydano dane dla tokenu dla " + user.getEmail());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		return ResponseEntity.ok().header("current_time", sdf.format(new Date())).body(jsonObject);
	}
	
	@GetMapping("/tips")
	@AndroidAuthorization
	public ResponseEntity<List<TipReadModel>> getTips(@RequestHeader String token) {
		var tips = appClientService.getTips(token);
		return ResponseEntity.ok(tips);
	}
	
	@GetMapping("/tipimage/{tid}")
	@AndroidAuthorization
	public ResponseEntity<byte[]> getTipPicture(@PathVariable Long tid) {
		var media = pictureService.getTipPictureFromTipId(tid);
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(media.getImageType()));
		return new ResponseEntity<byte[]>(media.getSource(), headers, HttpStatus.CREATED);
	}
	
	@GetMapping("/tipthumbimage/{tid}")
	@AndroidAuthorization
	public ResponseEntity<byte[]> getTipPictureThumb(@PathVariable Long tid) {
		var media = pictureService.getTipPictureFromTipId(tid);
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(media.getImageType()));
		return new ResponseEntity<byte[]>(media.getThumb(), headers, HttpStatus.CREATED);
	}
	
	@PostMapping("/newcomment")
	@AndroidAuthorization
	public ResponseEntity<?> addComment(@RequestBody @Valid TipCommentWriteModel comment, @RequestHeader String token) {
		
		try {
			tipService.addNewComment(comment, token);
			var uri = new URI("/comments/" + comment.getTipId());
			log.info("Komenatarz dodany: \"" + comment.getBody() + "\" do tip: " + comment.getTipId());
			return ResponseEntity.created(uri).build();
		}
		catch(URISyntaxException ex) {
			ex.printStackTrace();
		}
		return ResponseEntity.unprocessableEntity().build();
	}
	
	@DeleteMapping("/removecomment/{cid}")
	@AndroidAuthorization
	public ResponseEntity<?> removeComment(@PathVariable Long cid, @RequestHeader String token) {
		tipService.removeComment(cid, token);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/comments/{tid}")
	@AndroidAuthorization
	public ResponseEntity<List<TipCommentReadModel>> getCommentsByTip(@PathVariable Long tid) {
		return ResponseEntity.ok(new ArrayList<>(tipService.getCommentsByTip(tid)));
	}
	
	@PatchMapping("/reporttipseen/{tid}")
	@AndroidAuthorization
	public ResponseEntity<?> markTipAsSeenByUser(@PathVariable Long tid, @RequestHeader String token) {
		tipService.markAsSeen(tid, token);
		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/newfcmtoken")
	@AndroidAuthorization
	public ResponseEntity<?> registerNewFCMToken(@RequestBody String tokenFCM, @RequestHeader String token) {
		Gson gson = new Gson();
		String fcm = gson.fromJson(tokenFCM, JSONObject.class).getAsString("tokenFCM");
		appClientService.registerFCMToken(token, fcm);
		log.info("Token FCM (" + fcm + ") użytkownika poprawnie zarejestrowany");
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/logout")
	@AndroidAuthorization
	public ResponseEntity<?> logout(@RequestHeader String token) {
		appClientService.logout(token);
		return ResponseEntity.noContent().build();
	}
	
	
	@ExceptionHandler(NotFoundCustomException.class)
	public ResponseEntity<?> notFoundError() {
		return ResponseEntity.notFound().build();
	}
	
	@ExceptionHandler(SendFailedException.class)
	public ResponseEntity<?> emailError() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", RegistrationStatus.UNKNOWN_ERROR);
		log.warn("Problem z mailem");
		return ResponseEntity.ok(jsonObject);
	}

}
