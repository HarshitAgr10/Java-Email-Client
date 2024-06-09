package emailreceiver;

import javax.mail.*;
import java.io.IOException;
import java.util.Properties;

public class EmailReceiver {
    public static void receiveEmail(String username, String password) {
        // Set properties
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imaps.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");

        try {
            // Get the Session object
            Session emailSession = Session.getDefaultInstance(properties);

            // Create the IMAP store object and connect to the mail server
            Store store = emailSession.getStore("imaps");
            store.connect("imap.gmail.com", username, password);

            // Open the inbox folder from store in READ-ONLY mode
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // Fetch messages from the server
            Message[] messages = emailFolder.getMessages();
            System.out.println("Number of emails: " + messages.length);

            // e.g. :- Print out subject of each email
            for (Message message : messages) {
                System.out.println("Email Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());
            }

            // Close the store and folder objects
            emailFolder.close(false);
            store.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }
}
