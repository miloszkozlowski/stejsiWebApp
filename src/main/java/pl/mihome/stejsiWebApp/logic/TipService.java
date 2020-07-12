package pl.mihome.stejsiWebApp.logic;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import pl.mihome.stejsiWebApp.DTO.appComms.TipCommentReadModel;
import pl.mihome.stejsiWebApp.DTO.appComms.TipCommentWriteModel;
import pl.mihome.stejsiWebApp.DTO.appComms.TipReadModel;
import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.Tip;
import pl.mihome.stejsiWebApp.model.TipComment;
import pl.mihome.stejsiWebApp.model.TipCommentRepo;
import pl.mihome.stejsiWebApp.model.TipPictureRepo;
import pl.mihome.stejsiWebApp.model.TipRepo;
import pl.mihome.stejsiWebApp.model.TokenRepo;

@Service
public class TipService {

	TipRepo repo;
	TipCommentRepo commentRepo;
	TipPictureRepo pictureRepo;
	TokenRepo tokenRepo;
	
	AndroidNotificationsService notificationService;
	
	private static final Logger log = LoggerFactory.getLogger(TipService.class);
	
	public TipService(TipRepo repo, TipCommentRepo commentRepo, TipPictureRepo pictureRepo, TokenRepo tokenRepo, AndroidNotificationsService notificationService) {
		this.repo = repo;
		this.commentRepo = commentRepo;
		this.pictureRepo = pictureRepo;
		this.tokenRepo = tokenRepo;
		this.notificationService = notificationService;
	}
	
	public List<TipReadModel> getAllTips() {
		log.info("Zaczytywanie wszytkich tipów");
		return repo.findByRemovedIsFalse().stream()
				.sorted(Comparator.comparing(Tip::getWhenCreated).reversed())
				.map(TipReadModel::new)
				.collect(Collectors.toList());
	}
	
	
	public byte[] getTipPictureThumbinail(Long tid) throws NotFoundCustomException {
		log.info("Pobieranie miniaturki dla tip id: " + tid);
		var tipPicture = repo.findByIdAndRemovedIsFalse(tid);
		
		if(tipPicture.isPresent()) {
			return tipPicture.get().getPicture().getThumb();
			
		}
		else {
			log.warn("Nie odnaleziono obiektu tip do pobrania miniaturki");
			throw new NotFoundCustomException();
		}
	}
	
	
	
	public void saveNewTip(Tip source) {
		repo.save(source);
		log.info("Zapisano nowy tip \"" + source.getHeading() + "\"");
	}
	
	@Transactional
	public String removeById(Long tid) {
		log.info("Usuwanie tipu id: " + tid);
		var tip = repo.findById(tid);
		if(tip.isPresent())
		{
			tip.get().setRemoved(true);
			if(tip.get().isLocalImagePresent()) {
				var pic = pictureRepo.findByTipId(tip.get().getId());
				pic.ifPresentOrElse(p -> p.setPicture(null), () -> log.warn("Próba usunięcia zdjęcia wraz z tipem nie powiodła się - nie takeigo obiektu"));
				pic.ifPresent(p -> p.setRemoved(true));
			}
			log.info("Tip \"" + tip.get().getHeading() + "\" został usunięty");
			return tip.get().getHeading();
		}
		return "";
		
	}
	
	public Set<TipCommentReadModel> getCommentsByTip(Long tid) throws NotFoundCustomException {
		log.info("Pobieranie komentarzy do tip id: " + tid);
		Set<TipCommentReadModel> comments;
		
		var tip = repo.findByIdAndRemovedIsFalse(tid);
		
		if(tip.isPresent()) {
			comments = tip.get().getComments().stream()
					.filter(c -> !c.isRemoved())
					.map(TipCommentReadModel::new)
					.collect(Collectors.toSet());
			return comments;
		}
		
		else {
			log.warn("Nie odnaleziono obiektu tip do pobrania komentarzy");
			throw new NotFoundCustomException();
		}
			
		
	}

	
	public void addNewComment(@Valid TipCommentWriteModel comment, String token) throws IllegalArgumentException {
		log.info("Dodawanie komentarza do tip id: " + comment.getTipId());
		var tip = repo.findByIdAndRemovedIsFalse(comment.getTipId());
		var tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
		
		if(tip.isPresent() && tokenFound.isPresent()) {
			var newComment = new TipComment();
			newComment.setAuthor(tokenFound.get().getOwner());
			newComment.setBody(comment.getBody());
			newComment.setTip(tip.get());
			commentRepo.save(newComment);
			log.info("Nowy komentarz zapisany");
		}
		else {
			log.warn("Nie można było dodać komentarza, bo nie odnaleziono obiektu tip lub niezidentyfikowano użytkownika po tokenie");
			throw new IllegalArgumentException("Tip/user id missmatch");
		}
		
	}
	

