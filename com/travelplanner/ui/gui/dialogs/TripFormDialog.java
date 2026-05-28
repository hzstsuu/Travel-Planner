package com.travelplanner.ui.gui.dialogs;

import com.travelplanner.model.Trip;
import com.travelplanner.service.SessionManager;
import com.travelplanner.service.TripService;
import com.travelplanner.ui.gui.util.DateSpinner;
import com.travelplanner.ui.gui.util.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class TripFormDialog extends JDialog {

    private final TripService tripService;
    private final Trip        existing;
    private final Runnable    onSaved;

    private JTextField  destinationField;
    private DateSpinner startSpinner;
    private DateSpinner endSpinner;
    private JTextArea   descArea;
    private JTextArea   notesArea;

    public TripFormDialog(JFrame owner, TripService tripService,
                          Trip existing, Runnable onSaved) {
        super(owner, existing == null ? "New Trip" : "Edit Trip", true);
        this.tripService = tripService;
        this.existing    = existing;
        this.onSaved     = onSaved;
        build(owner);
    }

    private void build(JFrame owner) {
        setSize(500, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JLabel title = GuiUtil.pageTitle(existing == null ? "🧳 New Trip" : "✏ Edit Trip");
        title.setBorder(BorderFactory.createEmptyBorder(18, 22, 8, 22));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        GridBagConstraints g = GuiUtil.gbc();

        destinationField = new JTextField();
        startSpinner     = new DateSpinner(LocalDate.now());
        endSpinner       = new DateSpinner(LocalDate.now().plusDays(7));
        descArea         = new JTextArea(3, 20);
        descArea.setLineWrap(true); descArea.setWrapStyleWord(true);
        notesArea        = new JTextArea(3, 20);
        notesArea.setLineWrap(true); notesArea.setWrapStyleWord(true);

        if (existing != null) {
            destinationField.setText(existing.getDestination());
            startSpinner.setDate(existing.getStartDate());
            endSpinner.setDate(existing.getEndDate());
            descArea.setText(existing.getDescription());
            notesArea.setText(existing.getNotes());
        }

        addRow(form, g, 0, "Destination *", destinationField);
        addRow(form, g, 1, "Start Date",    startSpinner);
        addRow(form, g, 2, "End Date",      endSpinner);
        addRow(form, g, 3, "Description",   new JScrollPane(descArea));
        if (existing != null) addRow(form, g, 4, "Notes", new JScrollPane(notesArea));

        add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        JButton cancel = new JButton("Cancel");
        JButton save   = GuiUtil.primaryButton("Save");
        cancel.addActionListener(e -> dispose());
        save.addActionListener(e -> save());
        actions.add(cancel);
        actions.add(save);
        add(actions, BorderLayout.SOUTH);
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String lbl, Component comp) {
        g.gridx = 0; g.gridy = row; g.weightx = 0;
        p.add(new JLabel(lbl), g);
        g.gridx = 1; g.weightx = 1;
        p.add(comp, g);
    }

    private void save() {
        try {
            String dest  = destinationField.getText().trim();
            LocalDate s  = startSpinner.getDate();
            LocalDate e  = endSpinner.getDate();
            String desc  = descArea.getText();
            String notes = notesArea != null ? notesArea.getText() : "";

            if (existing == null) {
                tripService.createTrip(SessionManager.currentUserId(), dest, s, e, desc);
            } else {
                tripService.updateTrip(existing.getId(), dest, s, e, desc, notes);
            }
            onSaved.run();
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}