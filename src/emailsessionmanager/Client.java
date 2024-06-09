package emailsessionmanager;

import javax.mail.Message;

public class Client {
    public static void main(String[] args) {
        try {
            // Initialize the email session with credentials
            EmailSessionManager emailSessionManager = EmailSessionManager
                    .getInstance("harshithrs1710@gmail.com", "gqgljlwgwvgyieuc");

            // Fetch emails from the inbox
            Message[] messages = emailSessionManager.receiveEmail();
            for (Message message : messages) {
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());
            }

            // Close the session when done
            emailSessionManager.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
