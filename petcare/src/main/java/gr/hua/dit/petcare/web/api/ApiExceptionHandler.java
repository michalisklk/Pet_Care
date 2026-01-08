package gr.hua.dit.petcare.web.api;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    //Κάνει map τα 401/403 ResponseStatusException σε JSON error response ώστε να φαίνονται σωστά το μήνυματα στο Swagger (και όχι "Failed to fetch")
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> responseStatus(ResponseStatusException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new ErrorResponse(e.getStatusCode().toString(), e.getReason()));
    }

    public record ErrorResponse(String code, String message) {}
}
