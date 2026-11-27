package stayops_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        System.out.println(
                "JWT FILTER -> " +
                request.getMethod() +
                " " +
                request.getRequestURI()
        );

        System.out.println(
                "AUTH HEADER -> " +
                authorizationHeader
        );


        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            System.out.println(
                    "JWT FILTER -> NO BEARER TOKEN"
            );

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        String token =
                authorizationHeader.substring(7);


        try {

            String email =
                    jwtService.extractEmail(token);

            Long userId =
                    jwtService.extractUserId(token);


            System.out.println(
                    "JWT EMAIL -> " +
                    email
            );

            System.out.println(
                    "JWT USER ID -> " +
                    userId
            );


            if (email != null &&
                    userId != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId.toString(),
                                null,
                                Collections.emptyList()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );


                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );


                System.out.println(
                        "JWT FILTER -> AUTHENTICATED USER " +
                        userId
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "JWT FILTER -> INVALID TOKEN"
            );

            System.out.println(
                    "JWT ERROR -> " +
                    e.getMessage()
            );
        }


        filterChain.doFilter(
                request,
                response
        );
    }
}