package emailclientgui;

import javax.swing.*;

public class Client {
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread: creating and showing the GUI
        SwingUtilities.invokeLater(() -> new EmailClientGUI());
    }
}
