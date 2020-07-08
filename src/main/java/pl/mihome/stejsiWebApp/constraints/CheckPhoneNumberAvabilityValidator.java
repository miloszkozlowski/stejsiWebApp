package pl.mihome.stejsiWebApp.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import pl.mihome.stejsiWebApp.model.PodopiecznyRepo;

public class CheckPhoneNumberAvabilityValidator implements ConstraintValidator<CheckPhoneNumberAvability, String> {
	
	@Autowired
	private PodopiecznyRepo repo;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		try {
		return !repo.existsByPhoneNumber(Integer.valueOf(value));
		}
		catch(NumberFormatException e) {
			return true;
		}
	}

}
