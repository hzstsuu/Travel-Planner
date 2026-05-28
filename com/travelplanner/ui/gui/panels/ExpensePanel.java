package com.travelplanner.ui.gui.panels;
 
import com.travelplanner.model.Expense;
import com.travelplanner.model.ExpenseCategory;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.ui.gui.components.BudgetBarChartPanel;
import com.travelplanner.ui.gui.components.CategoryBadgeRenderer;
import com.travelplanner.ui.gui.dialogs.ExpenseFormDialog;
import com.travelplanner.ui.gui.util.GuiUtil;
 
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        this.tripId         = tripId;
        this.expenseService = expenseService;
        this.onChanged      = onChanged;
        setOpaque(false);
        setLayout(new BorderLayout(0, 10));
        build();
        refresh();
    }
 
    private void build() {
        // ── Header ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(GuiUtil.sectionTitle("Expenses"), BorderLayout.WEST);
        JButton addBtn = GuiUtil.primaryButton("+ Add Expense");
        addBtn.addActionListener(e -> openAddDialog());
        header.add(addBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
 
        // ── Split pane: table (left 60%) | summary (right 40%) ────────────
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.60);
        split.setDividerSize(6);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setOpaque(false);
 
        // Left: table
        model = new DefaultTableModel(
                new Object[]{"ID", "Category", "Description", "Amount ₱", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return switch (c) {
                    case 0 -> Integer.class;
                    case 1 -> ExpenseCategory.class;
                    case 3 -> Double.class;
                    default -> Object.class;
                };
            }
        };
 
        table = new JTable(model);
        table.setRowHeight(28);
        // FIX: enable both horizontal AND vertical grid lines for clear row/column separation
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(60, 66, 88));          // FIX: visible but subtle grid colour
        table.setIntercellSpacing(new Dimension(1, 1));      // FIX: 1px spacing around cells
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getColumnModel().getColumn(0).setMaxWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setCellRenderer(new CategoryBadgeRenderer());
        table.getColumnModel().getColumn(2).setPreferredWidth(130);  // FIX: explicit desc width
        table.getColumnModel().getColumn(3).setPreferredWidth(80);   // FIX: explicit amount width
        // FIX: right-align the Amount column for readability
        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightAlign);
        table.setAutoCreateRowSorter(true);
 
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(55, 62, 88), 1));
 
        JPanel leftPanel = new JPanel(new BorderLayout(0, 6));
        leftPanel.setOpaque(false);
        leftPanel.add(tableScroll, BorderLayout.CENTER);
 
        // Table action buttons
        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        tableActions.setOpaque(false);
        JButton editBtn   = GuiUtil.ghostButton("✏ Edit");
        JButton deleteBtn = GuiUtil.dangerButton("🗑 Delete");
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        tableActions.add(editBtn);
        tableActions.add(deleteBtn);
        leftPanel.add(tableActions, BorderLayout.SOUTH);
        split.setLeftComponent(leftPanel);
 
        // Right: summary + chart
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
 
        // Total badge
        totalLabel = new JLabel("₱ 0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        totalLabel.setForeground(GuiUtil.WARNING);
 
        JPanel totalCard = new JPanel(new BorderLayout(0, 4));
        totalCard.setOpaque(false);
        totalCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 62, 88), 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        JLabel totalLblHdr = GuiUtil.subLabel("TOTAL SPENT");
        totalCard.add(totalLblHdr, BorderLayout.NORTH);
        totalCard.add(totalLabel,  BorderLayout.CENTER);
        rightPanel.add(totalCard, BorderLayout.NORTH);
 
        // Bar chart — FIX: increased preferred height so amounts are not clipped
        JPanel chartCard = new JPanel(new BorderLayout(0, 6));
        chartCard.setOpaque(false);
        chartCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 62, 88), 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        chartCard.add(GuiUtil.sectionTitle("By Category"), BorderLayout.NORTH);
        chartPanel = new BudgetBarChartPanel();
        chartCard.add(chartPanel, BorderLayout.CENTER);
        rightPanel.add(chartCard, BorderLayout.CENTER);
 
        split.setRightComponent(rightPanel);
        add(split, BorderLayout.CENTER);
    }
 
    public void refresh() {
        model.setRowCount(0);
        List<Expense> expenses;
        try {
            expenses = expenseService.getExpensesByTripId(tripId);
        } catch (IllegalArgumentException e) {
            return;
        }
        for (Expense ex : expenses) {
            model.addRow(new Object[]{
                    ex.getId(), ex.getCategory(),
                    ex.getDescription(), ex.getAmount(), ex.getDate()
            });
        }
        double total = expenseService.getTotalExpensesForTrip(tripId);
        totalLabel.setText(String.format("₱ %.2f", total));
        Map<ExpenseCategory, Double> summary = expenseService.getBudgetSummaryByCategory(tripId);
        chartPanel.setData(summary);
        revalidate(); repaint();
    }
 
    private void openAddDialog() {
        new ExpenseFormDialog(ownerFrame(), tripId, expenseService, null, this::changed).setVisible(true);
    }
 
    private void editSelected() {
        Expense exp = selectedExpense();
        if (exp == null) return;
        new ExpenseFormDialog(ownerFrame(), tripId, expenseService, exp, this::changed).setVisible(true);
    }
 
    private void deleteSelected() {
        Integer id = selectedId();
        if (id == null) return;
        int ok = JOptionPane.showConfirmDialog(this, "Delete this expense?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) { expenseService.deleteExpense(id); changed(); }
    }
 
    private void changed() { refresh(); onChanged.run(); }
 
    private Integer selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an expense first.");
            return null;
        }
        return (Integer) model.getValueAt(table.convertRowIndexToModel(row), 0);
    }
 
    private Expense selectedExpense() {
        Integer id = selectedId();
        if (id == null) return null;
        return expenseService.getExpensesByTripId(tripId).stream()
                .filter(e -> e.getId() == id).findFirst().orElse(null);
    }
 
    private JFrame ownerFrame() {
        Window w = SwingUtilities.getWindowAncestor(this);
        return w instanceof JFrame f ? f : null;
    }
}