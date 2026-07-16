package com.project.emprendia.event.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendInvitationEmail(String to, String eventName, String eventDescription,
                                    String eventDate, String entrepreneurshipName,
                                    String message, Long invitationId) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Invitación a " + eventName);

            String html = buildInvitationHtml(eventName, eventDescription, eventDate,
                entrepreneurshipName, message, invitationId);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info("Invitación enviada a {} para el evento {}", to, eventName);
        } catch (MessagingException e) {
            log.error("Error al enviar correo a {}: {}", to, e.getMessage());
            throw new RuntimeException("Error al enviar correo de invitación", e);
        }
    }

    public void sendRejectionEmail(String to, String eventName, String entrepreneurshipName, String reason) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Invitación rechazada - " + eventName);

            String html = buildRejectionHtml(eventName, entrepreneurshipName, reason);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info("Correo de rechazo enviado a {} para el evento {}", to, eventName);
        } catch (MessagingException e) {
            log.error("Error al enviar correo de rechazo a {}: {}", to, e.getMessage());
            throw new RuntimeException("Error al enviar correo de rechazo", e);
        }
    }

    private String buildInvitationHtml(String eventName, String eventDescription,
                                       String eventDate, String entrepreneurshipName,
                                       String message, Long invitationId) {
        String desc = eventDescription != null ? eventDescription : "Sin descripción";
        String date = eventDate != null ? eventDate : "Por definir";

        String msgBlock = "";
        if (message != null && !message.isBlank()) {
            msgBlock = "<div class=\"message-box\"><strong>Mensaje:</strong><p>" + message + "</p></div>";
        }

        String inviteUrl = frontendUrl + "/invitations/" + invitationId + "/accept";

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
                    .event-details { background: #f8f9fa; border-radius: 4px; padding: 20px; margin: 20px 0; border: 1px solid #e9ecef; }
                    .event-details h3 { margin: 0 0 8px; color: #667eea; font-size: 18px; }
                    .event-details p { margin: 4px 0; color: #555; font-size: 14px; }
                    .message-box { background: #f0f4ff; border-left: 4px solid #667eea; padding: 15px 20px; margin: 20px 0; border-radius: 4px; }
                    .message-box p { margin: 5px 0 0; color: #444; }
                    .actions { text-align: center; margin: 30px 0 10px; }
                    .btn-primary { display: inline-block; padding: 14px 40px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; text-decoration: none; border-radius: 4px; font-weight: 600; font-size: 15px; letter-spacing: 0.3px; }
                    .btn-primary:hover { opacity: 0.9; }
                    .footer { padding: 20px 30px; text-align: center; color: #999; font-size: 12px; border-top: 1px solid #eee; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>!Has sido invitado!</h1>
                    </div>
                    <div class="content">
                        <p>Hola,</p>
                        <p>Tu emprendimiento <strong>__ENTREPRENEURSHIP__</strong> ha sido invitado a participar en el siguiente evento:</p>
                        <div class="event-details">
                            <h3>__EVENT_NAME__</h3>
                            <p><strong>Descripcion:</strong> __DESCRIPTION__</p>
                            <p><strong>Fecha:</strong> __DATE__</p>
                        </div>
                        __MESSAGE__
                        <div class="actions">
                            <a class="btn-primary" href="__INVITE_URL__">Aceptar invitacion</a>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Este es un correo generado automaticamente. Por favor no responder.</p>
                        <p>&copy; 2026 Emprendia. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """
            .replace("__ENTREPRENEURSHIP__", entrepreneurshipName)
            .replace("__EVENT_NAME__", eventName)
            .replace("__DESCRIPTION__", desc)
            .replace("__DATE__", date)
            .replace("__MESSAGE__", msgBlock)
            .replace("__INVITE_URL__", inviteUrl);
    }

    private String buildRejectionHtml(String eventName, String entrepreneurshipName, String reason) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f0f2f5; }
                    .container { max-width: 600px; margin: 30px auto; background: #ffffff; border-radius: 4px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
                    .header { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; padding: 35px 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 26px; font-weight: 600; }
                    .content { padding: 35px 30px; }
                    .content p { line-height: 1.7; color: #444; margin: 0 0 15px; }
                    .reason-box { background: #fdf0ef; border-left: 4px solid #e74c3c; padding: 15px 20px; margin: 20px 0; border-radius: 4px; }
                    .reason-box p { margin: 5px 0 0; color: #444; }
                    .footer { padding: 20px 30px; text-align: center; color: #999; font-size: 12px; border-top: 1px solid #eee; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Invitacion Rechazada</h1>
                    </div>
                    <div class="content">
                        <p>Hola <strong>__ENTREPRENEURSHIP__</strong>,</p>
                        <p>Lamentamos informarte que tu invitacion para participar en el evento <strong>__EVENT_NAME__</strong> ha sido rechazada.</p>
                        <div class="reason-box">
                            <strong>Motivo:</strong>
                            <p>__REASON__</p>
                        </div>
                        <p>Si tienes alguna duda, contacta al organizador del evento.</p>
                    </div>
                    <div class="footer">
                        <p>Este es un correo generado automaticamente. Por favor no responder.</p>
                        <p>&copy; 2026 Emprendia. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """
            .replace("__ENTREPRENEURSHIP__", entrepreneurshipName)
            .replace("__EVENT_NAME__", eventName)
            .replace("__REASON__", reason);
    }
}
