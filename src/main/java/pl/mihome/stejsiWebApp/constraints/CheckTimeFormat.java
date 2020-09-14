package pl.mihome.stejsiWebApp.constraints;

import pl.mihome.stejsiWebApp.constraints.CheckTimeFormat.List;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeFormatValidator.class)
@Documented
@Repeatable(List.class)
public @interface CheckTimeFormat {
	
	String message() default "{pl.mihome.stejsiWebApp.constraints.TimeFormat.message}";
	
	Class<?>[] groups() default { };
	
	Class<? extends Payload>[] payload() default { };
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
		CheckTimeFormat[] value();
    }

}
