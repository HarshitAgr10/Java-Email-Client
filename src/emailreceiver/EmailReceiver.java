package emailreceiver;

import javax.mail.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmailReceiver {

    // Static fields for storing email credentials
    private static String username = "";
    private static String password = "";

    // Method to get credentials
    public static void setCredentials(String user, String pass) {
        username = user;
        password = pass;
    }

    // Method to receive limited number of emails from a Gmail account
    public static Message[] receiveEmail(int limit) throws MessagingException, IOException {
        // Set properties
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imaps.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");

        List<Message> messageList = new ArrayList<Message>();

        // Get the Session object
        Session emailSession = Session.getInstance(properties);

        // Create the IMAP store object and connect to the mail server
        Store store = emailSession.getStore("imaps");
        store.connect("imap.gmail.com", username, password);

        // Open the inbox folder from store in READ-ONLY mode
        Folder emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);

        // Fetch messages from the server
        Message[] messages = emailFolder.getMessages();
        for (int i = 0; i < Math.min(limit, messages.length); i++) {
            messageList.add(messages[i]);
        }
//        for (Message message : messages) {
//            messageList.add(message);
//        }

        // Close the store and folder objects
        emailFolder.close(false);
        store.close();

        return messageList.toArray(new Message[0]);
    }
}
