package pl.mihome.stejsiWebApp.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import pl.mihome.stejsiWebApp.constraints.CheckPhoneNumberAvability.List;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckPhoneNumberAvabilityValidator.class)
@Documented
@Repeatable(List.class)
public @interface CheckPhoneNumberAvability {

	String message() default "{pl.mihome.stejsiWebApp.constraints.CheckPhoneNumberAvability.message}";
	
	Class<?>[] groups() default { };
	
	Class<? extends Payload>[] payload() default { };
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
		CheckPhoneNumberAvability[] value();
    }
	
}
