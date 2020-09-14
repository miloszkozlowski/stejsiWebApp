package pl.mihome.stejsiWebApp.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.mihome.stejsiWebApp.DTO.appComms.TipCommentReadModel;
import pl.mihome.stejsiWebApp.DTO.appComms.TipCommentWriteModel;
import pl.mihome.stejsiWebApp.DTO.appComms.TipReadModel;
import pl.mihome.stejsiWebApp.config.AndroidAppConfiguration;
import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TipService {

    private static final Logger log = LoggerFactory.getLogger(TipService.class);
    private final TipRepo repo;
    private final TipCommentRepo commentRepo;
    private final TipPictureRepo pictureRepo;
    private final TokenRepo tokenRepo;
    private final EmailSenderService emailService;
    private final AndroidNotificationsService notificationService;
    private final TrainerDataService trainerDataService;
    private final AndroidAppConfiguration androidAppConfiguration;

    public TipService(TipRepo repo, TipCommentRepo commentRepo, TipPictureRepo pictureRepo, TokenRepo tokenRepo,
                      EmailSenderService emailService, AndroidNotificationsService notificationService,
                      TrainerDataService trainerDataService, AndroidAppConfiguration androidAppConfiguration) {
        this.repo = repo;
        this.commentRepo = commentRepo;
        this.pictureRepo = pictureRepo;
        this.tokenRepo = tokenRepo;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.trainerDataService = trainerDataService;
        this.androidAppConfiguration = androidAppConfiguration;
    }

    public Tip getTipById(Long id) {
        log.info("Pobieranie pojedynczego obiektu tip, id: " + id);
        return repo.findByIdAndRemovedIsFalse(id).orElseThrow(() -> {
            throw new NotFoundCustomException("TIP_NOT_FOUND", "Could not find tip by delivered id: " + id);
        });
    }

    public Page<TipReadModel> getTipsPage(Pageable page) {
        log.info("Rozpoczynanie pobierania tipów w formacie strony, nr: " + page.getPageNumber());
        var entityPage = repo.findByRemovedIsFalse(page);
        return entityPage.map(TipReadModel::new);
    }


    public List<TipReadModel> getAllTips() {
        log.info("Zaczytywanie wszytkich tipów");
        return repo.findByRemovedIsFalse().stream()
                .sorted(Comparator.comparing(Tip::getWhenCreated).reversed())
                .map(TipReadModel::new)
                .collect(Collectors.toList());
    }


//	public byte[] getTipPictureThumbinail(Long tid) throws NotFoundCustomException {
//		log.info("Pobieranie miniaturki dla tip id: " + tid);
//		var tipPicture = repo.findByIdAndRemovedIsFalse(tid);
//
//		if(tipPicture.isPresent()) {
//			return tipPicture.get().getPicture().getThumb();
//
//		}
//		else {
//			log.warn("Nie odnaleziono obiektu tip do pobrania miniaturki");
//			throw new NotFoundCustomException();
//		}
//	}


    public Tip saveNewTip(Tip source) {
        var tip = repo.save(source);
        log.info("Zapisano nowy tip \"" + source.getHeading() + "\"");
        return tip;
    }


    @Transactional
    public String removeById(Long tid) {
        log.info("Usuwanie tipu id: " + tid);
        var tip = repo.findById(tid);
        if (tip.isPresent()) {
            tip.get().setRemoved(true);
            if (tip.get().isLocalImagePresent()) {
                var pic = pictureRepo.findByTipId(tip.get().getId());
                pic.ifPresentOrElse(p -> p.setPicture(null), () -> log.warn("Próba usunięcia zdjęcia wraz z tipem nie powiodła się - nie takeigo obiektu"));
                pic.ifPresent(p -> p.setRemoved(true));
            }
            log.info("Tip \"" + tip.get().getHeading() + "\" został usunięty");
            return tip.get().getHeading();
        } else {
            throw new NotFoundCustomException("TIP_NOT_FOUND", "Could not delete tip with delivered id: " + tid + ". Tip not found.");
        }
    }

    public Set<TipCommentReadModel> getCommentsByTip(Long tid) {
        log.info("Pobieranie komentarzy do tip id: " + tid);
        Set<TipCommentReadModel> comments;

        var tip = repo.findByIdAndRemovedIsFalse(tid);

        if (tip.isPresent()) {
            comments = tip.get().getComments().stream()
                    .filter(c -> !c.isRemoved())
                    .map(TipCommentReadModel::new)
                    .collect(Collectors.toSet());
            return comments;
        } else {
            log.warn("Nie odnaleziono obiektu tip do pobrania komentarzy");
            throw new NotFoundCustomException("TIP_NOT_FOUND", "Could not load comment of tip id: " + tid + ". Tip not found.");
        }


    }


    public void addNewComment(@Valid TipCommentWriteModel comment, String token) {
        log.info("Dodawanie komentarza do tip id: " + comment.getTipId());
        var tip = repo.findByIdAndRemovedIsFalse(comment.getTipId());
        var tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);

        if (tip.isPresent() && tokenFound.isPresent()) {
            var newComment = new TipComment();
            newComment.setAuthor(tokenFound.get().getOwner());
            newComment.setBody(comment.getBody());
            newComment.setTip(tip.get());
            commentRepo.save(newComment);

            String subject = tokenFound.get().getOwner().getImie() + " " + tokenFound.get().getOwner().getNazwisko() + " dodał(a) komentarz do \"" + tip.get().getHeading() + "\"";
            String text = "Trenerze! \n"
                    + tokenFound.get().getOwner().getImie() + " dodał(a) nowy komentarz do aktualności \"" + tip.get().getHeading() + "\":\n\n"
                    + "\"" + comment.getBody() + "\"\n\n"
                    + "Możesz teraz dodać swój komentarz do tego artykułu. Możesz też usunąć komentarz podopiecznego. Wszystko tutaj: " + androidAppConfiguration.getWebServerUrl() + "/tips?comments=" + tip.get().getId() + "#comments"
                    + "\n\nPozdrawiam\nAutomat";
            if (trainerDataService.getCurrentData().getEmail() != null) {
                emailService.sendSimpleMessage(trainerDataService.getCurrentData().getEmail(), subject, text);
                log.info("E-mail z informacją o nowym komentarzu wysłany do trenera");
            }

            log.info("Nowy komentarz zapisany");
        } else {
            log.warn("Nie można było dodać komentarza, bo nie odnaleziono obiektu tip lub niezidentyfikowano użytkownika po tokenie");
            throw new IllegalArgumentException("COMMENT_ERR");
        }

    }


    public TipCommentReadModel trainerToAddComment(TipCommentWriteModel comment) {
        log.info("Trener dodaje komentarz z pociomu aplikacji webowej");
        var tip = repo.findByIdAndRemovedIsFalse(comment.getTipId());

        if (tip.isPresent()) {
            var newComment = new TipComment();
            newComment.setBody(comment.getBody());
            newComment.setTip(tip.get());
            var addedComment = commentRepo.save(newComment);
            log.info("Nowy komentarz od trenera zapisany");
            return new TipCommentReadModel(addedComment);
        } else {
            log.warn("Nie można było dodać komentarza trenera, bo nie ma takiego obiektu tip");
            throw new NotFoundCustomException("TIP_NOT_FOUND", "Could not add trainer comment. Tip not found by id: " + comment.getTipId());
        }

    }

    @Transactional
    public void markAsSeen(Long tid, String token) {
        log.info("Oznaczanie jako przeczytany tip id: " + tid);
        var tip = repo.findByIdAndRemovedIsFalse(tid);
        var tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);

        if (tip.isPresent() && tokenFound.isPresent()) {
            if (!tip.get().getUsersRead().contains(tokenFound.get().getOwner())) {
                tokenFound.get().getOwner().getTipsRead().add(tip.get());
                log.info("Oznaczono jako przeczytany dla user id: " + tokenFound.get().getOwner().getId());
            }
        } else {
            log.warn("Nie można było oznaczyć jako przeczytany tip, bo nie odnaleziono obiektu tip lub niezidentyfikowano użytkownika po tokenie");
            throw new IllegalArgumentException("MARK_AS_SEEN_ERR");
        }

    }

    @Transactional
    public void removeComment(Long cid) {
        log.info("Usuwanie komentarza id: " + cid);
        var comment = commentRepo.findById(cid);
        comment.ifPresentOrElse(c -> c.setRemoved(true), () -> {
            log.warn("Nie można było usunąć komentarza, bo nie odnaleziono obiektu komentarza");
            throw new NotFoundCustomException("COMMENT_NOT_FOUND", "Could not delete comment. Comment not found by id: " + cid);
        });
    }

    @Transactional
    public void removeComment(Long cid, String token) {
        log.info("Usuwanie komentarza wraz z weryfikacją właściciela, dla komentarza id: " + cid);
        var tokenFound = tokenRepo.findByTokenStringAndActiveIsTrueAndRemovedIsFalse(token);
        var comment = commentRepo.findById(cid);

        if (tokenFound.isPresent() && comment.isPresent()) {
            if (comment.get().getAuthor().getId().equals(tokenFound.get().getOwner().getId())) {
                comment.get().setRemoved(true);
            }
        } else {
            log.warn("Nie powiodło się usuwanie komentarza wraz z weryfikacją właściciela, bo nie odnaleziono komentarza lub właściciela");
            throw new IllegalArgumentException("REMOVE_COMMENT_ERR");
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

        newTips
                .forEach(t -> {
                    t.setUsersNotified(LocalDateTime.now());
                    users.keySet().stream()
                            .filter(p -> !t.getUsersRead().contains(p))
                            .forEach(p -> notificationService.notifyUserAboutNewTip(t, users.get(p), p.getImie()));
                });


    }

    public boolean areThereTipsWithUesrsNotNotified() {
        log.info("Sprawdzanie, czy istnieją aktualności bez wysłanych powiadomień...");
        return repo.existsByUsersNotifiedIsNull();
    }


}
