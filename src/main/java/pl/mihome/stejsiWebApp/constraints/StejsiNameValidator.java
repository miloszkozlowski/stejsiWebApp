package pl.mihome.stejsiWebApp.constraints;


import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import pl.mihome.stejsiWebApp.DTO.PodopiecznyWriteModel;

public class StejsiNameValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return PodopiecznyWriteModel.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PodopiecznyWriteModel userDTO = (PodopiecznyWriteModel) target;
		if(userDTO.getName().toLowerCase().equals("stejsi")) {
			errors.reject("name", "Jedna Stejsi już jest - podopieczny nie może tak się nazywać!");
			
		}
		
	}
	

}
