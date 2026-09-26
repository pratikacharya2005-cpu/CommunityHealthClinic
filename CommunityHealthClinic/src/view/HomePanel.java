package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Landing screen offering quick navigation to every module. */
public class HomePanel extends JPanel {

    public HomePanel(MainMenuFrame frame) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Community Health Clinic Management System",
                SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(3, 2, 18, 18));
        buttons.setBorder(BorderFactory.createEmptyBorder(45, 90, 45, 90));
        buttons.add(navButton("Register Patient", frame, MainMenuFrame.CARD_PATIENT));
        buttons.add(navButton("Register Doctor", frame, MainMenuFrame.CARD_DOCTOR));
        buttons.add(navButton("Appointment Booking", frame, MainMenuFrame.CARD_APPOINTMENT));
        buttons.add(navButton("Treatment Entry", frame, MainMenuFrame.CARD_TREATMENT));
        buttons.add(navButton("Search Records", frame, MainMenuFrame.CARD_SEARCH));
        buttons.add(navButton("Reports", frame, MainMenuFrame.CARD_REPORTS));
        add(buttons, BorderLayout.CENTER);

        JLabel footer = new JLabel(
                "BN231 Assignment 2  |  MVC Desktop Application  |  File > Save Data to persist records",
                SwingConstants.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private JButton navButton(String text, MainMenuFrame frame, String card) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.addActionListener(e -> frame.showCard(card));
        return button;
    }
}