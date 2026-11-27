package stayops_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import stayops_api.security.JwtAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    // ==========================
    // SECURITY FILTER CHAIN
    // ==========================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

            // ==========================
            // CORS
            // ==========================

            .cors(cors ->
                    cors.configurationSource(
                            corsConfigurationSource()
                    )
            )


            // ==========================
            // CSRF
            // ==========================

            .csrf(csrf -> csrf.disable())


            // ==========================
            // AUTHORIZATION
            // ==========================

            .authorizeHttpRequests(auth -> auth

                // Browser preflight requests
                .requestMatchers(
                        HttpMethod.OPTIONS,
                        "/**"
                ).permitAll()

                // Login / Signup
                .requestMatchers(
                        "/api/auth/**"
                ).permitAll()

                // Admin Console Endpoints
                .requestMatchers(
                        "/api/admin/**"
                ).permitAll()

                // Platform SaaS Subscriptions & Billing
                .requestMatchers(
                        "/api/platform/**"
                ).permitAll()

                // Public Property & Storefront Endpoints
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/properties/*/availability"
                ).permitAll()

                // Public Guest Booking Endpoints
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/reservations",
                        "/api/reservations/**",
                        "/api/upload/document",
                        "/api/crm/documents/upload"
                ).permitAll()

                // Public Razorpay Payment Endpoints
                .requestMatchers(
                        "/api/payments/razorpay/**"
                ).permitAll()

                // Public Guest Mobile ID Verification Endpoints
                .requestMatchers(
                        "/api/guest-verification/**"
                ).permitAll()

                // WebSocket Live Handshake
                .requestMatchers(
                        "/ws",
                        "/ws/**"
                ).permitAll()

                // Meal Orders API
                .requestMatchers(
                        "/api/meal-orders/**"
                ).permitAll()

                // Housekeeping & Room Status API
                .requestMatchers(
                        "/api/housekeeping/**"
                ).permitAll()

                // Petty Cash API
                .requestMatchers(
                        "/api/petty-cash/**"
                ).permitAll()

                // Localization API
                .requestMatchers(
                        "/api/localization/**"
                ).permitAll()

                // Team & Staff API
                .requestMatchers(
                        "/api/team/**"
                ).permitAll()

                // Static uploads
                .requestMatchers(
                        HttpMethod.GET,
                        "/uploads/**"
                ).permitAll()

                // Swagger OpenAPI Documentation
                .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                ).permitAll()

                // Everything else requires authentication
                .anyRequest().authenticated()
            )


            // ==========================
            // JWT FILTER
            // ==========================

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );


        return http.build();
    }


    // ==========================
    // CORS CONFIGURATION
    // ==========================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        configuration.setAllowedOriginPatterns(
                List.of(
                        "*",
                        "http://*.localhost:5173",
                        "http://localhost:5173",
                        "http://localhost:8080",
                        "http://127.0.0.1:5173",
                        "http://127.0.0.1:8080",
                        "https://*.trycloudflare.com",
                        "https://*.loca.lt",
                        "https://*.stayops.in",
                        "https://stayops.in"
                )
        );


        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS",
                        "PATCH"
                )
        );


        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "X-Requested-With",
                        "Accept",
                        "Origin",
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers"
                )
        );


        configuration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }
}