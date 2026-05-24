package org.ide.dbp_proyecto.config;

import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.jwt.JwtFilter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                // Permitir iframes desde el mismo origen (necesario para H2 console)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/user/login",
                                "/user/register"
                        )
                        .permitAll()

                        // ⚠️ TEMPORAL — sin auth hasta que el Integrante 1 implemente roles.
                        // Luego cambiar a .hasRole("ADMIN") o equivalente.
                        .requestMatchers("/api/admin/**")
                        .permitAll()

                        // ⚠️ TEMPORAL — consola H2 abierta para desarrollo. Eliminar en producción.
                        .requestMatchers("/h2-console/**")
                        .permitAll()
                        // --------------------------------------------------------

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}