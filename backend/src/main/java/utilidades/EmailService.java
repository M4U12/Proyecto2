package utilidades;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private static final String REMITENTE = "codenschool1@gmail.com";
    private static final String PASSWORD = "vrpqafxvcjgnzarm"; 

    public static void enviarClaveTemporal(String destinatario, String claveTemporal) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(REMITENTE));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject("Recuperación de Contraseña - Sistema Escolar CODEN SCHOOL");

        String contenidoHtml = "<h3>Hola,</h3>"
                + "<p>Has solicitado restablecer tu contraseña.</p>"
                + "<p>Tu clave temporal es: <strong>" + claveTemporal + "</strong></p>"
                + "<p>Por seguridad, te recomendamos cambiar esta contraseña en la opción 'Mi Perfil' una vez ingreses al sistema.</p>";

        message.setContent(contenidoHtml, "text/html; charset=utf-8");

        Transport.send(message);
    }
}
