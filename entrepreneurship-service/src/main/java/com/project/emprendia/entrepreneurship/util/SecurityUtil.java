package com.project.emprendia.entrepreneurship.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

/**
 * Utility class for extracting information from the current JWT token.
 * Uses Spring Security context – valid only within an authenticated request scope.
 */
public class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * Returns the Keycloak user ID (sub claim) of the authenticated user.
     * Use this to verify ownership (e.g., user can only update their own entrepreneurship).
     */
    public static Optional<String> getCurrentKeycloakId() {
        return getCurrentJwt().map(Jwt::getSubject);
    }

    /**
     * Returns the preferred_username claim of the authenticated user.
     */
    public static Optional<String> getCurrentUsername() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString(SecurityConstants.USERNAME_CLAIM));
    }

    /**
     * Returns the email claim of the authenticated user.
     */
    public static Optional<String> getCurrentEmail() {
        return getCurrentJwt().map(jwt -> jwt.getClaimAsString(SecurityConstants.EMAIL_CLAIM));
    }

    /**
     * Checks whether the current authenticated user has the given role.
     */
    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_" + role));
    }

    /**
     * Returns the raw JWT token object for advanced claim extraction.
     */
    public static Optional<Jwt> getCurrentJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return Optional.of(jwtAuth.getToken());
        }
        return Optional.empty();
    }
}
