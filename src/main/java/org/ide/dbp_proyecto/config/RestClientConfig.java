package org.ide.dbp_proyecto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${google.places.timeout-ms:5000}")
    private int timeoutMs;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofMillis(timeoutMs))
                .readTimeout(Duration.ofMillis(timeoutMs))
                .build();
    }
}
