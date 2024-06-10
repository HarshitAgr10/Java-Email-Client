package emailclientgui;

import attachmentchooser.AttachmentChooser;
import emailsender.EmailSenderWithAttachment;
import emailsessionmanager.EmailSessionManager;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


public class EmailClientGUI extends JFrame {

    // Text fields for user to input username and password
    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private DefaultListModel<String> emailListModel = new DefaultListModel<>();   // Model for the email list
    private JList<String> emailList = new JList<>(emailListModel);   // Component to display emails
    private JTextArea emailContent = new JTextArea();
    private Message[] messages;    // Array to hold fetched messages
    private static final int EMAIL_FETCH_LIMIT = 10;   // Limit the number of emails to fetch

    // Constructor for EmailClientGUI class
    public EmailClientGUI() {
        setTitle("Java Email Client");       // Set the title of the window
        setSize(800, 600);      // Set the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Set default close operation(Exit app when window is closed)
        initUI();
        setVisible(true);                   // Make the window visible

        // Add window listener to handle application close (Logout Functionality)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    // Check if EmailSessionManager instance is initialized
                    if (EmailSessionManager.getInstance() != null) {
                        // CLose the email session
                        EmailSessionManager.getInstance().close();
                    }
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // Method to initialize user interface components
    private void initUI() {

        // JSplitPane to split two component horizontally, weight of split 0.5 to evenly divide space
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);   // To expand or collapse split pane with one touch

        // Set selection mode of email list to single selection, allowing only one email to be selected at a time
        emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Add listSelectionListener to email list that triggers emailListSelectionChanged() whenever email is selected
        emailList.addListSelectionListener(this::emailListSelectionChanged);

        // To wrap email list in a scroll pane, allowing list to be scrollable
        JScrollPane listScrollPane = new JScrollPane(emailList);
        emailContent.setEditable(false);     // Make email content text area read only

        // To wrap email content text area in a scroll pane, making content area scrollable
        JScrollPane contentScrollPane = new JScrollPane(emailContent);
        splitPane.setLeftComponent(listScrollPane);
        splitPane.setRightComponent(contentScrollPane);
        // Add split pane to the center of JFrame's content pane, filling the available space
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Inbox Panel
        // DefaultListModel<String> emailListModel = new DefaultListModel<>();   // To hold list of email subjects
        // JList<String> emailList = new JList<>(emailListModel);    // To display list of emails using email list model
        // add(new JScrollPane(emailList), BorderLayout.WEST);      // Add email list to scroll pane and to west of layout

        // Reading Panel
        // JTextArea emailContent = new JTextArea();   // A test area for displaying content of selected email
        // emailContent.setEditable(false);            // Make the text area non-editable(read-only)
        // add(new JScrollPane(emailContent), BorderLayout.CENTER); // Add text area to scroll pane and to center of layout

        // Compose Button
        JButton composeButton = new JButton("Compose");  // Button for composing new emails
        // add(composeButton, BorderLayout.SOUTH);     // Add compose button to south side of layout
        composeButton.addActionListener(e -> showComposeDialog());

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        JButton refreshInboxButton = new JButton("Refresh Box");
        // Add compose button and refresh inbox button to bottom panel
        bottomPanel.add(composeButton);
        bottomPanel.add(refreshInboxButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add action listener to refresh button to call refreshBox() when button is clicked
        refreshInboxButton.addActionListener(e -> refreshBox());

        // Schedule showLoginDialog() to be invoked on Event Dispatch thread,
        // ensuring that login dialog is shown after UI components are initialized
        SwingUtilities.invokeLater((this::showLoginDialog));
    }

    // Method to show login dialog (Login Functionality)
    private void showLoginDialog() {
        // Create a panel with a grid layout for the login dialog
        JPanel loginPanel = new JPanel(new GridLayout(0, 1));
        loginPanel.add(new JLabel("Email:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);

        // Show a confirmation dialog with login panel, OK and Cancel options
        int result = JOptionPane.showConfirmDialog(null, loginPanel,
                "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            // If OK is selected, retrieve the entered username and password
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                // Initialize EmailSessionManager with provided username and password
                EmailSessionManager.getInstance(username, password);
                System.out.println("Login successful with username: " + username);
                refreshBox();          // Refresh inbox to load emails
            } catch (MessagingException e)
            {
                // Show an error message if email session initialization fails
                JOptionPane.showMessageDialog(this,
                        "Failed to initialize email session:" + e.getMessage(),
                        "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Print to console if login is cancelled
            System.out.println("Login cancelled");
        }
    }

    // Method to refresh the inbox and update the email list
    private void refreshBox() {
        try {
            // Fetch a limited number of emails from the EmailSessionManager instance
            messages = EmailSessionManager.getInstance().receiveEmail(EMAIL_FETCH_LIMIT);
            System.out.println("Fetched " + messages.length + " emails.");
            emailListModel.clear();    // Clear existing content in email list model

            for (Message message : messages) {
                // Add each email's subject and sender information to the email list model
                emailListModel.addElement(message.getSubject() + " - From: "
                        + InternetAddress.toString(message.getFrom()));
            }
        } catch (MessagingException e) {
            // Show an error message dialog if fetching emails fails
            JOptionPane.showMessageDialog(this, "Failed to fetch emails: "
                    + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to handle changes in the email list selection
    private void emailListSelectionChanged(ListSelectionEvent e) {
        // Check if value is adjusting(i.e. if selection is being updated) and if an email is selected
        if (!e.getValueIsAdjusting() && emailList.getSelectedIndex() != -1) {
            try {
                // Get selected email message from messages[] based on selected index
                Message selectedMessage = messages[emailList.getSelectedIndex()];
                System.out.println("Selected email: " + selectedMessage.getSubject());
                emailContent.setText("");     // Clear previous content in email content text area
                emailContent.append("Subject: " + selectedMessage.getSubject() + "\n\n");
                emailContent.append("From: " + InternetAddress
                        .toString(selectedMessage.getFrom()) + "\n\n");
                emailContent.append(getTextFromMessage(selectedMessage));
            } catch (MessagingException | IOException ex) {
                // If there is error reading email content, display an error message in email content text area
                emailContent.setText("Error reading email content: " + ex.getMessage());
            }
        }
    }

    // Method to extract the text content from a given email message
    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        // Check if message content type is plain text
        if (message.isMimeType("text/plain")) {
            String content = (String) message.getContent();   // Return plain text content of message
            System.out.println("Plain text content: " + content);
            return content;
        } else if (message.isMimeType("multipart/*")) {
            // If message content type is multipart, cast it to MimeMultipart
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    String content = (String) bodyPart.getContent();
                    System.out.println("Multipart text content: " + content);
                    return content;
                }
            }
        }
        return "No readable content found";
    }

    // Method to show the compose email dialog
    private void showComposeDialog() {
        JDialog composeDialog = new JDialog(this, "Compose Email", true);
        composeDialog.setLayout(new BorderLayout(5, 5));

        // Create a vertical box to hold the fields for the email
        Box fieldsPanel = Box.createVerticalBox();
        JTextField toField = new JTextField();      // Text field for recipient's email address
        JTextField subjectField = new JTextField();  // Text field for the email subject

        JTextArea bodyArea = new JTextArea(10, 20);   // Text area for email body
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);    // Wrap at word boundaries

        // Add labels and text fields to fields panel
        fieldsPanel.add(new JLabel("To: "));
        fieldsPanel.add(toField);
        fieldsPanel.add(new JLabel("Subject: "));
        fieldsPanel.add(subjectField);

        // Create a panel for buttons at the bottom of dialog
        JPanel bottomPanel = new JPanel();
        JButton attachButton = new JButton("Attach Files");
        JButton sendButton = new JButton("Send");
        JLabel attachedFilesLabel = new JLabel("No files attached");

        // List to hold the attached files
        List<File> attachedFiles = new ArrayList<>();
        // Add action listener to attach button to choose files and update the label
        attachButton.addActionListener(e -> {
            File[] files = AttachmentChooser.chooseAttachments();
            attachedFiles.addAll(Arrays.asList(files));
            attachedFilesLabel.setText(attachedFiles.size() + " files attached");
        });

        // Add action listener to send button to send the email and close the dialog
        sendButton.addActionListener(e -> {
            String to = toField.getText();
            String subject = subjectField.getText();
            String body = bodyArea.getText();
            File[] attachments = attachedFiles.toArray(new File[0]);
            EmailSenderWithAttachment.sendEmailWithAttachment(to, subject, body, attachments);
            composeDialog.dispose();
        });

        // Add attach button and send button to the bottom panel
        bottomPanel.add(attachButton);
        bottomPanel.add(sendButton);

        composeDialog.add(fieldsPanel, BorderLayout.NORTH);
        composeDialog.add(new JScrollPane(bodyArea), BorderLayout.CENTER);  // Add text area for email body
        composeDialog.add(bottomPanel, BorderLayout.SOUTH);   // Add bottom panel with buttons

        composeDialog.pack();    // Pack dialog to fit its components
        composeDialog.setLocationRelativeTo(this);   // Center dialog relative to main window
        composeDialog.setVisible(true);    // Make dialog visible
    }
}
