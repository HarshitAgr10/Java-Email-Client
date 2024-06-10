package emailsessionmanager;

import javax.mail.*;
import java.util.Arrays;
import java.util.Properties;

// Singleton EmailSessionManager class to manage email sessions for connecting to, fetching
// emails from and disconnecting from an IMAP email server
public class EmailSessionManager {
    private Session emailSession;
    private Store store;
    private Folder emailFolder;
    private static EmailSessionManager instance;   // Holds the singleton instance of EmailSessionManager

    // Add static fields to store current username and password
    private static String currentUsername = "";
    private static String currentPassword = "";

    // Private Constructor to enforce a singleton pattern
    private EmailSessionManager(String username, String password) throws MessagingException {
        // Set properties for the mail session
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");

        this.emailSession = Session.getInstance(properties, null);   // Initialize email session
        // Get the store and connect to the mail server
        this.store = emailSession.getStore("imaps");
        this.store.connect(username, password);

        // Store the credentials upon successful connection
        currentUsername = username;
        currentPassword = password;
    }

    // Method to get the single instance of EmailSessionManager (Singleton pattern)
    public static EmailSessionManager getInstance(String username, String password) throws MessagingException {
        // Create a new instance if it doesn't exist
        if (instance == null) {
            instance = new EmailSessionManager(username, password);
        }
        return instance;
    }

    // Method to get the existing instance (throws exception if not initialized)
    public static EmailSessionManager getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("EmailSessionManager has not been initialized yet. Please login first");
        }
        return instance;
    }

    // Method to retrieve the current username
    public static String getUsername() {
        return currentUsername;
    }

    // Method to retrieve the current password
    public static String getPassword() {
        return currentPassword;
    }

    // Method for fetching emails from the inbox (Passing limit variable to fetch limited number of emails)
    public Message[] receiveEmail(int limit) throws MessagingException {
        // Open the inbox folder if it's not already open
        if (emailFolder == null || !emailFolder.isOpen()) {
            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);   // Open in read-only mode
        }
        Message[] messages = emailFolder.getMessages();
        return Arrays.copyOfRange(messages, 0, Math.min(limit, messages.length));   // Return the messages from inbox
    }

    // Method to properly close the emailFolder and store
    public void close() throws MessagingException {
        // Close the email folder if it's open
        if (emailFolder != null) {
            emailFolder.close(false);
            emailFolder = null;
        }

        // Close the store if it's connected
        if (store != null) {
            store.close();
            store = null;
        }

        instance = null;   // Reset the singleton instance

        // Clear the credentials upon closing the session
        currentUsername = "";
        currentPassword = "";
    }
}
