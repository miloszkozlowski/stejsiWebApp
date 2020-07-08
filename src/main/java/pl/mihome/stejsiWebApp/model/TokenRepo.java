package pl.mihome.stejsiWebApp.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenRepo {
	
	List<Token> findByOwnerAndActiveIsTrue(Podopieczny owner);
	
	Token save(Token token);

	void removeOutOfDate(LocalDateTime olderThan);
	
	void deleteAllByAssignedDeviceId(String assignedDeviceId);
	
	Optional<Token> findByTokenStringAndAssignedDeviceIdAndRemovedIsFalse(String token, String assignedDeviceId);
	
	Optional<Token> findByActivationCodeAndActiveIsFalse(String activationCode);
	
	Optional<Token> findByTokenStringAndActiveIsTrueAndRemovedIsFalse(String tokenString);

}
