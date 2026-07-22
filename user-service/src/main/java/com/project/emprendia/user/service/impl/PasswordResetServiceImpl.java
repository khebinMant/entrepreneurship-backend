package com.project.emprendia.user.service.impl;

import com.project.emprendia.user.domain.AppUser;
import com.project.emprendia.user.domain.UserContact;
import com.project.emprendia.user.dto.CatalogueValueResponse;
import com.project.emprendia.user.dto.ForgotPasswordRequest;
import com.project.emprendia.user.dto.ResetPasswordRequest;
import com.project.emprendia.user.exception.BadRequestException;
import com.project.emprendia.user.exception.ResourceNotFoundException;
import com.project.emprendia.user.repository.UserContactRepository;
import com.project.emprendia.user.repository.UserRepository;
import com.project.emprendia.user.service.KeycloakAdminService;
import com.project.emprendia.user.service.PasswordResetService;
import com.project.emprendia.user.client.SharedServiceClient;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserContactRepository userContactRepository;
    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;
    private final SharedServiceClient sharedServiceClient;
    private final JavaMailSender mailSender;

    @Value("${app.reset-password.token-expiration-minutes:30}")
    private int tokenExpirationMinutes;

    @Value("${app.reset-password.base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    private final Map<String, TokenData> tokenStore = new ConcurrentHashMap<>();

    @PostConstruct
    void startCleanup() {
        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    Thread.sleep(60_000);
                    Instant cutoff = Instant.now().minusSeconds(tokenExpirationMinutes * 60);
                    tokenStore.entrySet().removeIf(e -> e.getValue().createdAt().isBefore(cutoff));
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    private record TokenData(String email, Instant createdAt, boolean used) {
        TokenData markUsed() { return new TokenData(email, createdAt, true); }
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        Long emailTypeId = getEmailContactTypeId();

        UserContact contact = userContactRepository
            .findByContactTypeIdAndContactValue(emailTypeId, email)
            .orElseThrow(() -> new ResourceNotFoundException("No user found with email: " + email));

        AppUser user = contact.getUser();
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, new TokenData(email, Instant.now(), false));

        String resetLink = frontendBaseUrl + "/reset-password?token=" + token;

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Recuperaci\u00f3n de Contrase\u00f1a - Emprendia");
            helper.setText(buildResetPasswordHtml(user.getFirstName(), resetLink, tokenExpirationMinutes), true);
            mailSender.send(mimeMessage);
            log.info("Correo de recuperaci\u00f3n enviado a {}", email);
        } catch (Exception e) {
            tokenStore.remove(token);
            log.error("Error al enviar correo de recuperaci\u00f3n a {}: {}", email, e.getMessage());
            throw new RuntimeException("Error al enviar el correo de recuperaci\u00f3n", e);
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        TokenData data = tokenStore.get(request.getToken());
        if (data == null) {
            throw new BadRequestException("Invalid or expired reset token");
        }
        if (data.used()) {
            throw new BadRequestException("Reset token has already been used");
        }
        if (data.createdAt().plusSeconds(tokenExpirationMinutes * 60).isBefore(Instant.now())) {
            tokenStore.remove(request.getToken());
            throw new BadRequestException("Reset token has expired");
        }

        Long emailTypeId = getEmailContactTypeId();
        UserContact contact = userContactRepository
            .findByContactTypeIdAndContactValue(emailTypeId, data.email())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + data.email()));

        AppUser user = contact.getUser();
        keycloakAdminService.changePassword(user.getKeycloakId(), request.getNewPassword());

        tokenStore.put(request.getToken(), data.markUsed());
        log.info("Contrase\u00f1a restablecida exitosamente para el usuario ID: {} con email: {}",
            user.getUserId(), data.email());
    }

    private String buildResetPasswordHtml(String userName, String resetLink, int expirationMinutes) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f0f2f5; }
                    .container { max-width: 600px; margin: 30px auto; background: #ffffff; border-radius: 4px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 35px 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 26px; font-weight: 600; letter-spacing: 0.5px; }
                    .content { padding: 35px 30px; }
                    .content p { line-height: 1.7; color: #444; margin: 0 0 15px; }
                    .actions { text-align: center; margin: 30px 0 10px; }
                    .btn-primary { display: inline-block; padding: 14px 40px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; text-decoration: none; border-radius: 4px; font-weight: 600; font-size: 15px; letter-spacing: 0.3px; }
                    .btn-primary:hover { opacity: 0.9; }
                    .warning-box { background: #fff8e1; border-left: 4px solid #f9a825; padding: 15px 20px; margin: 20px 0; border-radius: 4px; }
                    .warning-box p { margin: 5px 0 0; color: #444; font-size: 13px; }
                    .footer { padding: 20px 30px; text-align: center; color: #999; font-size: 12px; border-top: 1px solid #eee; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Recuperaci\u00f3n de Contrase\u00f1a</h1>
                    </div>
                    <div class="content">
                        <p>Hola <strong>__USER_NAME__</strong>,</p>
                        <p>Hemos recibido una solicitud para restablecer la contrase\u00f1a de tu cuenta en <strong>Emprendia</strong>.</p>
                        <p>Para continuar con el proceso, haz clic en el siguiente bot\u00f3n:</p>
                        <div class="actions">
                            <a class="btn-primary" href="__RESET_LINK__">Restablecer Contrase\u00f1a</a>
                        </div>
                        <div class="warning-box">
                            <p>Este enlace expirar\u00e1 en <strong>__EXPIRATION__ minutos</strong>.</p>
                            <p>Si no solicitaste este cambio, ignora este mensaje y tu contrase\u00f1a permanecer\u00e1 segura.</p>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Este es un correo generado autom\u00e1ticamente. Por favor no responder.</p>
                        <p>&copy; 2026 Emprendia. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """
            .replace("__USER_NAME__", userName)
            .replace("__RESET_LINK__", resetLink)
            .replace("__EXPIRATION__", String.valueOf(expirationMinutes));
    }

    private Long getEmailContactTypeId() {
        List<CatalogueValueResponse> contactTypes = sharedServiceClient.getValuesByType("CONTACT_TYPE");
        return contactTypes.stream()
            .filter(ct -> "EMAIL".equals(ct.getCode()))
            .findFirst()
            .map(CatalogueValueResponse::getCatalogueValueId)
            .orElseThrow(() -> new RuntimeException("EMAIL contact type not found in catalogue CONTACT_TYPE"));
    }
}
