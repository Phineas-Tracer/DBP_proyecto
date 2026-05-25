package org.ide.dbp_proyecto.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final Resend resend;
    private final String fromEmail;

    public EmailService(
            @Value("${resend.api-key}") String apiKey,
            @Value("${resend.from-email}") String fromEmail) {
        this.resend = new Resend(apiKey);
        this.fromEmail = fromEmail;
    }

    /**
     * Email de bienvenida al registrarse.
     */
    @Async
    public void enviarEmailBienvenida(String emailDestino, String nombreUsuario) {
        String html = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">
                    <h1 style="color: #2c5f2d;">¡Bienvenido a Planificador de Rutas, %s!</h1>
                    <p>Tu cuenta ha sido creada exitosamente.</p>
                    <p>Ahora puedes:</p>
                    <ul>
                        <li>Explorar rutas de trekking cercanas a Lima</li>
                        <li>Planificar tus aventuras con puntos de interés</li>
                        <li>Coleccionar lugares visitados en tu álbum digital</li>
                        <li>Desbloquear recompensas por completar retos</li>
                    </ul>
                    <p>¡Disfruta de la naturaleza!</p>
                </body>
                </html>
                """.formatted(escapeHtml(nombreUsuario));

        enviar(emailDestino, "¡Bienvenido a Planificador de Rutas!", html);
    }

    /**
     * Email de confirmación cuando el usuario hace check-in en un POI.
     */
    @Async
    public void enviarEmailCheckin(String emailDestino, String nombreLugar, String fechaVisita) {
        String html = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">
                    <h2 style="color: #2c5f2d;">¡Check-in confirmado! 📍</h2>
                    <p>Acabas de visitar <strong>%s</strong>.</p>
                    <p>Fecha: %s</p>
                    <p>Este lugar ha sido añadido a tu álbum digital.</p>
                    <p>¡Sigue explorando para desbloquear más recompensas!</p>
                </body>
                </html>
                """.formatted(escapeHtml(nombreLugar), escapeHtml(fechaVisita));

        enviar(emailDestino, "Check-in en " + nombreLugar, html);
    }

    /** Helper privado que hace la llamada real a Resend. */
    private void enviar(String to, String subject, String html) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Email enviado a {} (id={})", to, response.getId());
        } catch (ResendException e) {
            // No relanzamos — si falla el email, no debe romper el flujo principal
            log.error("Error enviando email a {}: {}", to, e.getMessage());
        }
    }

    /** Escape básico de HTML para prevenir inyección en los placeholders. */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}