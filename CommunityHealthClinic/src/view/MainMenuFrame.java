package view;

import controller.AppointmentController;
import controller.DoctorController;
import controller.PatientController;
import controller.ReportController;
import controller.TreatmentController;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.Clinic;

/** Main window of the application (View Layer). */
public class MainMenuFrame extends JFrame {

    public static final String CARD_HOME = "HOME";
    public static final String CARD_PATIENT = "Register Patient";
    public static final String CARD_DOCTOR = "Register Doctor";
    public static final String CARD_APPOINTMENT = "Appointment Booking";
    public static final String CARD_TREATMENT = "Treatment Entry";
    public static final String CARD_SEARCH = "Search";
    public static final String CARD_REPORTS = "Reports";

    private final Clinic clinic;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final JLabel statusBar = new JLabel(" Ready");
    private final Map<String, JComponent> cards = new HashMap<>();

    private final PatientController patientController;
    private final DoctorController doctorController;
    private final AppointmentController appointmentController;
    private final TreatmentController treatmentController;
    private final ReportController reportController;

    public MainMenuFrame(Clinic clinic) {
        this.clinic = clinic;
        this.patientController = new PatientController(clinic);
        this.doctorController = new DoctorController(clinic);
        this.appointmentController = new AppointmentController(clinic);
        this.treatmentController = new TreatmentController(clinic);
        this.reportController = new ReportController(clinic);

        setTitle("Community Health Clinic Management System - BN231 Assignment 2");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 720));

        buildCards();

        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        setJMenuBar(buildMenuBar());
        add(cardPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { exitApplication(); }
        });

        setLocationRelativeTo(null);
        showCard(CARD_HOME);
    }

    private void buildCards() {
        registerCard(CARD_HOME, new HomePanel(this));
        registerCard(CARD_PATIENT, new RegisterPatientPanel(patientController));
        registerCard(CARD_DOCTOR, new RegisterDoctorPanel(doctorController));
        registerCard(CARD_APPOINTMENT, new AppointmentPanel(appointmentController));
        registerCard(CARD_TREATMENT, new TreatmentPanel(treatmentController));
        registerCard(CARD_SEARCH, new SearchPanel(patientController, appointmentController));
        registerCard(CARD_REPORTS, new ReportsPanel(reportController, doctorController));
    }

    private void registerCard(String key, JComponent component) {
        cards.put(key, component);
        cardPanel.add(component, key);
    }

    public void showCard(String key) {
        cardLayout.show(cardPanel, key);
        JComponent component = cards.get(key);
        if (component instanceof Refreshable) ((Refreshable) component).refreshData();
        setStatus("Viewing: " + key);
    }

    public void setStatus(String message) { statusBar.setText(" " + message); }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(menuItem("Save Data", e -> saveData(true)));
        fileMenu.add(menuItem("Load Data", e -> loadData(true)));
        fileMenu.addSeparator();
        fileMenu.add(menuItem("Exit", e -> exitApplication()));
        bar.add(fileMenu);

        JMenu navMenu = new JMenu("Navigate");
        navMenu.add(menuItem("Home", e -> showCard(CARD_HOME)));
        navMenu.add(menuItem("Register Patient", e -> showCard(CARD_PATIENT)));
        navMenu.add(menuItem("Register Doctor", e -> showCard(CARD_DOCTOR)));
        navMenu.add(menuItem("Appointment Booking", e -> showCard(CARD_APPOINTMENT)));
        navMenu.add(menuItem("Treatment Entry", e -> showCard(CARD_TREATMENT)));
        navMenu.add(menuItem("Search", e -> showCard(CARD_SEARCH)));
        navMenu.add(menuItem("Reports", e -> showCard(CARD_REPORTS)));
        bar.add(navMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(menuItem("About", e -> showAbout()));
        bar.add(helpMenu);

        return bar;
    }

    private JMenuItem menuItem(String text, ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(listener);
        return item;
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Community Health Clinic Management System\n"
                        + "BN231 - Software Development Skills and Tools\n"
                        + "Assignment 2 - MVC Desktop Application\n\n"
                        + "Data folder: " + clinic.getDataFolder(),
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean saveData(boolean showConfirmation) {
        try {
            clinic.saveAll();
            setStatus("Data saved to '" + clinic.getDataFolder() + "'.");
            if (showConfirmation)
                JOptionPane.showMessageDialog(this,
                        "All clinic data was saved successfully.",
                        "Save Complete", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save data:\n" + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            setStatus("Save failed: " + ex.getMessage());
            return false;
        }
    }

    private void loadData(boolean showConfirmation) {
        try {
            clinic.loadAll();
            clinic.seedDefaultAdministrator();
            for (JComponent component : cards.values()) {
                if (component instanceof Refreshable) ((Refreshable) component).refreshData();
            }
            setStatus("Data loaded from '" + clinic.getDataFolder() + "'.");
            if (showConfirmation)
                JOptionPane.showMessageDialog(this,
                        "Clinic data was loaded successfully.",
                        "Load Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load data:\n" + ex.getMessage(),
                    "Load Error", JOptionPane.ERROR_MESSAGE);
            setStatus("Load failed: " + ex.getMessage());
        }
    }

    private void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Save clinic data before exiting?",
                "Exit Application",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) return;
        if (choice == JOptionPane.YES_OPTION && !saveData(false)) return;
        dispose();
        System.exit(0);
    }

    public Clinic getClinic() { return clinic; }
}