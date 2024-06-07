package emailsender;

public class Client {
    public static void main(String[] args) {
        String to = "harshithrs1710@gmail.com";
        String subject = "Test Email from Java App";
        String body = "This is a test email sent from the Java EmailSender class";

        EmailSender.sendEmail(to, subject, body);
    }
}
