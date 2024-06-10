package emailclientgui;

import emailsessionmanager.EmailSessionManager;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class EmailClientGUI extends JFrame {

    // Text fields for user to input username and password
    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private DefaultListModel<String> emailListModel;   // Model for the email list
    private JList<String> emailList;   // Component to display emails
    private JTextArea emailContent = new JTextArea();
    private Message[] messages;    // Array to hold fetched messages

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
//        DefaultListModel<String> emailListModel = new DefaultListModel<>();   // To hold list of email subjects
//        JList<String> emailList = new JList<>(emailListModel);    // To display list of emails using email list model
//        add(new JScrollPane(emailList), BorderLayout.WEST);      // Add email list to scroll pane and to west of layout
//
//        // Reading Panel
//        JTextArea emailContent = new JTextArea();   // A test area for displaying content of selected email
//        emailContent.setEditable(false);            // Make the text area non-editable(read-only)
//        add(new JScrollPane(emailContent), BorderLayout.CENTER); // Add text area to scroll pane and to center of layout
//
//        // Compose Button
//        JButton composeButton = new JButton("Compose");  // Button for composing new emails
//        add(composeButton, BorderLayout.SOUTH);     // Add compose button to south side of layout

        // Schedule showLoginDialog() to be invoked on Event Dispatch thread,
        // ensuring that login dialog is shown after UI components are initialized
        SwingUtilities.invokeLater((this::showLoginDialog));
    }

    // Method to refresh the inbox and update the email list
    private void refreshBox() {
        try {
            // Fetch emails from the EmailSessionManager instance
            messages = EmailSessionManager.getInstance().receiveEmail();
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
}
