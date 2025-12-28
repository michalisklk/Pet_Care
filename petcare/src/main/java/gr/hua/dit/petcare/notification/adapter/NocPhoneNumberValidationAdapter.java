package gr.hua.dit.petcare.notification.adapter;

import gr.hua.dit.petcare.notification.dto.PhoneNumberValidationResult;
import gr.hua.dit.petcare.notification.port.PhoneNumberValidationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Adapter που κάνει το HTTP call προς την εξωτερική υπηρεσία (black box).
 * Η PetCare ΔΕΝ ξέρει τι γίνεται μέσα στην εξωτερική υπηρεσία.
 * Απλά καλεί αυτόν τον adapter, και ο adapter μιλάει με REST/HTTP.
 */
@Component
public class NocPhoneNumberValidationAdapter implements PhoneNumberValidationPort {

    // Το RestTemplate είναι το εργαλείο για να κάνουμε HTTP requests (GET/POST κλπ).
    private final RestTemplate restTemplate;

    // Το base URL το παίρνουμε από application.yml, π.χ.
    // external.phone-service.url = http://localhost:8081/api/v1/phone-numbers
    private final String phoneNumbersBaseUrl;

    public NocPhoneNumberValidationAdapter(
            RestTemplate restTemplate,
            @Value("${external.phone-service.url}") String phoneNumbersBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.phoneNumbersBaseUrl = phoneNumbersBaseUrl;
    }

    /**
     * Παίρνει ένα τηλέφωνο raw και ρωτάει την εξωτερική υπηρεσία
     * αν είναι valid και ποια είναι η E.164 μορφή
     */
    @Override
    public PhoneNumberValidationResult validate(String rawPhoneNumber) {
        try {
            // Φτιάχνουμε το τελικό URL του endpoint:
            // GET {baseUrl}/{phone}/validations
            // π.χ. http://localhost:8081/api/v1/phone-numbers/6940000000/validations
            String url = UriComponentsBuilder
                    .fromHttpUrl(phoneNumbersBaseUrl)
                    .path("/{phone}/validations")
                    .buildAndExpand(rawPhoneNumber)
                    .toUriString();

            // Κάνουμε GET request και ο Spring μετατρέπει το JSON σε PhoneNumberValidationResult
            return restTemplate.getForObject(url, PhoneNumberValidationResult.class);

        } catch (Exception e) {
            // Αν η υπηρεσία είναι down, ή γίνει error στο request,
            // τότε δεν θέλουμε να κρασάρει η PetCare.
            // Γυρνάμε ασφαλές αποτέλεσμα: valid=false
            return new PhoneNumberValidationResult(rawPhoneNumber, false, null, null);
        }
    }
}
