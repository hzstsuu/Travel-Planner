package com.travelplanner.ui.gui.dialogs;
import com.travelplanner.model.Expense;
import com.travelplanner.model.ExpenseCategory;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.ui.gui.util.DateSpinner;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
public class ExpenseFormDialog extends JDialog {
    private final int tripId;
    private final ExpenseService expenseService;
    private final Expense existingExpense;
    private final Runnable onSaved;
    private JComboBox<ExpenseCategory> categoryCombo;
    private JTextField descriptionField;
    private JSpinner amountSpinner;
    private DateSpinner dateSpinner;
    public ExpenseFormDialog(
            Frame owner,
            int tripId,
            ExpenseService expenseService,
            Expense existingExpense,
            Runnable onSaved
    ) {
        super(owner, existingExpense == null ? "Add Expense" : "Edit Expense", true);
        this.tripId = tripId;
        this.expenseService = expenseService;
        this.existingExpense = existingExpense;
        this.onSaved = onSaved;
        build(owner);
    }
    private void build(Frame owner) {
        setSize(520, 360);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        JLabel title = GuiUtil.pageTitle(existingExpense == null ? "💳 Add Expense" : "✏ Edit Expense");
        title.setBorder(BorderFactory.createEmptyBorder(18, 22, 8, 22));
        add(title, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        GridBagConstraints gbc = GuiUtil.gbc();
        categoryCombo = new JComboBox<>(ExpenseCategory.values());
        descriptionField = new JTextField();
        amountSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 99999999.0, 10.0));
        dateSpinner = new DateSpinner(LocalDate.now());
        if (existingExpense != null) {
            categoryCombo.setSelectedItem(existingExpense.getCategory());
            descriptionField.setText(existingExpense.getDescription());
            amountSpinner.setValue(existingExpense.getAmount());
            dateSpinner.setDate(existingExpense.getDate());
        }
        addRow(form, gbc, 0, "Category", categoryCombo);
        addRow(form, gbc, 1, "Description", descriptionField);
        addRow(form, gbc, 2, "Amount", amountSpinner);
        addRow(form, gbc, 3, "Date", dateSpinner);
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
            ExpenseCategory category = (ExpenseCategory) categoryCombo.getSelectedItem();
            String description = descriptionField.getText();
            double amount = ((Number) amountSpinner.getValue()).doubleValue();
            LocalDate date = dateSpinner.getDate();
            if (existingExpense == null) {
                expenseService.addExpense(tripId, category, description, amount, date);
            } else {
                expenseService.updateExpense(existingExpense.getId(), category, description, amount, date);
            }
            onSaved.run();
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}