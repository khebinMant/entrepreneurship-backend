package com.project.emprendia.shared.util;

/**
 * Constants related to Spring Security and Keycloak JWT claims.
 */
public class SecurityConstants {

    // Roles (without ROLE_ prefix – Spring Security adds it automatically)
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER  = "USER";

    // JWT claim names issued by Keycloak
    public static final String KEYCLOAK_ID_CLAIM  = "sub";
    public static final String USERNAME_CLAIM     = "preferred_username";
    public static final String EMAIL_CLAIM        = "email";
    public static final String REALM_ACCESS_CLAIM = "realm_access";
    public static final String ROLES_FIELD        = "roles";

    // HTTP
    public static final String BEARER_PREFIX          = "Bearer ";
    public static final String AUTHORIZATION_HEADER   = "Authorization";

    private SecurityConstants() {
    }
}
