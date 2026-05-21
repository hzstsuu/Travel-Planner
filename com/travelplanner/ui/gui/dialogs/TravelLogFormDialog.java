package com.travelplanner.ui.gui.dialogs;
import com.travelplanner.model.TravelLog;
import com.travelplanner.service.TravelLogService;
import com.travelplanner.ui.gui.util.DateSpinner;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
public class TravelLogFormDialog extends JDialog {
    private final int tripId;
    private final TravelLogService travelLogService;
    private final TravelLog existingLog;
    private final Runnable onSaved;
    private JTextArea reflectionArea;
    private JTextField imagePathField;
    private DateSpinner dateSpinner;
    public TravelLogFormDialog(
            Frame owner,
            int tripId,
            TravelLogService travelLogService,
            TravelLog existingLog,
            Runnable onSaved
    ) {
        super(owner, existingLog == null ? "Add Travel Log" : "Edit Travel Log", true);
        this.tripId = tripId;
        this.travelLogService = travelLogService;
        this.existingLog = existingLog;
        this.onSaved = onSaved;
        build(owner);
    }
    private void build(Frame owner) {
        setSize(620, 460);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        JLabel title = GuiUtil.pageTitle(existingLog == null ? "📔 New Diary Entry" : "✏ Edit Diary Entry");
        title.setBorder(BorderFactory.createEmptyBorder(18, 22, 8, 22));
        add(title, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        GridBagConstraints gbc = GuiUtil.gbc();
        reflectionArea = new JTextArea(6, 34);
        reflectionArea.setLineWrap(true);
        reflectionArea.setWrapStyleWord(true);
        imagePathField = new JTextField();
        dateSpinner = new DateSpinner(LocalDate.now());
        if (existingLog != null) {
            reflectionArea.setText(existingLog.getReflection());
            imagePathField.setText(existingLog.getImagePath());
            dateSpinner.setDate(existingLog.getLogDate());
        }
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        form.add(new JLabel("Reflection"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(new JScrollPane(reflectionArea), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        form.add(new JLabel("Image Path"), gbc);
        JPanel imageRow = new JPanel(new BorderLayout(8, 0));
        imageRow.add(imagePathField, BorderLayout.CENTER);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> browseImage());
        imageRow.add(browseButton, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(imageRow, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        form.add(new JLabel("Log Date"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(dateSpinner, gbc);
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
    private void browseImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            imagePathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    private void save() {
        try {
            String reflection = reflectionArea.getText();
            String imagePath = imagePathField.getText();
            LocalDate date = dateSpinner.getDate();
            if (existingLog == null) {
                travelLogService.addTravelLog(tripId, reflection, imagePath, date);
            } else {
                travelLogService.updateTravelLog(existingLog.getId(), reflection, imagePath, date);
            }
            onSaved.run();
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}