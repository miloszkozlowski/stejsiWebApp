package pl.mihome.stejsiWebApp.controller.controllerRest;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.mihome.stejsiWebApp.exeptions.CalendarTimeConflictException;
import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.HttpErrorBody;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice("pl.mihome.stejsiWebApp.controller.controllerRest")
@Order(1)
public class ErrorHandlerRest extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    ResponseEntity<HttpErrorBody> handleUnexpectedExceptions(Exception exception) {
        var error = new HttpErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), List.of("Internal server error"));
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<HttpErrorBody> handleAccessDeniedException(AccessDeniedException exception) {
        var error = new HttpErrorBody(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED", List.of(exception.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CalendarTimeConflictException.class)
    ResponseEntity<HttpErrorBody> handleCalendarControllerRest(CalendarTimeConflictException exception) {
        var error = new HttpErrorBody(HttpStatus.CONFLICT, "CALENDAR_CONFLICT", List.of(exception.getReadableTermin() + " is overlapping"));
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundCustomException.class)
    ResponseEntity<HttpErrorBody> handleNotFoundCustomExceptions(NotFoundCustomException exception) {
        var error = new HttpErrorBody(HttpStatus.NOT_FOUND, exception.getMessage(), List.of(exception.getDesc()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<HttpErrorBody> handleIllegalArgExceptions(IllegalArgumentException exception) {
        var error = new HttpErrorBody(HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<HttpErrorBody> handleConstraintViolationException(ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations().stream()
                .map(c -> c.getPropertyPath() + ": " + c.getMessage())
                .collect(Collectors.toList());
        var error = new HttpErrorBody(HttpStatus.BAD_REQUEST, "VALID_ERROR", errors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        List<String> errorsList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        var error = new HttpErrorBody(status, "VALID_ERROR", errorsList);
        return new ResponseEntity<>(error, status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        var error = new HttpErrorBody(status, "VALID_ERROR", List.of(ex.getParameterName() + " missing or invalid"));
        return new ResponseEntity<>(error, status);
    }
}
