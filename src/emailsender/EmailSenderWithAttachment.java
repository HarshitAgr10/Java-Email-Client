package emailsender;

import emailsessionmanager.EmailSessionManager;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class EmailSenderWithAttachment {
    public static void sendEmailWithAttachment(String to, String subject, String body, File[] attachments) {
        try {
            // Get the username and password dynamically from the EmailSessionManager
            String username = EmailSessionManager.getUsername();
            String password = EmailSessionManager.getPassword();

            // Setup properties for the mail session
            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");    // SMTP server for Gmail
            prop.put("mail.smtp.port", "587");               // Port for TLS
            prop.put("mail.smtp.auth", "true");              // Enable authentication
            prop.put("mail.smtp.starttls.enable", "true");   // Enable STARTTLS

            // Create a Session object with authentication
            Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                // Create a MimeMessage object
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));    // Sender's email
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));   // Recipient's email
                message.setSubject(subject);      // Email subject

                Multipart multipart = new MimeMultipart();
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(body);      // Email body text
                multipart.addBodyPart(textPart);   // Add the text part

                // Add each attachment
                for (File file : attachments) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(file);
                    multipart.addBodyPart(attachmentPart);   // Add attachment part to multipart
                }

                // Set complete multipart content as the message's content
                message.setContent(multipart);

                // Send the email
                Transport.send(message);
                System.out.println("Email sent successfully via Gmail with attachments");

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
