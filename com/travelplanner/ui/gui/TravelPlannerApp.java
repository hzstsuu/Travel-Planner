package com.travelplanner.ui.gui;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.service.ItineraryService;
import com.travelplanner.service.TravelLogService;
import com.travelplanner.service.TripService;
import com.travelplanner.ui.gui.panels.DashboardPanel;
import com.travelplanner.ui.gui.panels.ExpensesPanel;
import com.travelplanner.ui.gui.panels.ItineraryPanel;
import com.travelplanner.ui.gui.panels.LogsPanel;
import com.travelplanner.ui.gui.panels.SettingsPanel;
import com.travelplanner.ui.gui.panels.TripsPanel;
import com.travelplanner.ui.gui.theme.ThemeManager;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
public class TravelPlannerApp extends JFrame {
    public static final String CARD_DASHBOARD = "dashboard";
    public static final String CARD_TRIPS = "trips";
    public static final String CARD_ITINERARY = "itinerary";
    public static final String CARD_EXPENSES = "expenses";
    public static final String CARD_LOGS = "logs";
    public static final String CARD_SETTINGS = "settings";
    private final TripService tripService;
    private final ItineraryService itineraryService;
    private final ExpenseService expenseService;
    private final TravelLogService travelLogService;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private SidebarPanel sidebarPanel;
    private JLabel statusLabel;
    private JLabel clockLabel;
    private DashboardPanel dashboardPanel;
    private TripsPanel tripsPanel;
    private ItineraryPanel itineraryPanel;
    private ExpensesPanel expensesPanel;
    private LogsPanel logsPanel;
    private SettingsPanel settingsPanel;
    public TravelPlannerApp(
            TripService tripService,
            ItineraryService itineraryService,
            ExpenseService expenseService,
            TravelLogService travelLogService
    ) {
        this.tripService = tripService;
        this.itineraryService = itineraryService;
        this.expenseService = expenseService;
        this.travelLogService = travelLogService;
        ThemeManager.installDarkTheme();
        configureFrame();
        buildLayout();
        registerKeyboardShortcuts();
        startClock();
        navigateTo(CARD_DASHBOARD);
    }
    private void configureFrame() {
        setTitle("Travel Planner");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setSize(1360, 850);
        setLocationRelativeTo(null);
    }
    private void buildLayout() {
        setLayout(new BorderLayout());
        sidebarPanel = new SidebarPanel(this::navigateTo);
        add(sidebarPanel, BorderLayout.WEST);
        dashboardPanel = new DashboardPanel(tripService, expenseService, this::openTripDetails);
        tripsPanel = new TripsPanel(tripService, itineraryService, expenseService, travelLogService, this::openTripDetails);
        itineraryPanel = new ItineraryPanel(tripService, itineraryService);
        expensesPanel = new ExpensesPanel(tripService, expenseService);
        logsPanel = new LogsPanel(tripService, travelLogService);
        settingsPanel = new SettingsPanel(this::toggleTheme);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        contentPanel.add(dashboardPanel, CARD_DASHBOARD);
        contentPanel.add(tripsPanel, CARD_TRIPS);
        contentPanel.add(itineraryPanel, CARD_ITINERARY);
        contentPanel.add(expensesPanel, CARD_EXPENSES);
        contentPanel.add(logsPanel, CARD_LOGS);
        contentPanel.add(settingsPanel, CARD_SETTINGS);
        add(contentPanel, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }
    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(70, 70, 80)),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        statusLabel = new JLabel("Ready");
        clockLabel = new JLabel("", SwingConstants.RIGHT);
        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(clockLabel, BorderLayout.EAST);
        return bar;
    }
    public void navigateTo(String cardName) {
        cardLayout.show(contentPanel, cardName);
        sidebarPanel.setActive(cardName);
        switch (cardName) {
            case CARD_DASHBOARD -> {
                dashboardPanel.refresh();
                statusLabel.setText("Dashboard refreshed");
            }
            case CARD_TRIPS -> {
                tripsPanel.refresh();
                statusLabel.setText("Viewing trips");
            }
            case CARD_ITINERARY -> {
                itineraryPanel.refresh();
                statusLabel.setText("Planning itinerary");
            }
            case CARD_EXPENSES -> {
                expensesPanel.refresh();
                statusLabel.setText("Tracking expenses");
            }
            case CARD_LOGS -> {
                logsPanel.refresh();
                statusLabel.setText("Viewing travel diary");
            }
            case CARD_SETTINGS -> statusLabel.setText("Settings");
            default -> statusLabel.setText("Ready");
        }
    }
    private void openTripDetails(int tripId) {
        TripDetailDialog dialog = new TripDetailDialog(
                this,
                tripId,
                tripService,
                itineraryService,
                expenseService,
                travelLogService,
                () -> {
                    dashboardPanel.refresh();
                    tripsPanel.refresh();
                    itineraryPanel.refresh();
                    expensesPanel.refresh();
                    logsPanel.refresh();
                }
        );
        dialog.setVisible(true);
    }
    private void toggleTheme(boolean dark) {
        if (dark) {
            ThemeManager.installDarkTheme();
        } else {
            ThemeManager.installLightTheme();
        }
        GuiUtil.updateComponentTree(this);
        statusLabel.setText(dark ? "Dark theme enabled" : "Light theme enabled");
    }
    private void registerKeyboardShortcuts() {
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_DOWN_MASK), "dashboard");
        getRootPane().getActionMap().put("dashboard", GuiUtil.action(() -> navigateTo(CARD_DASHBOARD)));
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.CTRL_DOWN_MASK), "trips");
        getRootPane().getActionMap().put("trips", GuiUtil.action(() -> navigateTo(CARD_TRIPS)));
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.CTRL_DOWN_MASK), "itinerary");
        getRootPane().getActionMap().put("itinerary", GuiUtil.action(() -> navigateTo(CARD_ITINERARY)));
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_4, KeyEvent.CTRL_DOWN_MASK), "expenses");
        getRootPane().getActionMap().put("expenses", GuiUtil.action(() -> navigateTo(CARD_EXPENSES)));
        getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_5, KeyEvent.CTRL_DOWN_MASK), "logs");
        getRootPane().getActionMap().put("logs", GuiUtil.action(() -> navigateTo(CARD_LOGS)));
    }
    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timer timer = new Timer(1000, e -> clockLabel.setText(LocalTime.now().format(formatter)));
        timer.setInitialDelay(0);
        timer.start();
    }
}