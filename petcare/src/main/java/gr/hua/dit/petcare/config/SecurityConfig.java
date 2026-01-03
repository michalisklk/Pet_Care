package gr.hua.dit.petcare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )

                .authorizeHttpRequests(auth -> auth
                        // δημόσια pages
                        .requestMatchers(
                                "/",
                                "/register",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/h2-console/**"
                        ).permitAll()

                        // δημόσια endpoints
                        .requestMatchers("/api/test-notifications/**").permitAll()
                        .requestMatchers("/appointments/**").permitAll()
                        .requestMatchers("/ui/notifications/**").permitAll()

                        // Προστατευμένα endpoints της εφαρμογής
                        .requestMatchers("/pets/**").authenticated()
                        .requestMatchers("/vet/**").hasRole("VET")

                        // οποιοδήποτε άλλο request απαιτεί authentication
                        .anyRequest().authenticated()
                )

                // φόρμα login
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username") // email
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // αποσύνδεση
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}
