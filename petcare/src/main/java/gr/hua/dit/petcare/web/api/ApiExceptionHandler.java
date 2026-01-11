package gr.hua.dit.petcare.web.api;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import jakarta.validation.ConstraintViolationException;


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
    /**
     * @Valid αποτυχία σε @RequestBody (JSON) -> MethodArgumentNotValidException
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse methodArgumentNotValid(MethodArgumentNotValidException e) {

        // Παίρνουμε το πρώτο field error για απλό/καθαρό μήνυμα
        FieldError fe = e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);

        String msg;
        if (fe != null) {
            msg = fe.getField() + ": " + fe.getDefaultMessage();
        } else {
            msg = "Validation error";
        }

        return new ErrorResponse("BAD_REQUEST", msg);
    }

    /**
     * @Validated αποτυχία σε query params/path vars (π.χ. @RequestParam @Min) -> ConstraintViolationException
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse constraintViolation(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .orElse("Validation error");
        return new ErrorResponse("BAD_REQUEST", msg);
    }

    public record ErrorResponse(String code, String message) {}
}