	public void trainerToAddComment(TipCommentWriteModel comment) throws NotFoundCustomException {
		log.info("Trener dodaje komentarz z pociomu aplikacji webowej");
		var tip = repo.findByIdAndRemovedIsFalse(comment.getTipId());
		
		if(tip.isPresent()) {
			var newComment = new TipComment();
			newComment.setBody(comment.getBody());
			newComment.setTip(tip.get());
			commentRepo.save(newComment);
			log.info("Nowy komentarz od trenera zapisany");
		}
		else {
			log.warn("Nie można było dodać komentarza trenera, bo nie ma takiego obiektu tip");
			throw new NotFoundCustomException();
		}
		
	}

	@Transactional
	public void markAsSeen(Long tid, String token) throws IllegalArgumentException {
		log.info("Oznaczanie jako przeczytany tip id: " + tid);
		var tip = repo.findByIdAndRemovedIsFalse(tid);
		var tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
		
		if(tip.isPresent() && tokenFound.isPresent()) {
			if(!tip.get().getUsersRead().contains(tokenFound.get().getOwner())) {
				tokenFound.get().getOwner().getTipsRead().add(tip.get());
				log.info("Oznaczono jako przeczytany dla user id: " + tokenFound.get().getOwner().getId());
			}
		}
		else {
			log.warn("Nie można było oznaczyć jako przeczytany tip, bo nie odnaleziono obiektu tip lub niezidentyfikowano użytkownika po tokenie");
			throw new IllegalArgumentException("Tip/user id missmatch");
		}
		
	}

	@Transactional
	public void removeComment(Long cid) throws NotFoundCustomException {
		log.info("Usuwanie komentarza id: " + cid);
		var comment = commentRepo.findById(cid);
		comment.ifPresentOrElse(c -> c.setRemoved(true), () -> {
			log.warn("Nie można było usunąć komentarza, bo nie odnaleziono obiektu komentarza");
			throw new NotFoundCustomException();
			});	
	}
	
	@Transactional
	public void removeComment(Long cid, String token) throws IllegalArgumentException {
		log.info("Usuwanie komentarza wraz z weryfikacją właściciela, dla komentarza id: " + cid);
		var tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
		var comment = commentRepo.findById(cid);
		
		if(tokenFound.isPresent() && comment.isPresent()) {
			if(comment.get().getAuthor().getId().equals(tokenFound.get().getOwner().getId())) {
				comment.get().setRemoved(true);
			}
		}
		else {
			log.warn("Nie powiodło się usuwanie komentarza wraz z weryfikacją właściciela, bo nie odnaleziono komentarza lub właściciela");
			throw new IllegalArgumentException("Comment/user mismatch");
		}
	}

	@Transactional
	@Async
	public void notifyUsersOnNewTips() {
		log.info("Powiadamianie na żądanie użytkowników o nowym tip (async)");
		var tokens = tokenRepo.findByActiveIsTrueAndTokenFCMIsNotNull();
		var newTips = repo.findByRemovedIsFalseAndUsersNotifiedIsNull();
		
		Map<Podopieczny, String> users = new HashMap<>();
		
		tokens.stream()
		.filter(t -> t.getOwner().isSettingTipNotifications())
		.forEach(t -> users.put(t.getOwner(), t.getTokenFCM()));
		
		newTips.stream()
		.forEach(t -> {
			t.setUsersNotified(LocalDateTime.now());
			users.keySet().stream()
			.filter(p -> !t.getUsersRead().contains(p))
			.forEach(p -> notificationService.notifyUserAboutNewTip(t, users.get(p), p.getImie()));
		});
		
		
	}
	
	
}
