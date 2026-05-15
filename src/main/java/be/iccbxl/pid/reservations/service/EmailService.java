package be.iccbxl.pid.reservations.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    /**
     * Envoie un email avec contenu HTML.
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            log.info("Email envoyé à {} (sujet : {})", to, subject);
        } catch (MessagingException e) {
            log.error("Échec d'envoi d'email à {} : {}", to, e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email", e);
        }
    }

    /**
     * Envoie l'email de réinitialisation de mot de passe.
     */
    public void sendPasswordResetEmail(String to, String firstname, String resetUrl) {
        String subject = "Réinitialisation de votre mot de passe";

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 20px auto; padding: 20px;">
                <h2 style="color: #333;">Bonjour %s,</h2>
                <p>Vous avez demandé la réinitialisation de votre mot de passe.</p>
                <p>Cliquez sur le bouton ci-dessous pour définir un nouveau mot de passe :</p>
                <p style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #0d6efd; color: white; padding: 12px 24px;
                       text-decoration: none; border-radius: 5px; display: inline-block;">
                        Réinitialiser mon mot de passe
                    </a>
                </p>
                <p>Ce lien est valable pendant <strong>1 heure</strong>.</p>
                <p>Si vous n'êtes pas à l'origine de cette demande, vous pouvez ignorer cet email.</p>
                <hr style="margin-top: 30px;">
                <p style="color: #888; font-size: 12px;">Projet Réservations — EPFC / ICC</p>
            </body>
            </html>
            """.formatted(firstname != null ? firstname : "utilisateur", resetUrl);

        sendHtmlEmail(to, subject, htmlContent);
    }
}