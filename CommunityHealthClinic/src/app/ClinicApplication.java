package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.Clinic;
import view.MainMenuFrame;

/** Application entry point. Bootstraps the MVC stack. */
public final class ClinicApplication {

    private ClinicApplication() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }

            Clinic clinic = new Clinic();

            try {
                clinic.loadAll();
            } catch (Exception ex) {
                System.err.println("Warning: existing data could not be loaded - "
                        + ex.getMessage());
            }
            // Ensure an administrator always exists (runs even if loadAll failed).
            clinic.seedDefaultAdministrator();

            MainMenuFrame frame = new MainMenuFrame(clinic);
            frame.setVisible(true);
            frame.setStatus("Application started. Data folder: " + clinic.getDataFolder());
        });
    }
}