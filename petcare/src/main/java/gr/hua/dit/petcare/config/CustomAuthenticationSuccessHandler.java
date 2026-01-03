package gr.hua.dit.petcare.config;

import gr.hua.dit.petcare.core.model.Person;
import gr.hua.dit.petcare.core.model.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        Person user = (Person) authentication.getPrincipal();

        // εαν ειναι κτηνίατρος εισέρχεται στη σελίδα /vet/appointments/all διαφορετικά στη σελίδα /pets
        if (user.getRole() == Role.VET) {
            response.sendRedirect("/vet/appointments/all?vetId=" + user.getId());
        } else {
            response.sendRedirect("/pets");
        }
    }
}
