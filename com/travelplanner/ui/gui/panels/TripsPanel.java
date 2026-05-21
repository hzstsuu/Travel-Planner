package com.travelplanner.ui.gui.panels;
import com.travelplanner.model.Trip;
import com.travelplanner.model.TripStatus;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.service.ItineraryService;
import com.travelplanner.service.TravelLogService;
import com.travelplanner.service.TripService;
import com.travelplanner.ui.gui.dialogs.TripFormDialog;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.time.LocalDate;
import java.util.List;
import java.util.function.IntConsumer;
public class TripsPanel extends JPanel {
    private final TripService tripService;
    private final ItineraryService itineraryService;
    private final ExpenseService expenseService;
    private final TravelLogService travelLogService;
    private final IntConsumer onOpenTrip;
    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    public TripsPanel(
            TripService tripService,
            ItineraryService itineraryService,
            ExpenseService expenseService,
            TravelLogService travelLogService,
            IntConsumer onOpenTrip
    ) {
        this.tripService = tripService;
        this.itineraryService = itineraryService;
        this.expenseService = expenseService;
        this.travelLogService = travelLogService;
        this.onOpenTrip = onOpenTrip;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        build();
    }
    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(GuiUtil.pageTitle("🧳 My Trips"), BorderLayout.WEST);
        JButton newButton = GuiUtil.primaryButton("+ New Trip");
        newButton.setToolTipText("Create a new trip");
        newButton.addActionListener(e -> openCreateDialog());
        header.add(newButton, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tools.setOpaque(false);
        searchField = new JTextField(24);
        searchField.setToolTipText("Search by destination, description, notes, or status");
        filterCombo = new JComboBox<>(new String[]{"All", "Upcoming", "Completed"});
        tools.add(new JLabel("Search"));
        tools.add(searchField);
        tools.add(new JLabel("Filter"));
        tools.add(filterCombo);
        add(tools, BorderLayout.CENTER);
        model = new DefaultTableModel(new Object[]{"ID", "Destination", "Start Date", "End Date", "Status", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : Object.class;
            }
        };
        table = new JTable(model);
        table.setRowHeight(36);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedTrip();
                }
            }
        });
        searchField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                applyFilters();
            }
        });
        filterCombo.addActionListener(e -> applyFilters());
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton openButton = new JButton("Open");
        JButton editButton = new JButton("Edit");
        JButton completeButton = new JButton("Mark Completed");
        JButton deleteButton = GuiUtil.dangerButton("Delete");
        openButton.addActionListener(e -> openSelectedTrip());
        editButton.addActionListener(e -> editSelectedTrip());
        completeButton.addActionListener(e -> completeSelectedTrip());
        deleteButton.addActionListener(e -> deleteSelectedTrip());
        actions.add(openButton);
        actions.add(editButton);
        actions.add(completeButton);
        actions.add(deleteButton);
        tablePanel.add(actions, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.SOUTH);
    }
    public void refresh() {
        model.setRowCount(0);
        List<Trip> trips = tripService.getAllTrips();
        for (Trip trip : trips) {
            model.addRow(new Object[]{
                    trip.getId(),
                    trip.getDestination(),
                    trip.getStartDate(),
                    trip.getEndDate(),
                    trip.getStatus(),
                    trip.getDescription()
            });
        }
        applyFilters();
    }
    private void applyFilters() {
        String search = searchField.getText().trim().toLowerCase();
        String filter = (String) filterCombo.getSelectedItem();
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String destination = String.valueOf(entry.getValue(1)).toLowerCase();
                String start = String.valueOf(entry.getValue(2));
                String end = String.valueOf(entry.getValue(3));
                String status = String.valueOf(entry.getValue(4));
                String description = String.valueOf(entry.getValue(5)).toLowerCase();
                boolean matchesSearch = search.isBlank()
                        || destination.contains(search)
                        || description.contains(search)
                        || status.toLowerCase().contains(search);
                boolean matchesFilter = true;
                if ("Completed".equals(filter)) {
                    matchesFilter = TripStatus.COMPLETED.toString().equals(status);
                } else if ("Upcoming".equals(filter)) {
                    LocalDate endDate = LocalDate.parse(end);
                    matchesFilter = !TripStatus.COMPLETED.toString().equals(status)
                            && !endDate.isBefore(LocalDate.now());
                }
                return matchesSearch && matchesFilter;
            }
        });
    }
    private Integer selectedTripId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a trip first.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(row);
        return (Integer) model.getValueAt(modelRow, 0);
    }
    private void openSelectedTrip() {
        Integer id = selectedTripId();
        if (id != null) {
            onOpenTrip.accept(id);
        }
    }
    private void openCreateDialog() {
        JFrame owner = ownerFrame();
        TripFormDialog dialog = new TripFormDialog(owner, tripService, null, this::refresh);
        dialog.setVisible(true);
    }
    private void editSelectedTrip() {
        Integer id = selectedTripId();
        if (id == null) {
            return;
        }
        Trip trip = tripService.getTripById(id).orElse(null);
        if (trip == null) {
            JOptionPane.showMessageDialog(this, "Trip not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        TripFormDialog dialog = new TripFormDialog(ownerFrame(), tripService, trip, this::refresh);
        dialog.setVisible(true);
    }
    private void completeSelectedTrip() {
        Integer id = selectedTripId();
        if (id == null) {
            return;
        }
        tripService.markTripCompleted(id);
        refresh();
    }
    private void deleteSelectedTrip() {
        Integer id = selectedTripId();
        if (id == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this trip and all related itinerary, expenses, and logs?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        boolean deleted = tripService.deleteTrip(id);
        if (deleted) {
            itineraryService.deleteByTripId(id);
            expenseService.deleteByTripId(id);
            travelLogService.deleteByTripId(id);
            refresh();
        }
    }
    private JFrame ownerFrame() {
        Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        return window instanceof JFrame frame ? frame : null;
    }
    @FunctionalInterface
    private interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update();
        @Override
        default void insertUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }
        @Override
        default void removeUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }
        @Override
        default void changedUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }
    }
}