package ru.panic.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/qiwi")
                .allowedOrigins(
                        "http://79.142.16.0/20",
                        "http://195.189.100.0/22",
                        "http://91.232.230.0/23",
                        "http://91.213.51.0/24"
                );
    }
}
