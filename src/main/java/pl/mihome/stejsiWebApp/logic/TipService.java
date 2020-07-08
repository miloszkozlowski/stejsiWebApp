package pl.mihome.stejsiWebApp.logic;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import pl.mihome.stejsiWebApp.DTO.appComms.TipCommentReadModel;
import pl.mihome.stejsiWebApp.DTO.appComms.TipCommentWriteModel;
import pl.mihome.stejsiWebApp.DTO.appComms.TipReadModel;
import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.Tip;
import pl.mihome.stejsiWebApp.model.TipComment;
import pl.mihome.stejsiWebApp.model.TipCommentRepo;
import pl.mihome.stejsiWebApp.model.TipPicture;
import pl.mihome.stejsiWebApp.model.TipPictureRepo;
import pl.mihome.stejsiWebApp.model.TipRepo;
import pl.mihome.stejsiWebApp.model.TokenRepo;

@Service
public class TipService {

	TipRepo repo;
	TipCommentRepo commentRepo;
	TipPictureRepo pictureRepo;
	TokenRepo tokenRepo;
	
	private static final Logger log = LoggerFactory.getLogger(TipService.class);
	
	public TipService(TipRepo repo, TipCommentRepo commentRepo, TipPictureRepo pictureRepo, TokenRepo tokenRepo) {
		this.repo = repo;
		this.commentRepo = commentRepo;
		this.pictureRepo = pictureRepo;
		this.tokenRepo = tokenRepo;
	}
	
	public List<TipReadModel> getAllTips() {
		return repo.findByRemovedIsFalse().stream()
				.sorted(Comparator.comparing(Tip::getWhenCreated).reversed())
				.map(TipReadModel::new)
				.collect(Collectors.toList());
	}
	
	public TipPicture getTipPictureFromTipId(Long tid) throws NotFoundCustomException {
		var img = pictureRepo.findByTipId(tid);
		
		if(img.isPresent()) {
			return img.get();
		}
		else 
			throw new NotFoundCustomException();
	}
	
	public byte[] getTipPictureThumbinail(Long tid) throws NotFoundCustomException {
		var tipPicture = repo.findByIdAndRemovedIsFalse(tid);
		
		if(tipPicture.isPresent()) {
			return tipPicture.get().getPicture().getThumb();
			
		}
		else {
			throw new NotFoundCustomException();
		}
	}
	
	
	
	public void saveNewTip(Tip source) {
		repo.save(source);
		log.info("Zapisano nowy tip \"" + source.getHeading() + "\"");
	}
	
	@Transactional
	public String removeById(Long tid) {
		var tip = repo.findById(tid);
		if(tip.isPresent())
		{
			tip.get().setRemoved(true);
			//TODO dodać usuwanie zdjęcia i koemantarzy
			return tip.get().getHeading();
		}
		return "";
		
	}
	
	public Set<TipCommentReadModel> getCommentsByTip(Long tid) throws IllegalArgumentException {
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
			throw new IllegalArgumentException("Tip id missmatch");
		}
			
		
	}

	
	public void addNewComment(@Valid TipCommentWriteModel comment, String token) throws IllegalArgumentException {
		
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
			throw new IllegalArgumentException("Tip/user id missmatch");
		}
		
	}
	

	public void trainerToAddComment(TipCommentWriteModel comment) {
		var tip = repo.findByIdAndRemovedIsFalse(comment.getTipId());
		
		if(tip.isPresent()) {
			var newComment = new TipComment();
			newComment.setBody(comment.getBody());
			newComment.setTip(tip.get());
			commentRepo.save(newComment);
			log.info("Nowy komentarz od trenera zapisany");
		}
		else {
			throw new IllegalArgumentException("Tip/user id missmatch");
		}
		
	}

	@Transactional
	public void markAsSeen(Long tid, String token) {
		var tip = repo.findByIdAndRemovedIsFalse(tid);
		var tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
		
		if(tip.isPresent() && tokenFound.isPresent()) {
			if(!tip.get().getUsersRead().contains(tokenFound.get().getOwner())) {
				tokenFound.get().getOwner().getTipsRead().add(tip.get());
				
				log.info("Oznaczono jako przeczytany dla user id: " + tokenFound.get().getOwner().getId());
			}
		}
		else {
			throw new IllegalArgumentException("Tip/user id missmatch");
		}
		
	}

	@Transactional
	public void removeComment(Long cid) {
		var comment = commentRepo.findById(cid);
		comment.ifPresentOrElse(c -> c.setRemoved(true), () -> {throw new NotFoundCustomException();});	
	}
	
	@Transactional
	public void removeComment(Long cid, String token) {
		var tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
		var comment = commentRepo.findById(cid);
		
		if(tokenFound.isPresent() && comment.isPresent()) {
			if(comment.get().getAuthor().getId().equals(tokenFound.get().getOwner().getId())) {
				comment.get().setRemoved(true);
			}
		}
		else {
			throw new NotFoundCustomException();
		}
	}
	
	
}
