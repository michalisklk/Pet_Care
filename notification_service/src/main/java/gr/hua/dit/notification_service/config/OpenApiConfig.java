package gr.hua.dit.notification_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ρυθμίσεις για OpenAPI / Swagger της εξωτερικής υπηρεσίας SMS.
 *
 * Εδώ ορίζουμε:
 * - τις βασικές πληροφορίες του API (τίτλος, έκδοση, περιγραφή)
 * - ένα group "sms-api" που περιλαμβάνει τα endpoints του /api/v1/sms
 */
@Configuration
public class OpenApiConfig {

    /**
     * Bean OpenAPI με βασικές πληροφορίες για το API.
     * Το χρησιμοποιεί το springdoc για να εμφανίσει τίτλο, εκδοχή, περιγραφή
     * στο Swagger UI και στο /v3/api-docs.
     */
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetCare Notification Service") // τίτλος του API
                        .version("v1")                        // έκδοση του API
                        .description("Stateless REST API για αποστολή SMS που χρησιμοποιείται από την εφαρμογή PetCare"));
    }

    /**
     * Ορισμός group "sms-api" στο Swagger.
     *
     * - group("sms-api"): όνομα ομάδας που θα φαίνεται στο Swagger UI
     * - packagesToScan(...): από ποιο package θα διαβάσει controllers
     * - pathsToMatch(...): ποια paths θα ανήκουν σε αυτό το group
     */
    @Bean
    public GroupedOpenApi smsApi() {
        return GroupedOpenApi.builder()
                .group("sms-api")
                // Εδώ είναι το package όπου βρίσκεται ο SmsResource controller
                .packagesToScan("gr.hua.dit.notification_service.web.rest")
                // Θα συμπεριλάβει όλα τα endpoints που ξεκινούν με /api/v1/sms
                .pathsToMatch("/api/v1/sms/**")
                .build();
    }
}
