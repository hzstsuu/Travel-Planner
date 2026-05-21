package com.travelplanner.ui.gui.dialogs;
import com.travelplanner.model.Trip;
import com.travelplanner.service.TripService;
import com.travelplanner.ui.gui.util.DateSpinner;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
public class TripFormDialog extends JDialog {
    private final TripService tripService;
    private final Trip existingTrip;
    private final Runnable onSaved;
    private JTextField destinationField;
    private DateSpinner startDateSpinner;
    private DateSpinner endDateSpinner;
    private JTextArea descriptionArea;
    private JTextArea notesArea;
    public TripFormDialog(Frame owner, TripService tripService, Trip existingTrip, Runnable onSaved) {
        super(owner, existingTrip == null ? "Create New Trip" : "Edit Trip", true);
        this.tripService = tripService;
        this.existingTrip = existingTrip;
        this.onSaved = onSaved;
        build(owner);
    }
    private void build(Frame owner) {
        setSize(560, 540);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        JLabel title = GuiUtil.pageTitle(existingTrip == null ? "🌍 New Trip" : "✏ Edit Trip");
        title.setBorder(BorderFactory.createEmptyBorder(18, 22, 8, 22));
        add(title, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        GridBagConstraints gbc = GuiUtil.gbc();
        destinationField = new JTextField();
        startDateSpinner = new DateSpinner(existingTrip == null ? LocalDate.now() : existingTrip.getStartDate());
        endDateSpinner = new DateSpinner(existingTrip == null ? LocalDate.now().plusDays(3) : existingTrip.getEndDate());
        descriptionArea = new JTextArea(4, 30);
        notesArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        if (existingTrip != null) {
            destinationField.setText(existingTrip.getDestination());
            descriptionArea.setText(existingTrip.getDescription());
            notesArea.setText(existingTrip.getNotes());
        }
        addRow(form, gbc, 0, "Destination", destinationField);
        addRow(form, gbc, 1, "Start Date", startDateSpinner);
        addRow(form, gbc, 2, "End Date", endDateSpinner);
        addRow(form, gbc, 3, "Description", new JScrollPane(descriptionArea));
        addRow(form, gbc, 4, "Notes", new JScrollPane(notesArea));
        add(form, BorderLayout.CENTER);
        JPanel actions = new JPanel();
        actions.setBorder(BorderFactory.createEmptyBorder(8, 22, 18, 22));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = GuiUtil.primaryButton(existingTrip == null ? "Create Trip" : "Save Changes");
        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> save());
        actions.add(cancelButton);
        actions.add(saveButton);
        add(actions, BorderLayout.SOUTH);
    }
    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }
    private void save() {
        try {
            String destination = destinationField.getText().trim();
            LocalDate start = startDateSpinner.getDate();
            LocalDate end = endDateSpinner.getDate();
            String description = descriptionArea.getText();
            String notes = notesArea.getText();
            if (existingTrip == null) {
                tripService.createTrip(destination, start, end, description);
            } else {
                tripService.updateTrip(existingTrip.getId(), destination, start, end, description, notes);
            }
            onSaved.run();
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}