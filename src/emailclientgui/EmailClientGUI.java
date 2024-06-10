package emailclientgui;

import emailsessionmanager.EmailSessionManager;
import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class EmailClientGUI extends JFrame {

    // Text fields for user to input username and password
    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);

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

        // Inbox Panel
        DefaultListModel<String> emailListModel = new DefaultListModel<>();   // To hold list of email subjects
        JList<String> emailList = new JList<>(emailListModel);    // To display list of emails using email list model
        add(new JScrollPane(emailList), BorderLayout.WEST);      // Add email list to scroll pane and to west of layout

        // Reading Panel
        JTextArea emailContent = new JTextArea();   // A test area for displaying content of selected email
        emailContent.setEditable(false);            // Make the text area non-editable(read-only)
        add(new JScrollPane(emailContent), BorderLayout.CENTER); // Add text area to scroll pane and to center of layout

        // Compose Button
        JButton composeButton = new JButton("Compose");  // Button for composing new emails
        add(composeButton, BorderLayout.SOUTH);     // Add compose button to south side of layout

        // Schedule showLoginDialog() to be run on Event Dispatch thread,
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
