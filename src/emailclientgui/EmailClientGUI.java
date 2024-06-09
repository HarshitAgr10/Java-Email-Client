package emailclientgui;

import javax.swing.*;
import java.awt.*;

public class EmailClientGUI extends JFrame {

    // Constructor for EmailClientGUI class
    public EmailClientGUI() {
        setTitle("Java Email Client");       // Set the title of the window
        setSize(800, 600);      // Set the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Set default close operation(Exit app when window is closed)
        initUI();
        setVisible(true);                   // Make the window visible
    }

    // Method to initialize user interface components
    private void initUI() {

        // Inbox Panel
        DefaultListModel<String> emailListModel = new DefaultListModel<>();   // To hold list of email subjects
        JList<String> emailList = new JList<>(emailListModel);    // To display list of emails using email list model
        add(new JScrollPane(emailList), BorderLayout.WEST);     // Add email list to scroll pane and to west of layout

        // Reading Panel
        JTextArea emailContent = new JTextArea();   // A test area for displaying content of selected email
        emailContent.setEditable(false);            // Make the text area non-editable(read-only)
        add(new JScrollPane(emailContent), BorderLayout.CENTER); // Add text area to scroll pane and to center of layout

        JButton composeButton = new JButton("Compose");  // Button for composing new emails
        add(composeButton, BorderLayout.SOUTH);   // Add compose button to south side of layout
    }
}
