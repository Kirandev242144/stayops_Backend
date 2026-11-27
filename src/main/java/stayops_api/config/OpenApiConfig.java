package stayops_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        String descriptionMarkdown = """
                ### 🏨 StayOps Hospitality Operations System - Developer Portal
                
                Welcome to the official API documentation for **StayOps**, an all-in-one Property Management System (PMS), Caretaker Operations Platform, and Direct Guest Reservation Engine.
                
                ---
                
                #### 🏗️ Core Architecture & Data Flow
                ```
                [ React Frontend ]  ──(HTTP REST / JWT)──>  [ Spring Boot API ]  ──(JPA / Hibernate)──>  [ MySQL DB ]
                ```
                
                #### 🔑 Multi-Tenant Security Model
                - **Authentication**: JWT Tokens generated on `POST /api/auth/login` (supports property subdomain tenancy validation).
                - **Data Isolation**: All queries and mutations (Properties, Reservations, Meal Orders, Policies, Taxes, Petty Cash) are strictly scoped to the authenticated owner's `userId`.
                - **Bearer Auth Header**: `Authorization: Bearer <your_jwt_token>`
                
                ---
                
                #### 📌 Functional Modules
                1. **Authentication (`/api/auth`)**: User signup & JWT token issuance with tenant subdomain checks.
                2. **Property Management (`/api/properties`)**: Setup properties, room categories, amenities, Wi-Fi credentials, and photos.
                3. **Front Desk & Reservations (`/api/reservations`)**: Manage bookings, room assignments, check-ins/outs, and stay statuses.
                4. **Calendar & Rate Ops (`/api/calendar`)**: Room blackout dates, maintenance windows, and seasonal rate overrides.
                5. **Meal Orders & Dining (`/api/meal-orders`)**: In-room dining and F&B order logging against active guest stays.
                6. **Guest CRM & Profiles (`/api/crm`)**: Guest stay histories, preferences, and contact details.
                7. **Tax & Policy Settings (`/api/policies`, `/api/properties/{id}/taxes`)**: Cancellation rules, GST modes, and service charges.
                8. **Public Storefront (`/api/public`)**: Unauthenticated storefront property details, availability, and guest policies.
                9. **Housekeeping & Room Status (`/api/housekeeping`)**: Real-time room cleaning states (Clean, Dirty, Inspection).
                10. **Payment Gateways & Logs (`/api/payments`)**: Razorpay online payment integration, webhook handlers, and transaction logs.
                11. **Petty Cash Ledger (`/api/petty-cash`)**: Property cash float, owner funding, and caretaker expense ledger.
                """;

        return new OpenAPI()
                .info(new Info()
                        .title("StayOps Hospitality Management API Engine")
                        .version("1.0.0")
                        .description(descriptionMarkdown)
                        .contact(new Contact().name("StayOps Engineering Team").email("dev@stayops.test")))
                .tags(List.of(
                        new Tag().name("1. Authentication").description("User Registration, Login, and JWT Token Issuance"),
                        new Tag().name("2. Property & Room Inventory").description("Property setup, Room categories, Wi-Fi, Amenities, and Photos"),
                        new Tag().name("3. Front Desk & Reservations").description("Room Calendar, Booking Creation, Status Updates, and Guest Stays"),
                        new Tag().name("4. Calendar & Rate Ops").description("Room Blackout Dates, Maintenance, and Seasonal Rate Overrides"),
                        new Tag().name("5. Meal Orders & Dining").description("F&B Meal Order logging against active stays"),
                        new Tag().name("6. Guest CRM & Profiles").description("Guest CRM summary, stay history, and contact details"),
                        new Tag().name("7. Tax & Policy Settings").description("GST Tax Modes, Service Charges, and Cancellation Policies"),
                        new Tag().name("8. Public Storefront").description("Public Property Details, Availability, and Storefront Policies"),
                        new Tag().name("9. Housekeeping & Room Status").description("Room cleaning states (Clean, Dirty, In Progress)"),
                        new Tag().name("10. Payment Gateways & Logs").description("Razorpay payment integration, webhooks, and transaction audit logs"),
                        new Tag().name("11. Petty Cash Ledger").description("Property cash float, owner funding, and caretaker expense ledger")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
