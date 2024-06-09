package attachmentchooser;

import javax.swing.*;
import java.io.File;

public class AttachmentChooser {

    // Method to choose multiple attachments
    public static File[] chooseAttachments() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);

        // Show the open dialog and get the user's selection
        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFiles();    // Return selected files
        }

        return new File[] {};    // Return an empty array if no selection
    }
}
