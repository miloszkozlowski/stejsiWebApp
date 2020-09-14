package pl.mihome.stejsiWebApp.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class TimeFormatValidator implements ConstraintValidator<CheckTimeFormat, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        try {
            LocalTime.parse(value);
        } catch (DateTimeParseException ex) {
            return false;
        }
        return true;
    }
}
