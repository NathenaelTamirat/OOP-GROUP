

// Main entry point; starts the GUI with LoginFrame.

import ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}