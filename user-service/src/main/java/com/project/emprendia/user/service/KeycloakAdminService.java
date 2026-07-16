package com.project.emprendia.user.service;

import com.project.emprendia.user.dto.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KeycloakAdminService {

    private final RestClient restClient;
    private final String serverUrl;
    private final String adminUsername;
    private final String adminPassword;
    private final String realm;
    private final String defaultRoleName;

    public KeycloakAdminService(
            RestClient.Builder restClientBuilder,
            @Value("${keycloak.admin.server-url}") String serverUrl,
            @Value("${keycloak.admin.username}") String adminUsername,
            @Value("${keycloak.admin.password}") String adminPassword,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.role.default-role-name}") String defaultRoleName) {
        this.restClient = restClientBuilder.build();
        this.serverUrl = serverUrl;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.realm = realm;
        this.defaultRoleName = defaultRoleName;
    }

    public String createUser(UserRequest request) {
        String adminToken = getAdminToken();
        String keycloakUserId = doCreateUser(adminToken, request);
        assignDefaultRole(adminToken, keycloakUserId);
        log.info("Usuario creado en Keycloak con ID: {} y rol '{}' asignado", keycloakUserId, defaultRoleName);
        return keycloakUserId;
    }

    public void updateUser(String keycloakUserId, String firstName, String lastName, String email) {
        try {
            String adminToken = getAdminToken();
            doUpdateUser(adminToken, keycloakUserId, firstName, lastName, email);
            log.info("Usuario {} actualizado en Keycloak", keycloakUserId);
        } catch (Exception e) {
            log.error("Error al actualizar usuario {} en Keycloak: {}", keycloakUserId, e.getMessage());
            throw new RuntimeException("Error al actualizar usuario en Keycloak: " + e.getMessage(), e);
        }
    }

    public void changePassword(String keycloakUserId, String newPassword) {
        try {
            String adminToken = getAdminToken();
            doChangePassword(adminToken, keycloakUserId, newPassword);
            log.info("Contraseña actualizada para usuario {} en Keycloak", keycloakUserId);
        } catch (Exception e) {
            log.error("Error al cambiar contraseña para usuario {} en Keycloak: {}", keycloakUserId, e.getMessage());
            throw new RuntimeException("Error al cambiar contraseña en Keycloak: " + e.getMessage(), e);
        }
    }

    public String getUserEmail(String keycloakUserId) {
        try {
            String adminToken = getAdminToken();
            Map<String, Object> userData = restClient.get()
                    .uri(serverUrl + "/admin/realms/{realm}/users/{userId}", realm, keycloakUserId)
                    .header("Authorization", "Bearer " + adminToken)
                    .retrieve()
                    .body(Map.class);
            return userData != null ? (String) userData.get("email") : null;
        } catch (Exception e) {
            log.warn("No se pudo obtener email de Keycloak para usuario {}: {}", keycloakUserId, e.getMessage());
            return null;
        }
    }

    public void assignDefaultRole(String keycloakUserId) {
        try {
            String adminToken = getAdminToken();
            assignDefaultRole(adminToken, keycloakUserId);
        } catch (Exception e) {
            log.warn("No se pudo asignar rol '{}' al usuario {}: {}", defaultRoleName, keycloakUserId, e.getMessage());
        }
    }

    private void assignDefaultRole(String adminToken, String keycloakUserId) {
        assignRealmRole(adminToken, keycloakUserId, defaultRoleName);
    }

    private void assignRealmRole(String adminToken, String keycloakUserId, String roleName) {
        try {
            Map<String, Object> role = getRole(adminToken, roleName);
            if (role != null) {
                assignRole(adminToken, keycloakUserId, role);
                log.info("Rol '{}' asignado al usuario de Keycloak: {}", roleName, keycloakUserId);
            }
        } catch (Exception e) {
            log.warn("No se pudo asignar rol '{}' al usuario {}: {}", roleName, keycloakUserId, e.getMessage());
        }
    }

    private String getAdminToken() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");
        body.add("username", adminUsername);
        body.add("password", adminPassword);

        Map<String, Object> response = restClient.post()
                .uri(serverUrl + "/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(Map.class);

        return (String) response.get("access_token");
    }

    private String doCreateUser(String adminToken, UserRequest request) {
        Map<String, Object> keycloakUser = Map.of(
                "username", request.getUsername(),
                "email", request.getEmail(),
                "firstName", request.getFirstName(),
                "lastName", request.getLastName(),
                "enabled", true,
                "emailVerified", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", request.getPassword(),
                        "temporary", false
                ))
        );

        ResponseEntity<Void> response = restClient.post()
                .uri(serverUrl + "/admin/realms/{realm}/users", realm)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(keycloakUser)
                .retrieve()
                .toBodilessEntity();

        URI location = response.getHeaders().getLocation();
        if (location == null) {
            throw new RuntimeException("No se recibió Location header al crear usuario en Keycloak");
        }

        return location.getPath().substring(location.getPath().lastIndexOf('/') + 1);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getRole(String adminToken, String roleName) {
        return restClient.get()
                .uri(serverUrl + "/admin/realms/{realm}/roles/{roleName}", realm, roleName)
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .body(Map.class);
    }

    private void assignRole(String adminToken, String userId, Map<String, Object> role) {
        restClient.post()
                .uri(serverUrl + "/admin/realms/{realm}/users/{userId}/role-mappings/realm", realm, userId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(Map.of("id", role.get("id"), "name", role.get("name"))))
                .retrieve()
                .toBodilessEntity();
    }

    private void doUpdateUser(String adminToken, String keycloakUserId, String firstName, String lastName, String email) {
        Map<String, Object> updates = new java.util.HashMap<>();
        if (firstName != null) updates.put("firstName", firstName);
        if (lastName != null) updates.put("lastName", lastName);
        if (email != null) updates.put("email", email);

        if (!updates.isEmpty()) {
            restClient.put()
                    .uri(serverUrl + "/admin/realms/{realm}/users/{userId}", realm, keycloakUserId)
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updates)
                    .retrieve()
                    .toBodilessEntity();
        }
    }

    private void doChangePassword(String adminToken, String keycloakUserId, String newPassword) {
        List<Map<String, Object>> credentials = List.of(Map.of(
                "type", "password",
                "value", newPassword,
                "temporary", false
        ));

        restClient.put()
                .uri(serverUrl + "/admin/realms/{realm}/users/{userId}", realm, keycloakUserId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("credentials", credentials))
                .retrieve()
                .toBodilessEntity();
    }
}
