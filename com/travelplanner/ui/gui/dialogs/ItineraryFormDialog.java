package com.travelplanner.ui.gui.dialogs;
import com.travelplanner.model.ItineraryItem;
import com.travelplanner.service.ItineraryService;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
public class ItineraryFormDialog extends JDialog {
    private final int tripId;
    private final ItineraryService itineraryService;
    private final ItineraryItem existingItem;
    private final Runnable onSaved;
    private JSpinner daySpinner;
    private JTextArea activityArea;
    public ItineraryFormDialog(
            Frame owner,
            int tripId,
            ItineraryService itineraryService,
            ItineraryItem existingItem,
            Runnable onSaved
    ) {
        super(owner, existingItem == null ? "Add Itinerary Item" : "Edit Itinerary Item", true);
        this.tripId = tripId;
        this.itineraryService = itineraryService;
        this.existingItem = existingItem;
        this.onSaved = onSaved;
        build(owner);
    }
    private void build(Frame owner) {
        setSize(520, 360);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        JLabel title = GuiUtil.pageTitle(existingItem == null ? "🗓 Add Itinerary" : "✏ Edit Itinerary");
        title.setBorder(BorderFactory.createEmptyBorder(18, 22, 8, 22));
        add(title, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        GridBagConstraints gbc = GuiUtil.gbc();
        daySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        activityArea = new JTextArea(5, 30);
        activityArea.setLineWrap(true);
        activityArea.setWrapStyleWord(true);
        if (existingItem != null) {
            daySpinner.setValue(existingItem.getDayNumber());
            activityArea.setText(existingItem.getActivity());
        }
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Day Number"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(daySpinner, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        form.add(new JLabel("Activity"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(activityArea, gbc);
        add(form, BorderLayout.CENTER);
        JPanel actions = new JPanel();
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = GuiUtil.primaryButton("Save");
        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> save());
        actions.add(cancelButton);
        actions.add(saveButton);
        add(actions, BorderLayout.SOUTH);
    }
    private void save() {
        try {
            int day = (Integer) daySpinner.getValue();
            String activity = activityArea.getText();
            if (existingItem == null) {
                itineraryService.addItineraryItem(tripId, day, activity);
            } else {
                itineraryService.updateItineraryItem(existingItem.getId(), day, activity);
            }
            onSaved.run();
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}