package gr.hua.dit.notification_catalog_service.web.api.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Κεντρικός χειριστής σφαλμάτων για ΟΛΟ το REST API.
 * Αντί να πετάει το Spring "άσχημα" errors,
 *   γυρνάμε πάντα ένα καθαρό JSON με:
 *   status, message, path, timestamp και (αν υπάρχει) fieldErrors.
 * Αυτό βοηθάει και στο Swagger, γιατί τα errors φαίνονται πιο καθαρά.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Πιάνει errors από @Valid @RequestBody (π.χ. POST /sms με λάθος/κενά πεδία).
     * μαζεύουμε όλα τα field errors σε Map:
     *  "content" -> "content is required"
     *  "phone"   -> "phone must be E.164"
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest req) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();

        // Παίρνουμε τα errors ανά πεδίο από το BindingResult
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage()));

        // Γυρνάμε 400 + "Validation failed" + map με τα πεδία
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI(), fieldErrors);
    }

    /**
     * Πιάνει errors από validations σε params/path/query όταν έχουμε @Validated.
     * Π.χ. /phone-numbers/{phone}/validations και το phone δεν ταιριάζει σε regex.
     * ConstraintViolationException έχει πολλές παραβιάσεις,
     * γι' αυτό τις μαζεύουμε σε map
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
                                                              HttpServletRequest req) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();

        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            // propertyPath -> σε ποιο πεδίο/param έγινε η παραβίαση
            // message -> το μήνυμα του validation
            fieldErrors.put(v.getPropertyPath().toString(), v.getMessage());
        }

        return build(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI(), fieldErrors);
    }

    /**
     * Γενικά "κακά requests" που δεν είναι καθαρό validation:
     * - MethodArgumentTypeMismatchException: λάθος τύπος (π.χ. id=abc αντί για number)
     * - HttpMessageNotReadableException: κακό JSON (δεν γίνεται parse)
     * - IllegalArgumentException: λάθος input που πετάμε εμείς στο service/controller
     *
     * Όλα αυτά τα γυρνάμε 400 με το message τους.
     */
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), null);
    }

    /**
     * Πολλά exceptions τύπου "ResponseStatusException" καταλήγουν σαν ErrorResponseException.
     * Εδώ διαβάζουμε:
     * - status code
     * - detail message (αν υπάρχει)
     * και το γυρνάμε σε μορφή ApiError.
     */
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiError> handleErrorResponseException(ErrorResponseException ex,
                                                                 HttpServletRequest req) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();

        // Προσπαθούμε να πάρουμε πιο ωραίο μήνυμα από το body, αλλιώς message
        String msg = ex.getBody() != null ? ex.getBody().getDetail() : ex.getMessage();

        return build(status, msg, req.getRequestURI(), null);
    }

    /**
     * Fallback: πιάνει Ο,ΤΙΔΗΠΟΤΕ άλλο δεν πιάστηκε παραπάνω.
     * Εδώ γυρνάμε 500 με generic μήνυμα
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleFallback(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", req.getRequestURI(), null);
    }

    /**
     * Helper μέθοδος για να χτίζουμε πάντα το ίδιο JSON format.
     * Έτσι όλα τα errors έχουν ίδιο schema:
     * timestamp, status, error, message, path, fieldErrors
     */
    private ResponseEntity<ApiError> build(HttpStatus status,
                                           String message,
                                           String path,
                                           Map<String, String> fieldErrors) {

        ApiError body = new ApiError(
                Instant.now(),             // πότε έγινε το error
                status.value(),            // π.χ. 400
                status.getReasonPhrase(),  // π.χ. "Bad Request"
                message,                   // λεπτομέρεια/μήνυμα
                path,                      // ποιο endpoint χτυπήθηκε
                fieldErrors                // null ή map με errors ανά πεδίο
        );

        return ResponseEntity.status(status).body(body);
    }
}
