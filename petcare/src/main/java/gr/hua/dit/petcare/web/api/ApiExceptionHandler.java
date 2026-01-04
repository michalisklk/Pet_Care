package gr.hua.dit.petcare.web.api;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, ValidationException.class})
    public ErrorResponse badRequest(Exception e) {
        return new ErrorResponse("BAD_REQUEST", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse notFound(Exception e) {
        return new ErrorResponse("NOT_FOUND", e.getMessage());
    }

    public record ErrorResponse(String code, String message) {}
}
