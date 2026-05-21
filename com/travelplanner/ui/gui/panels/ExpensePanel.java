package com.travelplanner.ui.gui.panels;
import com.travelplanner.model.Expense;
import com.travelplanner.model.ExpenseCategory;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.ui.gui.components.BudgetBarChartPanel;
import com.travelplanner.ui.gui.components.CategoryBadgeRenderer;
import com.travelplanner.ui.gui.dialogs.ExpenseFormDialog;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;
import java.util.Map;
public class ExpensePanel extends JPanel {
    private final int tripId;
    private final ExpenseService expenseService;
    private final Runnable onChanged;
    private DefaultTableModel model;
    private JTable table;
    private JLabel totalLabel;
    private BudgetBarChartPanel chartPanel;
    public ExpensePanel(int tripId, ExpenseService expenseService, Runnable onChanged) {
        this.tripId = tripId;
        this.expenseService = expenseService;
        this.onChanged = onChanged;
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        build();
        refresh();
    }
    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(GuiUtil.sectionTitle("Expenses"), BorderLayout.WEST);
        JButton addButton = GuiUtil.primaryButton("+ Add Expense");
        addButton.addActionListener(e -> openAddDialog());
        header.add(addButton, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        JPanel center = new JPanel(new GridLayout(1, 2, 14, 14));
        center.setOpaque(false);
        model = new DefaultTableModel(new Object[]{"ID", "Category", "Description", "Amount", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> Integer.class;
                    case 1 -> ExpenseCategory.class;
                    case 3 -> Double.class;
                    default -> Object.class;
                };
            }
        };
        table = new JTable(model);
        table.setRowHeight(34);
        table.getColumnModel().getColumn(1).setCellRenderer(new CategoryBadgeRenderer());
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        JPanel tableCard = GuiUtil.cardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(tableScroll, BorderLayout.CENTER);
        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableActions.setOpaque(false);
        JButton editButton = new JButton("Edit");
        JButton deleteButton = GuiUtil.dangerButton("Delete");
        editButton.addActionListener(e -> editSelected());
        deleteButton.addActionListener(e -> deleteSelected());
        tableActions.add(editButton);
        tableActions.add(deleteButton);
        tableCard.add(tableActions, BorderLayout.SOUTH);
        JPanel summaryCard = GuiUtil.cardPanel();
        summaryCard.setLayout(new BorderLayout());
        totalLabel = new JLabel("Total: 0.00");
        totalLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 24));
        chartPanel = new BudgetBarChartPanel();
        summaryCard.add(totalLabel, BorderLayout.NORTH);
        summaryCard.add(chartPanel, BorderLayout.CENTER);
        center.add(tableCard);
        center.add(summaryCard);
        add(center, BorderLayout.CENTER);
    }
    public void refresh() {
        model.setRowCount(0);
        List<Expense> expenses;
        try {
            expenses = expenseService.getExpensesByTripId(tripId);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (Expense expense : expenses) {
            model.addRow(new Object[]{
                    expense.getId(),
                    expense.getCategory(),
                    expense.getDescription(),
                    expense.getAmount(),
                    expense.getDate()
            });
        }
        double total = expenseService.getTotalExpensesForTrip(tripId);
        totalLabel.setText(String.format("Total: %.2f", total));
        Map<ExpenseCategory, Double> summary = expenseService.getBudgetSummaryByCategory(tripId);
        chartPanel.setData(summary);
        revalidate();
        repaint();
    }
    private Integer selectedExpenseId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an expense first.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(row);
        return (Integer) model.getValueAt(modelRow, 0);
    }
    private Expense selectedExpense() {
        Integer id = selectedExpenseId();
        if (id == null) {
            return null;
        }
        return expenseService.getExpensesByTripId(tripId)
                .stream()
                .filter(expense -> expense.getId() == id)
                .findFirst()
                .orElse(null);
    }
    private void openAddDialog() {
        ExpenseFormDialog dialog = new ExpenseFormDialog(ownerFrame(), tripId, expenseService, null, this::changed);
        dialog.setVisible(true);
    }
    private void editSelected() {
        Expense expense = selectedExpense();
        if (expense == null) {
            return;
        }
        ExpenseFormDialog dialog = new ExpenseFormDialog(ownerFrame(), tripId, expenseService, expense, this::changed);
        dialog.setVisible(true);
    }
    private void deleteSelected() {
        Integer id = selectedExpenseId();
        if (id == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this expense?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            expenseService.deleteExpense(id);
            changed();
        }
    }
    private void changed() {
        refresh();
        onChanged.run();
    }
    private JFrame ownerFrame() {
        Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        return window instanceof JFrame frame ? frame : null;
    }
}