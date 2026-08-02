package com.mrunali.fleet_mgmt_platform.config;

import com.mrunali.fleet_mgmt_platform.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/users/set-password").permitAll()
                .requestMatchers("/api/users/invite").hasRole("ADMIN")
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/vehicles/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/assignments/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/trips/start").hasRole("DRIVER")
                .requestMatchers("/api/trips/end").hasRole("DRIVER")
                .requestMatchers("/api/trips/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/telemetry").permitAll() // Allow telemetry ingestion without authentication
                .requestMatchers("/api/telemetry/trip/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/telemetry/latest/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/live-vehicle-status/vehicle/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/live-vehicle-status/all").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/live-vehicle-status/connected").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/live-vehicle-status/disconnected").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/alerts/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration configuration = new CorsConfiguration();
    //     configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Add your frontend URL here
    //     configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    //     configuration.setAllowedHeaders(Arrays.asList("*"));
    //     configuration.setAllowCredentials(true);
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", configuration);
    //     return source;
    // }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}