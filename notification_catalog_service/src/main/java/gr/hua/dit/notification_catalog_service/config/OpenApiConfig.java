package gr.hua.dit.notification_catalog_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ρυθμίσεις για OpenAPI / Swagger της εξωτερικής υπηρεσίας
 * - Το Swagger UI είναι μια σελίδα που μας δείχνει τα endpoints και μας αφήνει να τα δοκιμάζουμε.
 * - Το springdoc διαβάζει τους controllers και φτιάχνει αυτόματα το OpenAPI ( /v3/api-docs ).
 *
 * βάζουμε:
 * βασικές πληροφορίες του API (τίτλος, έκδοση, περιγραφή)
 * ένα group στο Swagger για να είναι όλα μαζεμένα/τακτοποιημένα
 */
@Configuration
public class OpenApiConfig {

    /**
     * Βασικά στοιχεία του API.
     */
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetCare Notification & Catalog Service")   // τίτλος που θα φαίνεται στο Swagger
                        .version("v1")                           // έκδοση (για εμάς είναι v1)
                        .description("REST API για phone validation, αποστολή SMS και βοηθητικό vaccine catalog (mock), που καλείται από την PetCare."));
    }

    /**
     * Group στο Swagger για όλα τα endpoints του /api/v1/** (sms + phone-numbers).
     */
    @Bean
    public GroupedOpenApi notificationApi() {
        return GroupedOpenApi.builder()
                .group("notification-api")
                // Σκανάρει τους controllers που είναι εδώ μέσα:
                .packagesToScan("gr.hua.dit.notification_catalog_service.web.rest")
                // Θα δείξει όλα τα endpoints που ξεκινάνε με /api/v1/
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
