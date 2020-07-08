package pl.mihome.stejsiWebApp.logic;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.SendFailedException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.mihome.stejsiWebApp.DTO.PodopiecznyReadModel;
import pl.mihome.stejsiWebApp.DTO.PodopiecznyWriteModel;
import pl.mihome.stejsiWebApp.DTO.appComms.EmailRegistrationDTO;
import pl.mihome.stejsiWebApp.DTO.appComms.RegistrationStatus;
import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.PodopiecznyRepo;
import pl.mihome.stejsiWebApp.model.Token;
import pl.mihome.stejsiWebApp.model.TokenRepo;

@Service
public class PodopiecznyService {

	private PodopiecznyRepo repo;
	private TokenRepo tokenRepo;
	private AppClientService appClientService;
	
	public PodopiecznyService(PodopiecznyRepo repo, TokenRepo tokenRepo, AppClientService appClientService) {
		this.repo = repo;
		this.tokenRepo = tokenRepo;
		this.appClientService = appClientService;
	}
	
	public PodopiecznyReadModel getUserById(Long id) throws IllegalArgumentException {
		return repo.findById(id).map(PodopiecznyReadModel::new).orElseThrow(() -> new IllegalArgumentException());
	}
	
	public PodopiecznyReadModel saveNewUser(PodopiecznyWriteModel source) {
		var p = repo.save(source.toPodopieczny());
		return new PodopiecznyReadModel(p);
	}
	
	public List<PodopiecznyReadModel> getAll() {
		return repo.findAll().stream()
				.filter(u -> !u.isRemoved())
				.map(u -> new PodopiecznyReadModel(u))
				.collect(Collectors.toList());
	}
	
	public Page<PodopiecznyReadModel> getAllPage(int pageNo, String sort, Boolean reverese, Boolean onlyactive) {
		
		Pageable pageable;
		Sort.Order order;
		if(reverese) {
			order = new Sort.Order(Sort.Direction.DESC, sort).ignoreCase();
			pageable = PageRequest.of(pageNo, 10, Sort.by(order));

		}
		else {
			order = new Sort.Order(Sort.Direction.ASC, sort).ignoreCase();
			pageable = PageRequest.of(pageNo, 10, Sort.by(order));
		}
		
		Page<Podopieczny> listaPage;
		
		if(onlyactive)
			listaPage = repo.findByAktywnyIsTrue(pageable);
		else
			listaPage = repo.findAll(pageable);
		
			
		List<PodopiecznyReadModel> listaContent= listaPage.stream()
				.map(PodopiecznyReadModel::new)
				.collect(Collectors.toList());
		return new PageImpl<PodopiecznyReadModel>(listaContent, pageable, listaPage.getTotalElements());
		
	}
	
	
	
	@Transactional
	public RegistrationStatus registerDeviceStatus(EmailRegistrationDTO registerSet) throws SendFailedException {
		var user = repo.findByEmail(registerSet.getEmailAddress().trim());
		
		if(user.isEmpty())
			return RegistrationStatus.EMAIL_NOT_FOUND;
		
		if(user.get().isRemoved())
			return RegistrationStatus.EMAIL_NOT_FOUND;
		
		
		var userTokens = tokenRepo.findByOwnerAndActiveIsTrue(user.get());
		
		if(userTokens.size()==0) {
			appClientService.createNewDeviceSession(user.get(), registerSet);
			return RegistrationStatus.ACTIVATION_SENT;
		}
		
		else {
			Optional<Token> matchingToken = userTokens.stream()
			.sorted(Comparator.comparing(Token::getWhenCreated))
			.filter(t -> t.getTokenString() == registerSet.getToken())
			.findFirst();
			
			if(matchingToken.isPresent()) {
				if(matchingToken.get().getAssignedDeviceId() == registerSet.getAndroidDeviceId() && !matchingToken.get().isRemoved())
					return RegistrationStatus.ALREADY_OK;
				else
					return RegistrationStatus.DEVICE_MISMATCH;
			}
			
			if(userTokens.stream()
					.filter(t -> t.getAssignedDeviceId() != registerSet.getAndroidDeviceId() && !t.isRemoved())
					.count() > 0) {
				
				appClientService.createNewDeviceSession(user.get(), registerSet);
				return RegistrationStatus.NEW_DEVICE;
			}
			else {
				userTokens.stream()
				.filter(t -> t.getAssignedDeviceId() == registerSet.getAndroidDeviceId() && !t.isRemoved())
				.forEach(t -> t.setRemoved(true));
				appClientService.createNewDeviceSession(user.get(), registerSet);
				return RegistrationStatus.ACTIVATION_SENT;
			}
				
					
		}
		
	}
	


}
