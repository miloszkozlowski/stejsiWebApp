package pl.mihome.stejsiWebApp.constraints;

import pl.mihome.stejsiWebApp.constraints.CheckDateFormat.List;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateFormatValidator.class)
@Documented
@Repeatable(List.class)
public @interface CheckDateFormat {
	
	String message() default "{pl.mihome.stejsiWebApp.constraints.DateFormat.message}";
	
	Class<?>[] groups() default { };
	
	Class<? extends Payload>[] payload() default { };
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
		CheckDateFormat[] value();
    }

}
