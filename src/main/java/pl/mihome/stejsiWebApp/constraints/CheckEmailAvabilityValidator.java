package pl.mihome.stejsiWebApp.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import pl.mihome.stejsiWebApp.logic.PodopiecznyService;
import pl.mihome.stejsiWebApp.model.PodopiecznyRepo;

public class CheckEmailAvabilityValidator implements ConstraintValidator<CheckEmailAvability, String> {
	
	@Autowired
	private PodopiecznyService service;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return service.isEmailAvailableForUse(value);
	}

}
