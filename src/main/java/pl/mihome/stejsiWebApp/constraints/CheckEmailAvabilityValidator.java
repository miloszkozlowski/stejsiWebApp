package pl.mihome.stejsiWebApp.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import pl.mihome.stejsiWebApp.model.PodopiecznyRepo;

public class CheckEmailAvabilityValidator implements ConstraintValidator<CheckEmailAvability, String> {
	
	@Autowired
	private PodopiecznyRepo repo;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return !repo.existsByEmail(value.toLowerCase());
	}

}
