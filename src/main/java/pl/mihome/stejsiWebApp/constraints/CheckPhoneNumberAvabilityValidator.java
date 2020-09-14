package pl.mihome.stejsiWebApp.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import pl.mihome.stejsiWebApp.logic.PodopiecznyService;
import pl.mihome.stejsiWebApp.model.PodopiecznyRepo;

public class CheckPhoneNumberAvabilityValidator implements ConstraintValidator<CheckPhoneNumberAvability, String> {
	
	@Autowired
	private PodopiecznyService podopiecznyService;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return podopiecznyService.isPhoneNumberAvailableForUser(value);
	}

}
