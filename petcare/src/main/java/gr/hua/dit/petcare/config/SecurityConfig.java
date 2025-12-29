package gr.hua.dit.petcare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/test-notifications/**").permitAll()
                        .requestMatchers("/appointments/**").permitAll()
                        .requestMatchers("/ui/notifications/**").permitAll()
                        .anyRequest().authenticated()
                );



        return http.build();
    }
}
